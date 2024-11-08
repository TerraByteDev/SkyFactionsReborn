package net.skullian.skyfactions.api;

import java.util.ArrayList;
import java.util.List;

import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.defence.struct.DefenceData;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.faction.Faction;
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

import com.jeff_media.customblockdata.CustomBlockData;

import net.kyori.adventure.text.Component;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.defence.Defence;
import net.skullian.skyfactions.defence.DefencesFactory;
import net.skullian.skyfactions.defence.struct.DefenceStruct;
import net.skullian.skyfactions.event.DefenceHandler;
import net.skullian.skyfactions.util.text.TextUtility;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public class DefenceAPI {

    /**
     * Get the itemstack of a defence.
     *
     * @param defence Struct of the defence.
     * @return
     */
    public static ItemStack createDefenceStack(DefenceStruct defence, Player player) {
        ItemStack stack = SkullAPI.convertToSkull(new ItemStack(Material.getMaterial(defence.getITEM_MATERIAL())), defence.getITEM_SKULL());
        NamespacedKey nameKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-identifier");

        ItemMeta meta = stack.getItemMeta();
        meta.displayName(TextUtility.color(defence.getNAME(), PlayerHandler.getLocale(player.getUniqueId()), player));
        meta.getPersistentDataContainer().set(nameKey, PersistentDataType.STRING, defence.getIDENTIFIER());

        meta.lore(getFormattedLore(defence, defence.getITEM_LORE(), player));
        stack.setItemMeta(meta);

        return stack;
    }

    public static boolean isDefence(ItemStack item) {
        NamespacedKey defenceKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-identifier");
        return item.getItemMeta().getPersistentDataContainer().has(defenceKey, PersistentDataType.STRING);
    }

    public static boolean isDefence(Location location) {
        Block block = location.getBlock();
        NamespacedKey defenceKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-identifier");

        PersistentDataContainer container = new CustomBlockData(block, SkyFactionsReborn.getInstance());
        if (container.has(defenceKey, PersistentDataType.STRING)) {
            return true;
        }

        return false;
    }

    private static List<Component> getFormattedLore(DefenceStruct struct, List<String> lore, Player player) {
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
                    .replace("max_level", maxLevel)
                    .replace("range", range)
                    .replace("ammo", ammo)
                    .replace("target_max", targetMax)
                    .replace("damage", damage)
                    .replace("cooldown", cooldown)
                    .replace("healing", healing)
                    .replace("distance", distance)
                    .replace("cost", String.valueOf(struct.getBUY_COST())), PlayerHandler.getLocale(player.getUniqueId()), player));
        }

        return newLore;
    }

    public static Defence getDefenceFromData(DefenceData data) {
        return getLoadedDefence(
                new Location(Bukkit.getWorld(data.getWORLD_LOC()), data.getX(), data.getY(), data.getZ())
        );
    }

    public static Defence getLoadedDefence(Location location) {
        return DefenceHandler.loadedFactionDefences.values().stream()
                .flatMap(List::stream)
                .filter(d -> d.getDefenceLocation().equals(location))
                .findFirst()
                .orElseGet(() -> DefenceHandler.loadedPlayerDefences.values().stream()
                        .flatMap(List::stream)
                        .filter(d -> d.getDefenceLocation().equals(location))
                        .findFirst()
                        .orElse(null));
    }

    public static void returnDefence(DefenceStruct struct, Player player) {
        ItemStack stack = DefenceAPI.createDefenceStack(struct, player);
        player.getInventory().addItem(stack);
    }

    public static boolean hasPermissions(List<String> permissions, Player player, Faction faction) {
        return permissions.contains(faction.getRankType(player.getUniqueId()).getRankValue());
    }

    public static ItemBuilder processPermissions(ItemBuilder builder, Player player) {
        String locale = PlayerHandler.getLocale(player.getUniqueId());

        for (String line : Messages.DEFENCE_INSUFFICIENT_PERMISSIONS_LORE.getStringList()) {
            builder.addLoreLines(TextUtility.legacyColor(line, locale, player));
        }

        return builder;
    }
}
