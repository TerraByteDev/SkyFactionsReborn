package net.skullian.skyfactions.paper.api;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import net.skullian.skyfactions.common.api.IslandAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import net.skullian.skyfactions.common.defence.Defence;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import net.skullian.skyfactions.paper.event.defence.DefencePlacementHandler;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SpigotIslandAPI extends IslandAPI {

    @Override
    public void removePlayerIsland(SkyUser player) {
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

        getPlayerIsland(player.getUniqueId()).whenComplete((island, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your island", "SQL_ISLAND_GET", ex);
                return;
            }

            World world = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
            if (world != null) {
                BlockVector3 bottom = BukkitAdapter.asBlockVector(SpigotAdapter.adapt(island.getPosition1(null)));
                BlockVector3 top = BukkitAdapter.asBlockVector(SpigotAdapter.adapt(island.getPosition2(null)));

                CompletableFuture.allOf(
                        SkyApi.getInstance().getRegionAPI().cutRegion(SpigotAdapter.adapt(bottom, world.getName()), SpigotAdapter.adapt(top, world.getName())),
                        SkyApi.getInstance().getRegionAPI().removeRegion("sfr_player_" + player.getUniqueId(), world.getName()),
                        SkyApi.getInstance().getDatabaseManager().getPlayerIslandManager().removeIsland(player),
                        SkyApi.getInstance().getDatabaseManager().getPlayerIslandManager().removeAllTrustedPlayers(island.getId()),
                        SkyApi.getInstance().getDatabaseManager().getDefencesManager().removeAllDefences(player.getUniqueId().toString(), false)
                ).whenComplete((ignored, throwable) -> {
                    if (throwable != null) {
                        throwable.printStackTrace();
                        return;
                    }

                    SkyApi.getInstance().getPlayerAPI().clearInventory(player);
                    SkyApi.getInstance().getPlayerAPI().clearEnderChest(player);

                    Messages.ISLAND_DELETION_SUCCESS.send(player, locale);
                    getAwaitingDeletion().remove(player.getUniqueId());
                    getIslands().remove(player.getUniqueId());
                });
            } else {
                Messages.ERROR.send(player, locale, "operation", "delete your island", "debug", "WORLD_NOT_EXIST");
            }
        });
    }

    public static void modifyDefenceOperation(SpigotFactionAPI.DefenceOperation operation, UUID playerUUID) {
        if (operation == SpigotFactionAPI.DefenceOperation.DISABLE && !SkyApi.getInstance().getRegionAPI().isLocationInRegion(SkyApi.getInstance().getUserManager().getUser(playerUUID).getLocation(), "sfr_player_" + playerUUID.toString())) return;

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
