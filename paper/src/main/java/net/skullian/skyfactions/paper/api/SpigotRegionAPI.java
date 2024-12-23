package net.skullian.skyfactions.paper.api;

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
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.skullian.skyfactions.common.api.RegionAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.common.util.SkyLocation;
import net.skullian.skyfactions.common.util.worldborder.BorderPos;
import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SpigotRegionAPI extends RegionAPI {

    @Override
    public CompletableFuture<Void> cutRegion(SkyLocation corner1, SkyLocation corner2) {
        return CompletableFuture.runAsync(() -> {
            BlockVector3 min = BlockVector3.at(corner1.getX(), corner1.getY(), corner1.getZ());
            BlockVector3 max = BlockVector3.at(corner2.getX(), corner2.getY(), corner2.getZ());

            Region region = new CuboidRegion(BukkitAdapter.adapt(Bukkit.getWorld(corner1.getWorldName())), min, max);

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(region.getWorld())) {
                editSession.setBlocks(region, BlockType.REGISTRY.get("air"));
            }
        });
    }

    @Override
    public boolean isLocationInRegion(SkyLocation skyLoc, String regionName) {

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        com.sk89q.worldedit.util.Location location = BukkitAdapter.adapt(SpigotAdapter.adapt(skyLoc));
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
    public void createRegion(SkyUser player, SkyLocation corner1, SkyLocation corner2, String world, String regionName) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(Bukkit.getWorld(world)));

        if (regionManager != null) {
            BlockVector3 min = BlockVector3.at(corner1.getBlockX(), -64, corner1.getBlockZ());
            BlockVector3 max = BlockVector3.at(corner2.getBlockX(), 320, corner2.getBlockZ());

            ProtectedRegion region = new ProtectedCuboidRegion(regionName, min, max);
            region.getMembers().addPlayer(player.getUniqueId());
            region.setFlag(Flags.EXIT, StateFlag.State.DENY);

            regionManager.addRegion(region);
        }
    }

    @NotNull
    @Override
    public SkyLocation getHubLocation() {
        List<Integer> hubLocArray = Settings.HUB_LOCATION.getIntegerList();
        return new SkyLocation(Settings.HUB_WORLD_NAME.getString(), hubLocArray.get(0), hubLocArray.get(1), hubLocArray.get(2));
    }

    @Override
    public void modifyWorldBorder(SkyUser player, SkyLocation center, int size) {
        Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> {
            SkyApi.getInstance().getWorldBorderAPI().setWorldBorder(player, (size * 2), new BorderPos(center.getX(), center.getZ()));
        });
    }

    @Override
    public CompletableFuture<Boolean> pasteIslandSchematic(SkyUser player, SkyLocation location, String worldName, String type) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    throw new RuntimeException("Could not find world " + worldName);
                } else {
                    File schemFile = null;
                    if (type.equals("player")) {
                        schemFile = SkyApi.getInstance().getFileAPI().getSchematicFile(Settings.ISLAND_PLAYER_SCHEMATIC.getString());
                    } else if (type.equals("faction")) {
                        schemFile = SkyApi.getInstance().getFileAPI().getSchematicFile(Settings.ISLAND_FACTION_SCHEMATIC.getString());
                    }
                    if (schemFile == null)
                        throw new RuntimeException("Could not find schematic file when attempting to paste island schematic!");

                    com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);
                    Clipboard clipboard;
                    ClipboardFormat format = ClipboardFormats.findByFile(schemFile);
                    if (format == null) throw new RuntimeException("ClipboardFormat returned null for file " + schemFile.getName());
                    try (ClipboardReader reader = format.getReader(new FileInputStream(schemFile))) {
                        clipboard = reader.read();
                    }

                    try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
                        Operation operation = new ClipboardHolder(clipboard)
                                .createPaste(editSession)
                                .to(BukkitAdapter.adapt(SpigotAdapter.adapt(location)).toBlockPoint())
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

    @Override
    public CompletableFuture<Void> removeRegion(String regionName, String world) {
        return CompletableFuture.runAsync(() -> {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager manager = container.get(BukkitAdapter.adapt(Bukkit.getWorld(world)));
            if (manager != null) manager.removeRegion(regionName);
        });
    }

    @Override
    public boolean worldExists(String worldName) {
        return Bukkit.getWorld(worldName) != null;
    }
}
