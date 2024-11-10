package net.skullian.skyfactions.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import net.skullian.skyfactions.event.PlayerHandler;
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
import net.skullian.skyfactions.island.PlayerIsland;
import net.skullian.skyfactions.obelisk.ObeliskHandler;
import net.skullian.skyfactions.util.ErrorUtil;
import net.skullian.skyfactions.util.PlayerUtil;
import net.skullian.skyfactions.util.SoundUtil;

public class IslandAPI {

    public static Map<UUID, PlayerIsland> islands = new HashMap<>();
    public static HashSet<UUID> awaitingDeletion = new HashSet<>();

    public static CompletableFuture<PlayerIsland> getPlayerIsland(UUID playerUUID) {
        if (islands.containsKey(playerUUID)) return CompletableFuture.completedFuture(islands.get(playerUUID));
        return SkyFactionsReborn.databaseManager.playerIslandManager.getPlayerIsland(playerUUID).thenApply((island) -> {
            islands.put(playerUUID, island);

            return island;
        });
    }

    // for PlaceholderAPI
    public static void cacheData(Player player) {
        if (!GemsAPI.playerGems.containsKey(player.getUniqueId())) GemsAPI.cachePlayer(player.getUniqueId());
        if (!RunesAPI.playerRunes.containsKey(player.getUniqueId())) RunesAPI.cachePlayer(player.getUniqueId());
        if (!FactionAPI.factionCache.containsKey(player.getUniqueId())) FactionAPI.getFaction(player.getUniqueId());
    }

    public static void createIsland(Player player) {

        PlayerIsland island = new PlayerIsland(SkyFactionsReborn.databaseManager.playerIslandManager.cachedPlayerIslandID);
        SkyFactionsReborn.databaseManager.playerIslandManager.cachedPlayerIslandID++;

        World world = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
        if (world == null) {
            ErrorUtil.handleError(player, "create an island", "WORLD_NOT_EXIST", new IllegalArgumentException("Unknown World: " + Settings.ISLAND_PLAYER_WORLD.getString()));
            return;
        }

        Messages.ISLAND_CREATING.send(player, PlayerHandler.getLocale(player.getUniqueId()));
        RegionAPI.createRegion(player, island.getPosition1(world), island.getPosition2(world), world, player.getUniqueId().toString());

        CompletableFuture.allOf(
                SkyFactionsReborn.databaseManager.playerIslandManager.createIsland(player, island),
                RegionAPI.pasteIslandSchematic(player, island.getCenter(world), world.getName(), "player")
        ).whenComplete((ignored, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "create your island", "SQL_ISLAND_CREATE", ex);
                removePlayerIsland(player);
                SkyFactionsReborn.databaseManager.playerIslandManager.removeIsland(player);
                return;
            }

            islands.put(player.getUniqueId(), island);

            RegionAPI.modifyWorldBorder(player, island.getCenter(world), island.getSize()); // shift join border
            RegionAPI.teleportPlayerToLocation(player, island.getCenter(world));

            ObeliskHandler.spawnPlayerObelisk(player, island);
            Messages.ISLAND_CREATED.send(player, PlayerHandler.getLocale(player.getUniqueId()));
            SoundUtil.playSound(player, Settings.SOUNDS_ISLAND_CREATE_SUCCESS.getString(), Settings.SOUNDS_ISLAND_CREATE_SUCCESS_PITCH.getInt(), 1f);
        });
    }

    public static CompletableFuture<Boolean> hasIsland(UUID playerUUID) {
        return SkyFactionsReborn.databaseManager.playerIslandManager.hasIsland(playerUUID);
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
                        SkyFactionsReborn.databaseManager.playerIslandManager.removeIsland(player),
                        SkyFactionsReborn.databaseManager.playerIslandManager.removeAllTrustedPlayers(island.getId()),
                        SkyFactionsReborn.databaseManager.defencesManager.removeAllDefences(player.getUniqueId().toString(), false)
                ).whenComplete((ignored, throwable) -> {
                    if (throwable != null) {
                        throwable.printStackTrace();
                        return;
                    }

                    PlayerUtil.clearInventory(player);
                    PlayerUtil.clearEnderChest(player);

                    Messages.ISLAND_DELETION_SUCCESS.send(player, PlayerHandler.getLocale(player.getUniqueId()));
                });
            } else {
                Messages.ERROR.send(player, PlayerHandler.getLocale(player.getUniqueId()), "operation", "delete your island", "debug", "WORLD_NOT_EXIST");
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

            SkyFactionsReborn.npcManager.spawnNPC(playerUUID, island);
        });
    }

    public static void modifyDefenceOperation(FactionAPI.DefenceOperation operation, UUID playerUUID) {
        if (operation == FactionAPI.DefenceOperation.DISABLE && !RegionAPI.isLocationInRegion(Bukkit.getPlayer(playerUUID).getLocation(), playerUUID.toString())) return;

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
