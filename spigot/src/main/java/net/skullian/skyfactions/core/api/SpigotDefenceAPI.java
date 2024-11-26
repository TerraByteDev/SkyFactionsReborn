package net.skullian.skyfactions.core.api;

import com.jeff_media.customblockdata.CustomBlockData;
import net.kyori.adventure.text.Component;
import net.skullian.skyfactions.common.api.DefenceAPI;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.util.text.TextUtility;
import net.skullian.skyfactions.core.SkyFactionsReborn;
import net.skullian.skyfactions.common.defence.Defence;
import net.skullian.skyfactions.core.defence.DefencesFactory;
import net.skullian.skyfactions.common.defence.struct.DefenceData;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;
import net.skullian.skyfactions.core.event.defence.DefencePlacementHandler;
import net.skullian.skyfactions.common.faction.Faction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpigotDefenceAPI extends DefenceAPI {

    @Override
    public @NotNull ItemStack createDefenceStack(DefenceStruct defence, Player player) {
        ItemStack stack = SpigotPlayerAPI.convertToSkull(new ItemStack(Material.getMaterial(defence.getITEM_MATERIAL())), defence.getITEM_SKULL());
        NamespacedKey nameKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-identifier");

        ItemMeta meta = stack.getItemMeta();
        meta.displayName(TextUtility.color(defence.getNAME(), SpigotPlayerAPI.getLocale(player.getUniqueId()), player));
        meta.getPersistentDataContainer().set(nameKey, PersistentDataType.STRING, defence.getIDENTIFIER());

        meta.lore(getFormattedLore(defence, defence.getITEM_LORE(), player));
        stack.setItemMeta(meta);

        return stack;
    }

    @Override
    public boolean isDefence(ItemStack item) {
        NamespacedKey defenceKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-identifier");
        return item.getItemMeta().getPersistentDataContainer().has(defenceKey, PersistentDataType.STRING);
    }

    @Override
    public boolean isDefence(Location location) {
        Block block = location.getBlock();
        NamespacedKey defenceKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-identifier");

        PersistentDataContainer container = new CustomBlockData(block, SkyFactionsReborn.getInstance());
        if (container.has(defenceKey, PersistentDataType.STRING)) {
            return true;
        }

        return false;
    }

    @Override
    public @NotNull List<Component> getFormattedLore(DefenceStruct struct, List<String> lore, Player player) {
        String maxLevel = String.valueOf(struct.getMAX_LEVEL());
        String range = DefencesFactory.solveFormula(struct.getATTRIBUTES().getRANGE(), 1);
        String ammo = DefencesFactory.solveFormula(struct.getATTRIBUTES().getMAX_AMMO(), 1);
        String targetMax = DefencesFactory.solveFormula(struct.getATTRIBUTES().getMAX_TARGETS(), 1);
        String damage = DefencesFactory.solveFormula(struct.getATTRIBUTES().getDAMAGE(), 1);
        String cooldown = DefencesFactory.solveFormula(struct.getATTRIBUTES().getCOOLDOWN(), 1);
        String healing = DefencesFactory.solveFormula(struct.getATTRIBUTES().getHEALING(), 1);
        String distance = DefencesFactory.solveFormula(struct.getATTRIBUTES().getDISTANCE(), 1);
        List<Component> newLore = new ArrayList<>();

        for (String str : lore) {
            newLore.add(TextUtility.color(str
                    .replace("<max_level>", maxLevel)
                    .replace("<range>", range)
                    .replace("<ammo>", ammo)
                    .replace("<target_max>", targetMax)
                    .replace("<damage>", damage)
                    .replace("<cooldown>", cooldown)
                    .replace("<healing>", healing)
                    .replace("<distance>", distance)
                    .replace("<cost>", String.valueOf(struct.getBUY_COST())), SpigotPlayerAPI.getLocale(player.getUniqueId()), player));
        }

        return newLore;
    }

    @Override
    public Defence getDefenceFromData(DefenceData data) {
        return getLoadedDefence(
                new Location(Bukkit.getWorld(data.getWORLD_LOC()), data.getX(), data.getY(), data.getZ())
        );
    }

    @Override
    public Defence getLoadedDefence(Location location) {
        return DefencePlacementHandler.loadedFactionDefences.values().stream()
                .flatMap(List::stream)
                .filter(d -> d.getDefenceLocation().equals(location))
                .findFirst()
                .orElseGet(() -> DefencePlacementHandler.loadedPlayerDefences.values().stream()
                        .flatMap(List::stream)
                        .filter(d -> d.getDefenceLocation().equals(location))
                        .findFirst()
                        .orElse(null));
    }

    @Override
    public void returnDefence(DefenceStruct struct, Player player) {
        ItemStack stack = createDefenceStack(struct, player);
        player.getInventory().addItem(stack);
    }

    @Override
    public boolean hasPermissions(List<String> permissions, Player player, Faction faction) {
        return permissions.contains(faction.getRankType(player.getUniqueId()).getRankValue());
    }

    @Override
    public @NotNull ItemBuilder processPermissions(ItemBuilder builder, Player player) {
        String locale = SpigotPlayerAPI.getLocale(player.getUniqueId());

        for (String line : Messages.DEFENCE_INSUFFICIENT_PERMISSIONS_LORE.getStringList(locale)) {
            builder.addLoreLines(TextUtility.legacyColor(line, locale, player));
        }

        return builder;
    }

    @Override
    public boolean isDefenceMaterial(Block block) {
        return DefencesFactory.defences.values().stream()
                .flatMap(inner -> inner.values().stream())
                .anyMatch(struct -> struct.getBLOCK_MATERIAL().equals(block.getType().name()));
    }

    @Override
    public DefenceStruct getDefenceFromItem(ItemStack itemStack, Player player) {
        NamespacedKey defenceKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-identifier");
        PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();

        if (container.has(defenceKey, PersistentDataType.STRING)) {
            String identifier = container.get(defenceKey, PersistentDataType.STRING);
            DefenceStruct struct = DefencesFactory.defences.getOrDefault(SpigotPlayerAPI.getLocale(player.getUniqueId()), DefencesFactory.getDefaultStruct()).get(identifier);

            return struct;
        }

        return null;
    }

    @Override
    public boolean isFaction(String uuid) {
        try {
            UUID.fromString(uuid);
            return false; // is player uuid
        } catch (Exception ignored) {
            return true; // is false
        }
    }
}
