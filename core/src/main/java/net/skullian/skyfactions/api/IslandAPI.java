package net.skullian.skyfactions.api;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import net.skullian.skyfactions.island.IslandModificationAction;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.defence.Defence;
import net.skullian.skyfactions.event.defence.DefencePlacementHandler;
import net.skullian.skyfactions.island.impl.PlayerIsland;
import net.skullian.skyfactions.obelisk.ObeliskHandler;
import net.skullian.skyfactions.util.ErrorUtil;
import net.skullian.skyfactions.util.PlayerUtil;
import net.skullian.skyfactions.util.SoundUtil;

public class IslandAPI {

    public static Map<UUID, PlayerIsland> islands = new ConcurrentHashMap<>();
    public static List<UUID> awaitingDeletion = new ArrayList<>();

    public static CompletableFuture<PlayerIsland> getPlayerIsland(UUID playerUUID) {
        if (islands.containsKey(playerUUID)) return CompletableFuture.completedFuture(islands.get(playerUUID));
        return SkyFactionsReborn.getDatabaseManager().getPlayerIslandManager().getPlayerIsland(playerUUID).thenApply((island) -> {
            islands.put(playerUUID, island);

            return island;
        });
    }

    public static CompletableFuture<Void> createIsland(Player player) {

        PlayerIsland island = new PlayerIsland(SkyFactionsReborn.getDatabaseManager().getPlayerIslandManager().cachedPlayerIslandID);
        SkyFactionsReborn.getDatabaseManager().getPlayerIslandManager().cachedPlayerIslandID++;

        World world = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
        if (world == null) {
            IllegalArgumentException gasp = new IllegalArgumentException("Unknown World: " + Settings.ISLAND_PLAYER_WORLD.getString());
            ErrorUtil.handleError(player, "create an island", "WORLD_NOT_EXIST", gasp);
            throw new IllegalArgumentException(gasp);
        }

        Messages.ISLAND_CREATING.send(player, PlayerAPI.getLocale(player.getUniqueId()));
        RegionAPI.createRegion(player, island.getPosition1(world), island.getPosition2(world), world, "sfr_player_" + player.getUniqueId());

        IslandModificationAction action = IslandModificationAction.CREATE;
        action.setId(island.getId());

        return CompletableFuture.allOf(
                RegionAPI.pasteIslandSchematic(player, island.getCenter(world), world.getName(), "player"),
                SkyFactionsReborn.getDatabaseManager().getPlayerIslandManager().createIsland(player.getUniqueId(), action)
        ).whenComplete((ignored, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "create your island", "SQL_ISLAND_CREATE", ex);
                removePlayerIsland(player);
                SkyFactionsReborn.getDatabaseManager().getPlayerIslandManager().removeIsland(player);
                return;
            }

            islands.put(player.getUniqueId(), island);
            RunesAPI.playerRunes.put(player.getUniqueId(), 0);
            GemsAPI.playerGems.put(player.getUniqueId(), 0);

            RegionAPI.modifyWorldBorder(player, island.getCenter(world), island.getSize()); // shift join border
            RegionAPI.teleportPlayerToLocation(player, island.getCenter(world));

            ObeliskHandler.spawnPlayerObelisk(player, island);
            Messages.ISLAND_CREATED.send(player, PlayerAPI.getLocale(player.getUniqueId()));
            SoundUtil.playSound(player, Settings.SOUNDS_ISLAND_CREATE_SUCCESS.getString(), Settings.SOUNDS_ISLAND_CREATE_SUCCESS_PITCH.getInt(), 1f);
        });
    }

    public static CompletableFuture<Boolean> hasIsland(UUID playerUUID) {
        return SkyFactionsReborn.getDatabaseManager().getPlayerIslandManager().hasIsland(playerUUID);
    }

    public static void onIslandRemove(Player player) {
        RegionAPI.teleportPlayerToLocation(player, RegionAPI.getHubLocation());
        SkyFactionsReborn.getWorldBorderApi().resetBorder(player);

        // reset runes and gems.

        SkyFactionsReborn.getCacheService().getEntry(player.getUniqueId()).onIslandRemove();
        RunesAPI.playerRunes.remove(player.getUniqueId());
        GemsAPI.playerGems.remove(player.getUniqueId());

        removePlayerIsland(player);
        awaitingDeletion.remove(player.getUniqueId());
    }

    public static void removePlayerIsland(Player player) {
        getPlayerIsland(player.getUniqueId()).whenComplete((island, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your island", "SQL_ISLAND_GET", ex);
                return;
            }

            World world = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
            if (world != null) {
                BlockVector3 bottom = BukkitAdapter.asBlockVector(island.getPosition1(null));
                BlockVector3 top = BukkitAdapter.asBlockVector(island.getPosition2(null));

                Region region = new CuboidRegion(BukkitAdapter.adapt(world), bottom, top);

                CompletableFuture.allOf(
                        RegionAPI.cutRegion(region),
                        RegionAPI.removeRegion("sfr_player_" + player.getUniqueId(), world),
                        SkyFactionsReborn.getDatabaseManager().getPlayerIslandManager().removeIsland(player),
                        SkyFactionsReborn.getDatabaseManager().getPlayerIslandManager().removeAllTrustedPlayers(island.getId()),
                        SkyFactionsReborn.getDatabaseManager().getDefencesManager().removeAllDefences(player.getUniqueId().toString(), false)
                ).whenComplete((ignored, throwable) -> {
                    if (throwable != null) {
                        throwable.printStackTrace();
                        return;
                    }

                    PlayerUtil.clearInventory(player);
                    PlayerUtil.clearEnderChest(player);

                    Messages.ISLAND_DELETION_SUCCESS.send(player, PlayerAPI.getLocale(player.getUniqueId()));
                });
            } else {
                Messages.ERROR.send(player, PlayerAPI.getLocale(player.getUniqueId()), "operation", "delete your island", "debug", "WORLD_NOT_EXIST");
            }
        });
    }

    public static void onIslandLoad(UUID playerUUID) {
        modifyDefenceOperation(FactionAPI.DefenceOperation.ENABLE, playerUUID);

        getPlayerIsland(playerUUID).whenComplete((island, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }

            SkyFactionsReborn.getNpcManager().spawnNPC(playerUUID, island);
        });
    }

    public static void modifyDefenceOperation(FactionAPI.DefenceOperation operation, UUID playerUUID) {
        if (operation == FactionAPI.DefenceOperation.DISABLE && !RegionAPI.isLocationInRegion(Bukkit.getPlayer(playerUUID).getLocation(), "sfr_player_" + playerUUID.toString())) return;

        List<Defence> defences = DefencePlacementHandler.loadedPlayerDefences.get(playerUUID);
        if (defences == null || defences.isEmpty()) return;

        for (Defence defence : defences) {
            if (operation == FactionAPI.DefenceOperation.ENABLE) {
                defence.onLoad(playerUUID.toString());
            } else {
                defence.disable();
            }
        }
    }
}
