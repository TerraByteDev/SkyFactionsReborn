package net.skullian.skyfactions.common.api;

import lombok.Getter;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.defence.Defence;
import net.skullian.skyfactions.common.island.IslandModificationAction;
import net.skullian.skyfactions.common.island.SkyIsland;
import net.skullian.skyfactions.common.island.impl.PlayerIsland;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import net.skullian.skyfactions.common.util.SLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public abstract class IslandAPI {

    private final Map<UUID, PlayerIsland> islands;
    private final List<UUID> awaitingDeletion;

    public IslandAPI() {
        this.islands = new ConcurrentHashMap<>();
        this.awaitingDeletion = new ArrayList<>();
    }

    public CompletableFuture<PlayerIsland> getPlayerIsland(UUID playerUUID) {
        if (islands.containsKey(playerUUID)) return CompletableFuture.completedFuture(islands.get(playerUUID));
        return SkyApi.getInstance().getDatabaseManager().getPlayerIslandManager().getPlayerIsland(playerUUID).thenApply((island) -> {
            if (island != null) islands.put(playerUUID, island);

            return island;
        });
    }

    public CompletableFuture<Void> createIsland(SkyUser player) {
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

        PlayerIsland island = new PlayerIsland(SkyApi.getInstance().getDatabaseManager().getPlayerIslandManager().cachedPlayerIslandID);
        SkyApi.getInstance().getDatabaseManager().getPlayerIslandManager().cachedPlayerIslandID++;

        String worldName = Settings.ISLAND_PLAYER_WORLD.getString();
        if (!SkyApi.getInstance().getRegionAPI().worldExists(worldName)) {
            IllegalArgumentException err = new IllegalArgumentException("Unknown World: " + worldName);
            ErrorUtil.handleError(player, "create an island", "WORLD_NOT_EXIST", err);
            throw new IllegalArgumentException(err);
        }

        Messages.ISLAND_CREATING.send(player, locale);
        SkyApi.getInstance().getRegionAPI().createRegion(player, island.getPosition1(worldName), island.getPosition2(worldName), worldName, "sfr_player_" + player.getUniqueId());

        IslandModificationAction action = IslandModificationAction.CREATE;
        action.setId(island.getId());

        return CompletableFuture.allOf(
                SkyApi.getInstance().getRegionAPI().pasteIslandSchematic(player, island.getCenter(worldName), worldName, "player"),
                SkyApi.getInstance().getDatabaseManager().getPlayerIslandManager().createIsland(player.getUniqueId(), action)
        ).whenComplete((ignored, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "create your island", "SQL_ISLAND_CREATE", ex);
                removePlayerIsland(player);
                SkyApi.getInstance().getDatabaseManager().getPlayerIslandManager().removeIsland(player);
                return;
            }

            islands.put(player.getUniqueId(), island);
            SkyApi.getInstance().getRegionAPI().modifyWorldBorder(player, island.getCenter(worldName), island.getSize());
            player.teleport(island.getCenter(worldName));

            SkyApi.getInstance().getObeliskAPI().spawnPlayerObelisk(player, island);
            Messages.ISLAND_CREATED.send(player, locale);
            SkyApi.getInstance().getSoundAPI().playSound(player, Settings.SOUNDS_ISLAND_CREATE_SUCCESS.getString(), Settings.SOUNDS_ISLAND_CREATE_SUCCESS_PITCH.getInt(), 1f);
        });
    }

    public abstract void removePlayerIsland(SkyUser player);

    public void onIslandLoad(SkyUser user) {
        modifyDefenceOperation(FactionAPI.DefenceOperation.ENABLE, user);

        getPlayerIsland(user.getUniqueId()).whenComplete((island, ex) -> {
            if (ex != null) {
                SLogger.fatal("Failed to fetch island for user {} - {}", user.getUniqueId(), ex);
                return;
            }

            SkyApi.getInstance().getNPCManager().spawnNPC(user.getUniqueId(), island);
        });
    }

    public void onIslandRemove(SkyUser user) {
        user.teleport(SkyApi.getInstance().getRegionAPI().getHubLocation());
        SkyApi.getInstance().getWorldBorderAPI().resetBorder(user);

        SkyApi.getInstance().getCacheService().getEntry(user.getUniqueId()).onIslandRemove();

        removePlayerIsland(user);
    }

    public void modifyDefenceOperation(FactionAPI.DefenceOperation operation, SkyUser user) {
        if (operation == FactionAPI.DefenceOperation.DISABLE && !SkyApi.getInstance().getRegionAPI().isLocationInRegion(user.getLocation(), "sfr_player_" + user.getUniqueId())) return;

        List<Defence> defences = SkyApi.getInstance().getDefenceAPI().getLoadedPlayerDefences().get(user.getUniqueId());
        if (defences != null && !defences.isEmpty()) {
            for (Defence defence : defences) {
                if (operation == FactionAPI.DefenceOperation.ENABLE) defence.onLoad(user.getUniqueId().toString());
                    else defence.disable();
            }
        }
    }

    public void teleportPlayerToIsland(SkyUser user, SkyIsland island) {
        user.teleport(island.getCenter(Settings.ISLAND_PLAYER_WORLD.getString()));
    }


}
