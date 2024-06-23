package net.skullian.torrent.skyfactions.island;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import lombok.extern.log4j.Log4j2;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.util.FileUtil;
import net.skullian.torrent.skyfactions.util.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Log4j2(topic = "SkyFactionsReborn")
public class IslandAPI {

    public static List<UUID> awaitingDeletion;

    public static void createIsland(Player player) {

        SkyIsland island = new SkyIsland(SkyFactionsReborn.db.cachedNextID);
        SkyFactionsReborn.db.cachedNextID++;
        World world = Bukkit.getWorld(SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getString("Island.ISLAND_WORLD_NAME"));

        Messages.ISLAND_CREATING.send(player);
        createRegion(player, island, world) ;

        SkyFactionsReborn.db.createIsland(player, island).thenAccept(value -> pasteIslandSchematic(player, island.getCenter(world), world.getName(), "player").thenAccept(res -> {
            teleportPlayerToLocation(player, island.getCenter(world));
            Messages.ISLAND_CREATING.send(player);
            // TODO MAKE CONFIGURABLE
            SoundUtil.playSound(player, SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getString("Sounds.ISLAND_CREATE_SUCCESS"), SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getInt("Sounds.ISLAND_CREATE_SUCCESS_PITCH"), 1f);
        }).exceptionally(ex -> {
            ex.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "create a new island", "%debug%", "FAWE_ISLAND_PASTE");
            return null;
        })).exceptionally(ex -> {
            ex.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "create a new island", "%debug%", "SQL_ISLAND_CREATE");
           return null;
        });
    }

    public static CompletableFuture<Void> pasteIslandSchematic(Player player, Location location, String worldName, String type) {
        return CompletableFuture.runAsync(() -> {
            try {
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    LOGGER.error("Could not find world: {}", worldName);
                    throw new RuntimeException("Could not find world " + worldName);
                } else {
                    File schemFile = null;
                    if (type.equals("player")) {
                        schemFile = FileUtil.getSchematicFile(SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getString("Island.NORMAL_ISLAND_SCHEMATIC"));
                    } else if (type.equals("faction")) {
                        schemFile = FileUtil.getSchematicFile(SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getString("Island.FACTION_ISLAND_SCHEMATIC"));
                    }

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
                        LOGGER.warn("Pasting schematic [{}] in world [{}] for player [{}].", schemFile.getName(), world.getName(), player.getName());

                        Operations.complete(operation);
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Error pasting island schematic", e);
                throw new RuntimeException(e);
            }
        });
    }

    public static void teleportPlayerToLocation(Player player, Location location) {
        Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> player.teleport(location));
    }

    private static void createRegion(Player player, SkyIsland island, World world) {
        Location corner1 = island.getPosition1(null);
        Location corner2 = island.getPosition2(null);

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(world));
        BlockVector3 min = BlockVector3.at(corner1.getBlockX(), -64, corner1.getBlockZ());
        BlockVector3 max = BlockVector3.at(corner2.getBlockX(), 319, corner2.getBlockZ());
        ProtectedRegion region = new ProtectedCuboidRegion(player.getUniqueId().toString(), min, max);

        DefaultDomain owners = region.getOwners();
        owners.addPlayer(player.getUniqueId());

        region.setOwners(owners);
        regionManager.addRegion(region);
    }

    public static CompletableFuture<Void> saveIslandSchematic(Player player, SkyIsland island) {
        LOGGER.warn("Saving WE Schematic for {}", player.getName());
        return CompletableFuture.runAsync(() -> {
            World world = Bukkit.getWorld(SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getString("Island.ISLAND_WORLD_NAME"));

            File file = new File(SkyFactionsReborn.getInstance().getDataFolder(), "/schematics/raid_saves/" + player.getUniqueId() + ".schematic");
            BlockVector3 bottom = BukkitAdapter.asBlockVector(island.getPosition1(null));
            BlockVector3 top = BukkitAdapter.asBlockVector(island.getPosition2(null));
            CuboidRegion region = new CuboidRegion(BukkitAdapter.adapt(world), bottom, top);


            BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

            ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                    BukkitAdapter.adapt(world), region, clipboard, region.getMinimumPoint()
            );
            forwardExtentCopy.setCopyingEntities(true);
            Operations.complete(forwardExtentCopy);

            try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
                writer.write(clipboard);
            } catch (IOException error) {
                error.printStackTrace();
                throw new RuntimeException(error);
            }
        });
    }

    public static void removePlayerIsland(Player player) {
        SkyFactionsReborn.db.getPlayerIsland(player).thenAccept(island -> {
            World world = Bukkit.getWorld(SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getString("Island.ISLAND_WORLD_NAME"));
            if (world != null) {
                try {
                    Region region = new CuboidRegion(BukkitAdapter.adapt(island.getPosition1(world)).toBlockPoint(), BukkitAdapter.adapt(island.getPosition2(world)).toBlockPoint());
                    cutRegion(region).get();
                } catch (InterruptedException | ExecutionException error) {
                    error.printStackTrace();
                    Messages.ERROR.send(player, "%operation%", "delete your island", "%debug%", "FAWE_ISLAND_CUT");
                }
            } else {
                Messages.ERROR.send(player, "%operation%", "delete your island", "%debug%", "WORLD_NOT_EXIST");
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    private static CompletableFuture<Void> cutRegion(Region region) {
        return CompletableFuture.runAsync(() -> {
            try (EditSession editSession = WorldEdit.getInstance().newEditSession(region.getWorld())) {
                editSession.setBlocks(region, BlockType.REGISTRY.get("air"));
            }
        });
    }
}
