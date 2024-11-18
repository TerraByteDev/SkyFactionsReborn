package net.skullian.skyfactions.api;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.island.impl.PlayerIsland;
import net.skullian.skyfactions.util.FileUtil;
import net.skullian.skyfactions.util.SLogger;
import net.skullian.skyfactions.util.worldborder.WorldBorder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RegionAPI {

    public static void teleportPlayerToLocation(Player player, Location location) {
        Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> player.teleport(location));
    }

    public static CompletableFuture<Void> cutRegion(Region region) {
        return CompletableFuture.runAsync(() -> {
            try (EditSession editSession = WorldEdit.getInstance().newEditSession(region.getWorld())) {
                editSession.setBlocks(region, BlockType.REGISTRY.get("air"));
            }
        });
    }

    /**
     * Check if a location is in a certain region.
     *
     * @param bukkitLoc  Location to check.
     * @param regionName Name of region.
     * @return {@link Boolean}
     */
    public static boolean isLocationInRegion(Location bukkitLoc, String regionName) {

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        com.sk89q.worldedit.util.Location location = BukkitAdapter.adapt(bukkitLoc);
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(location);

        for (ProtectedRegion region : set) {
            if (region.getId().equalsIgnoreCase(regionName)) {
                return true;
            }
        }

        return false;
    }

    public static void createRegion(Player player, Location corner1, Location corner2, World world, String regionName) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(world));
        BlockVector3 min = BlockVector3.at(corner1.getBlockX(), -64, corner1.getBlockZ());
        BlockVector3 max = BlockVector3.at(corner2.getBlockX(), 320, corner2.getBlockZ());
        ProtectedRegion region = new ProtectedCuboidRegion(regionName, min, max);

        DefaultDomain owners = region.getOwners();
        owners.addPlayer(player.getUniqueId());

        region.setOwners(owners);
        regionManager.addRegion(region);
    }

    public static Location getHubLocation() {
        World hubWorld = Bukkit.getWorld(Settings.HUB_WORLD_NAME.getString());

        List<Integer> hubLocArray = Settings.HUB_LOCATION.getIntegerList();
        return new Location(hubWorld, hubLocArray.get(0), hubLocArray.get(1), hubLocArray.get(2));
    }

    public static void modifyWorldBorder(Player player, Location center, int size) {
        Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> {
            SkyFactionsReborn.getWorldBorderApi().setWorldBorder(player, (size * 2), new WorldBorder.BorderPos(center.getX(), center.getZ()));
        });
    }

    public static CompletableFuture<Boolean> pasteIslandSchematic(Player player, Location location, String worldName, String type) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    SLogger.fatal("Could not find world: {}", worldName);
                    throw new RuntimeException("Could not find world " + worldName);
                } else {
                    File schemFile = null;
                    if (type.equals("player")) {
                        schemFile = FileUtil.getSchematicFile(Settings.ISLAND_PLAYER_SCHEMATIC.getString());
                    } else if (type.equals("faction")) {
                        schemFile = FileUtil.getSchematicFile(Settings.ISLAND_FACTION_SCHEMATIC.getString());
                    }
                    if (schemFile == null)
                        throw new RuntimeException("Could not find schematic file when attempting to paste island schematic!");

                    com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);
                    Clipboard clipboard;
                    ClipboardFormat format = ClipboardFormats.findByFile(schemFile);
                    try (ClipboardReader reader = format.getReader(new FileInputStream(schemFile))) {
                        clipboard = reader.read();
                    }

                    try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
                        Operation operation = new ClipboardHolder(clipboard)
                                .createPaste(editSession)
                                .to(BukkitAdapter.adapt(location).toBlockPoint())
                                .build();
                        SLogger.warn("Pasting schematic [{}] in world [{}] for player [{}].", schemFile.getName(), world.getName(), player.getName());

                        Operations.complete(operation);
                    }

                    return true;
                }
            } catch (IOException e) {
                SLogger.fatal("Error pasting island schematic", e);
                throw new RuntimeException(e);
            }
        });
    }
}
