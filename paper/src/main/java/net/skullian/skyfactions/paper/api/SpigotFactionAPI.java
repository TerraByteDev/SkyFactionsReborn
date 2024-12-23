package net.skullian.skyfactions.paper.api;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.skullian.skyfactions.common.api.FactionAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.island.impl.FactionIsland;
import net.skullian.skyfactions.common.notification.NotificationType;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import net.skullian.skyfactions.common.util.SkyLocation;
import net.skullian.skyfactions.common.util.worldborder.BorderPos;
import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.concurrent.CompletableFuture;

public class SpigotFactionAPI extends FactionAPI {

    @Override
    public void handleFactionWorldBorder(SkyUser player, FactionIsland island) {
        Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> {
            SkyLocation center = island.getCenter(null);

            SkyApi.getInstance().getWorldBorderAPI().setWorldBorder(player, (island.getSize() * 2), new BorderPos(center.getX(), center.getZ()));
        });
    }

    /**
     * Teleport the player to their faction's island.
     *
     * @param player Player to teleport.
     */
    @Override
    public void teleportToFactionIsland(SkyUser player, Faction faction) {
        World world = Bukkit.getWorld(Settings.ISLAND_FACTION_WORLD.getString());

        if (world != null) {
            player.teleport(faction.getIsland().getCenter(world.getName()));
            onFactionLoad(faction, player);
        } else {
            Messages.ERROR.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "operation", "teleport to your faction's island", "debug", "WORLD_NOT_EXIST");
        }
    }

    @Override
    public void disbandFaction(SkyUser player, Faction faction) {

        World world = Bukkit.getWorld(Settings.ISLAND_FACTION_WORLD.getString());
        if (world != null && faction.getIsland() != null) {
            faction.createBroadcast(player, Messages.FACTION_DISBAND_BROADCAST);
            onFactionDisband(faction);

            CompletableFuture.allOf(
                    SkyApi.getInstance().getRegionAPI().cutRegion(faction.getIsland().getPosition1(world.getName()), faction.getIsland().getPosition2(world.getName())),
                    SkyApi.getInstance().getRegionAPI().removeRegion("sfr_faction_" + faction.getName(), world.getName()),
                    SkyApi.getInstance().getDatabaseManager().getFactionsManager().removeFaction(faction.getName()),
                    SkyApi.getInstance().getDatabaseManager().getDefencesManager().removeAllDefences(faction.getName(), true)
            ).whenComplete((ignored, ex) -> {
                if (ex != null) {
                    ErrorUtil.handleError(player, "disband your faction", "SQL_FACTION_DISBAND", ex);
                    return;
                }


                Messages.FACTION_DISBAND_SUCCESS.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()));
            });
        }
    }

    @Override
    public void onFactionDisband(Faction faction) {
        for (SkyUser member : faction.getAllMembers()) {
            getFactionUserCache().remove(member.getUniqueId());
            getFactionCache().remove(faction.getName());

            SkyApi.getInstance().getNotificationAPI().createNotification(member.getUniqueId(), NotificationType.FACTION_DISBANDED);

            if (member.isOnline() && SkyApi.getInstance().getRegionAPI().isLocationInRegion(member.getLocation(), "sfr_faction_" + faction.getName())) {
                member.teleport(SkyApi.getInstance().getRegionAPI().getHubLocation());
            }
        }
    }

    public static ProtectedRegion getFactionRegion(Faction faction) {
        World world = Bukkit.getWorld(Settings.ISLAND_FACTION_WORLD.getString());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(world));

        if (world != null && regions != null) {
           return regions.getRegion("sfr_faction_" + faction.getName());
        }

        return null;
    }

    @Override
    public void createRegion(SkyUser player, FactionIsland island, String worldName, String faction_name) {
        SkyLocation corner1 = island.getPosition1(worldName);
        SkyLocation corner2 = island.getPosition2(worldName);

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager manager = container.get(BukkitAdapter.adapt(Bukkit.getWorld(worldName)));
        if (manager == null) throw new NullPointerException("Could not find region manager for world: " + worldName);

        BlockVector3 min = BlockVector3.at(corner1.getX(), -64, corner1.getZ());
        BlockVector3 max = BlockVector3.at(corner2.getX(), 320, corner2.getZ());
        ProtectedRegion region = new ProtectedCuboidRegion("sfr_faction_" + faction_name, min, max);
        region.getMembers().addPlayer(player.getUniqueId());
        region.setFlag(Flags.EXIT, StateFlag.State.DENY);

        manager.addRegion(region);
    }

    @Override
    public boolean isInRegion(SkyUser player, String regionName) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        com.sk89q.worldedit.util.Location location = BukkitAdapter.adapt(SpigotAdapter.adapt(player.getLocation()));
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(location);

        for (ProtectedRegion region : set) {
            if (region.getId().equalsIgnoreCase(regionName)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void removeMemberFromRegion(SkyUser user, Faction faction) {
        ProtectedRegion region = getFactionRegion(faction);
        if (region != null) region.getMembers().removePlayer(user.getUniqueId());
    }

    @Override
    public void addMemberToRegion(SkyUser user, Faction faction) {
        ProtectedRegion region = getFactionRegion(faction);
        if (region != null) region.getMembers().addPlayer(user.getUniqueId());
    }

}
