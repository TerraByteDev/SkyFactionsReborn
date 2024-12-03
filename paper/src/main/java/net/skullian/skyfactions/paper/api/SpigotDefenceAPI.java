package net.skullian.skyfactions.paper.api;

import com.jeff_media.customblockdata.CustomBlockData;
import net.skullian.skyfactions.common.api.DefenceAPI;
import net.skullian.skyfactions.common.api.PlayerAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.faction.AuditLogType;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;
import net.skullian.skyfactions.common.util.SkyLocation;
import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.common.defence.Defence;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import net.skullian.skyfactions.common.defence.struct.DefenceData;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;
import net.skullian.skyfactions.paper.event.defence.DefencePlacementHandler;
import net.skullian.skyfactions.common.faction.Faction;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpigotDefenceAPI extends DefenceAPI {

    @Override
    public @NotNull SkyItemStack createDefenceStack(DefenceStruct defence, SkyUser player) {
        SkyItemStack.SkyItemStackBuilder stack = PlayerAPI.convertToSkull(SkyItemStack.builder().material(defence.getITEM_MATERIAL()), defence.getITEM_SKULL());
        stack.displayName(defence.getNAME());
        stack.lore(getFormattedLore(defence, defence.getITEM_LORE(), player));
        stack.persistentData(new SkyItemStack.PersistentData("defence-identifier", "STRING", defence.getIDENTIFIER()));

        return stack.build();
    }

    @Override
    public void addDefence(SkyUser player, DefenceStruct defence, Faction faction) {
        SkyItemStack stack = createDefenceStack(defence, player);
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

        if (faction != null) {
            // assumes the type is faction
            faction.subtractRunes(defence.getBUY_COST());
            player.addItem(stack);

            SkyApi.getInstance().getSoundAPI().playSound(player, Settings.DEFENCE_PURCHASE_SUCCESS_SOUND.getString(), Settings.DEFENCE_PURCHASE_SUCCESS_SOUND_PITCH.getInt(), 1f);
            Messages.DEFENCE_PURCHASE_SUCCESS.send(player, locale, "defence_name", defence.getNAME());

            faction.createAuditLog(player.getUniqueId(), AuditLogType.DEFENCE_PURCHASE, "player_name", player.getName(), "defence_name", defence.getNAME());
        } else {
            SkyApi.getInstance().getRunesAPI().removeRunes(player.getUniqueId(), defence.getBUY_COST());
            player.addItem(stack);

            SkyApi.getInstance().getSoundAPI().playSound(player, Settings.DEFENCE_PURCHASE_SUCCESS_SOUND.getString(), Settings.DEFENCE_PURCHASE_SUCCESS_SOUND_PITCH.getInt(), 1f);
            Messages.DEFENCE_PURCHASE_SUCCESS.send(player, locale, "defence_name", defence.getNAME());
        }
    }

    @Override
    public boolean isDefence(SkyItemStack item) {
        return item.hasPersistentData("defence-identifier");
    }

    @Override
    public boolean isDefence(SkyLocation location) {
        Block block = SpigotAdapter.adapt(location).getBlock();
        NamespacedKey defenceKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-identifier");

        PersistentDataContainer container = new CustomBlockData(block, SkyFactionsReborn.getInstance());
        return container.has(defenceKey, PersistentDataType.STRING);
    }

    @Override
    public @NotNull List<String> getFormattedLore(DefenceStruct struct, List<String> lore, SkyUser player) {
        String maxLevel = String.valueOf(struct.getMAX_LEVEL());
        String range = DefenceAPI.solveFormula(struct.getATTRIBUTES().getRANGE(), 1);
        String ammo = DefenceAPI.solveFormula(struct.getATTRIBUTES().getMAX_AMMO(), 1);
        String targetMax = DefenceAPI.solveFormula(struct.getATTRIBUTES().getMAX_TARGETS(), 1);
        String damage = DefenceAPI.solveFormula(struct.getATTRIBUTES().getDAMAGE(), 1);
        String cooldown = DefenceAPI.solveFormula(struct.getATTRIBUTES().getCOOLDOWN(), 1);
        String healing = DefenceAPI.solveFormula(struct.getATTRIBUTES().getHEALING(), 1);
        String distance = DefenceAPI.solveFormula(struct.getATTRIBUTES().getDISTANCE(), 1);
        List<String> newLore = new ArrayList<>();

        for (String str : lore) {
            newLore.add(str
                    .replace("<max_level>", maxLevel)
                    .replace("<range>", range)
                    .replace("<ammo>", ammo)
                    .replace("<target_max>", targetMax)
                    .replace("<damage>", damage)
                    .replace("<cooldown>", cooldown)
                    .replace("<healing>", healing)
                    .replace("<distance>", distance)
                    .replace("<cost>", String.valueOf(struct.getBUY_COST())));
        }

        return newLore;
    }

    @Override
    public Defence getDefenceFromData(DefenceData data) {
        return getLoadedDefence(
                new SkyLocation(data.getWORLD_LOC(), data.getX(), data.getY(), data.getZ())
        );
    }

    @Override
    public Defence getLoadedDefence(SkyLocation location) {
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
    public void returnDefence(DefenceStruct struct, SkyUser player) {
        player.addItem(createDefenceStack(struct, player));
    }

    @Override
    public boolean hasPermissions(List<String> permissions, SkyUser player, Faction faction) {
        return permissions.contains(faction.getRankType(player.getUniqueId()).getRankValue());
    }

    @Override
    public @NotNull SkyItemStack.SkyItemStackBuilder processPermissions(SkyItemStack.SkyItemStackBuilder builder, SkyUser player) {
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

        for (String line : Messages.DEFENCE_INSUFFICIENT_PERMISSIONS_LORE.getStringList(locale)) {
            builder.loreLine(line);
        }

        return builder;
    }

    @Override
    public boolean isDefenceMaterial(SkyLocation location) {
        Block bukkitBlock = SpigotAdapter.adapt(location).getBlock();
        return SkyApi.getInstance().getDefenceFactory().getDefences().values().stream()
                .flatMap(inner -> inner.values().stream())
                .anyMatch(struct -> struct.getBLOCK_MATERIAL().equals(bukkitBlock.getType().name()));
    }

    @Override
    public DefenceStruct getDefenceFromItem(SkyItemStack itemStack, SkyUser player) {
        if (itemStack.hasPersistentData("defence-identifier")) {
            String identifier = itemStack.getPersistentData("defence-identifier").getData().toString();

            return SkyApi.getInstance().getDefenceFactory().getDefences().getOrDefault(SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), SkyApi.getInstance().getDefenceFactory().getDefaultStruct()).get(identifier);
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
