package net.skullian.skyfactions.core.api;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import net.skullian.skyfactions.common.api.IslandAPI;
import net.skullian.skyfactions.core.SkyFactionsReborn;
import net.skullian.skyfactions.common.defence.Defence;
import net.skullian.skyfactions.core.event.defence.DefencePlacementHandler;
import net.skullian.skyfactions.core.island.IslandModificationAction;
import net.skullian.skyfactions.core.island.impl.PlayerIsland;
import net.skullian.skyfactions.core.obelisk.ObeliskHandler;
import net.skullian.skyfactions.core.util.ErrorUtil;
import net.skullian.skyfactions.core.util.PlayerUtil;
import net.skullian.skyfactions.core.util.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SpigotIslandAPI extends IslandAPI {

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

        Messages.ISLAND_CREATING.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
        SpigotRegionAPI.createRegion(player, island.getPosition1(world), island.getPosition2(world), world, "sfr_player_" + player.getUniqueId());

        IslandModificationAction action = IslandModificationAction.CREATE;
        action.setId(island.getId());

        return CompletableFuture.allOf(
                SpigotRegionAPI.pasteIslandSchematic(player, island.getCenter(world), world.getName(), "player"),
                SkyFactionsReborn.getDatabaseManager().getPlayerIslandManager().createIsland(player.getUniqueId(), action)
        ).whenComplete((ignored, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "create your island", "SQL_ISLAND_CREATE", ex);
                removePlayerIsland(player);
                SkyFactionsReborn.getDatabaseManager().getPlayerIslandManager().removeIsland(player);
                return;
            }

            islands.put(player.getUniqueId(), island);
            SpigotRunesAPI.playerRunes.put(player.getUniqueId(), 0);
            SpigotGemsAPI.playerGems.put(player.getUniqueId(), 0);

            SpigotRegionAPI.modifyWorldBorder(player, island.getCenter(world), island.getSize()); // shift join border
            SpigotRegionAPI.teleportPlayerToLocation(player, island.getCenter(world));

            ObeliskHandler.spawnPlayerObelisk(player, island);
            Messages.ISLAND_CREATED.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
            SoundUtil.playSound(player, Settings.SOUNDS_ISLAND_CREATE_SUCCESS.getString(), Settings.SOUNDS_ISLAND_CREATE_SUCCESS_PITCH.getInt(), 1f);
        });
    }

    public static CompletableFuture<Boolean> hasIsland(UUID playerUUID) {
        return SkyFactionsReborn.getDatabaseManager().getPlayerIslandManager().hasIsland(playerUUID);
    }

    public static void onIslandRemove(Player player) {
        SpigotRegionAPI.teleportPlayerToLocation(player, SpigotRegionAPI.getHubLocation());
        SkyFactionsReborn.getWorldBorderApi().resetBorder(player);

        // reset runes and gems.

        SkyFactionsReborn.getCacheService().getEntry(player.getUniqueId()).onIslandRemove();
        SpigotRunesAPI.playerRunes.remove(player.getUniqueId());
        SpigotGemsAPI.playerGems.remove(player.getUniqueId());

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
                        SpigotRegionAPI.cutRegion(region),
                        SpigotRegionAPI.removeRegion("sfr_player_" + player.getUniqueId(), world),
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

                    Messages.ISLAND_DELETION_SUCCESS.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
                });
            } else {
                Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "delete your island", "debug", "WORLD_NOT_EXIST");
            }
        });
    }

    public static void onIslandLoad(UUID playerUUID) {
        modifyDefenceOperation(SpigotFactionAPI.DefenceOperation.ENABLE, playerUUID);

        getPlayerIsland(playerUUID).whenComplete((island, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }

            SkyFactionsReborn.getNpcManager().spawnNPC(playerUUID, island);
        });
    }

    public static void modifyDefenceOperation(SpigotFactionAPI.DefenceOperation operation, UUID playerUUID) {
        if (operation == SpigotFactionAPI.DefenceOperation.DISABLE && !SpigotRegionAPI.isLocationInRegion(Bukkit.getPlayer(playerUUID).getLocation(), "sfr_player_" + playerUUID.toString())) return;

        List<Defence> defences = DefencePlacementHandler.loadedPlayerDefences.get(playerUUID);
        if (defences == null || defences.isEmpty()) return;

        for (Defence defence : defences) {
            if (operation == SpigotFactionAPI.DefenceOperation.ENABLE) {
                defence.onLoad(playerUUID.toString());
            } else {
                defence.disable();
            }
        }
    }
}
