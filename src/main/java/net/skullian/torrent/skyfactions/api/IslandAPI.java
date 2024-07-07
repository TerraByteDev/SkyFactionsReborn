package net.skullian.torrent.skyfactions.api;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
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
import net.skullian.torrent.skyfactions.config.Settings;
import net.skullian.torrent.skyfactions.island.PlayerIsland;
import net.skullian.torrent.skyfactions.obelisk.ObeliskHandler;
import net.skullian.torrent.skyfactions.util.FileUtil;
import net.skullian.torrent.skyfactions.util.SoundUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Log4j2(topic = "SkyFactionsReborn")
public class IslandAPI {

    public static HashSet<UUID> awaitingDeletion = new HashSet<>();

    public static void createIsland(Player player) {

        PlayerIsland island = new PlayerIsland(SkyFactionsReborn.db.cachedPlayerIslandID);
        SkyFactionsReborn.db.cachedPlayerIslandID++;

        World world = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
        Messages.ISLAND_CREATING.send(player);
        createRegion(player, island, world) ;

        SkyFactionsReborn.db.createIsland(player, island).join();
        pasteIslandSchematic(player, island.getCenter(world), world.getName(), "player").thenAccept(ac -> {
            teleportPlayerToLocation(player, island.getCenter(world));

            ObeliskHandler.spawnPlayerObelisk(player, island);
            Messages.ISLAND_CREATED.send(player);
            SoundUtil.playSound(player, Settings.SOUNDS_ISLAND_CREATE_SUCCESS.getString(), Settings.SOUNDS_ISLAND_CREATE_SUCCESS_PITCH.getInt(), 1f);
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
                        schemFile = FileUtil.getSchematicFile(Settings.ISLAND_PLAYER_SCHEMATIC.getString());
                    } else if (type.equals("faction")) {
                        schemFile = FileUtil.getSchematicFile(Settings.ISLAND_FACTION_SCHEMATIC.getString());
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

                        Thread.sleep(750);
                    } catch (InterruptedException ignored) {}
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

    private static void createRegion(Player player, PlayerIsland island, World world) {
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

    public static void removePlayerIsland(Player player) {
        SkyFactionsReborn.db.getPlayerIsland(player).thenAccept(island -> {
            World world = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
            if (world != null) {
                try {
                    BlockVector3 bottom = BukkitAdapter.asBlockVector(island.getPosition1(null));
                    BlockVector3 top = BukkitAdapter.asBlockVector(island.getPosition2(null));

                    Region region = new CuboidRegion(BukkitAdapter.adapt(world), bottom, top);
                    cutRegion(region).get();

                    SkyFactionsReborn.db.removeIsland(player).thenAccept(res -> {
                        SkyFactionsReborn.db.removeAllTrustedPlayers(island.getId());
                        Messages.ISLAND_DELETION_SUCCESS.send(player);
                    }).exceptionally(ex -> {
                        ex.printStackTrace();
                        Messages.ERROR.send(player, "%operation%", "delete your island", "%debug%", "SQL_ISLAND_DELETE");
                        return null;
                    });
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
