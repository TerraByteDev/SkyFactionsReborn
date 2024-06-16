package net.skullian.torrent.skyfactions.island;

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
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
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
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

@Log4j2(topic = "SkyFactionsReborn")
public class IslandAPI {

    public static void createIsland(Player player) {

        SkyIsland island = new SkyIsland(SkyFactionsReborn.db.cachedNextID);
        SkyFactionsReborn.db.cachedNextID++;
        World world = Bukkit.getWorld(SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getString("Island.ISLAND_WORLD_NAME"));

        Messages.ISLAND_CREATING.send(player);
        createRegion(player, island, world) ;

        SkyFactionsReborn.db.createIsland(player, island).thenAccept(value -> {
            CompletableFuture<Void> pasteIslandSchematicFuture = pasteIslandSchematic(player, island.getCenter(world), world.getName(), "player");

            CompletableFuture.allOf(pasteIslandSchematicFuture)
                    .handle((result, ex) -> {
                        if (ex!= null) {
                            ex.printStackTrace();
                            Messages.ERROR.send(player, "%operation%", "create a new island");
                        } else {
                            teleportPlayerToLocation(player, island.getCenter(world));
                            Messages.ISLAND_CREATED.send(player);
                            SoundUtil.playSound(player, "ui.toast.challenge_complete", 1f, 1f);
                        }
                        return null;
                    });
        }).exceptionally(ex -> {
            ex.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "create a new island");
            return null;
        });


    }

    public static CompletableFuture<Void> pasteIslandSchematic(Player player, Location location, String worldName, String type) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    LOGGER.error("Could not find world: {}", worldName);
                    return null;
                }

                File schemFile = null;
                if (type.equals("player")) {
                    schemFile = FileUtil.getSchematicFile(SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getString("Island.NORMAL_ISLAND_SCHEMATIC"));
                } else if (type.equals("faction")) {
                    schemFile = FileUtil.getSchematicFile(SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getString("Island.FACTION_ISLAND_SCHEMATIC"));
                }

                com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);
                Clipboard clipboard = null;
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
                return null;
            } catch (IOException e) {
                LOGGER.error("Error pasting island schematic", e);
                return null;
            }
        });
    }

    private static void teleportPlayerToLocation(Player player, Location location) {
        Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> {
            player.teleport(location);
        });
    }

    private static void createRegion(Player player, SkyIsland island, World world) {
        WorldGuardPlugin worldGuardPlugin = WorldGuardPlugin.inst();

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

    private static void takeRaidSchematic(Player player) throws SQLException {



    }
}
