package net.skullian.skyfactions.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

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
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.defence.Defence;
import net.skullian.skyfactions.event.DefenceHandler;
import net.skullian.skyfactions.island.PlayerIsland;
import net.skullian.skyfactions.obelisk.ObeliskHandler;
import net.skullian.skyfactions.util.ErrorHandler;
import net.skullian.skyfactions.util.FileUtil;
import net.skullian.skyfactions.util.SLogger;
import net.skullian.skyfactions.util.SoundUtil;

public class IslandAPI {

    public static Map<UUID, PlayerIsland> islands = new HashMap<>();
    public static HashSet<UUID> awaitingDeletion = new HashSet<>();

    public static CompletableFuture<PlayerIsland> getPlayerIsland(UUID playerUUID) {
        if (islands.containsKey(playerUUID)) return CompletableFuture.completedFuture(islands.get(playerUUID));
        return SkyFactionsReborn.databaseHandler.getPlayerIsland(playerUUID).thenApply((island) -> {
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

    public static void handlePlayerJoinBorder(Player player, PlayerIsland island) {
        Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> {
            World islandWorld = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
            if (islandWorld == null) return;

            SkyFactionsReborn.worldBorderApi.setBorder(player, (island.getSize() * 2), island.getCenter(islandWorld));
        });
    }

    public static void createIsland(Player player) {

        PlayerIsland island = new PlayerIsland(SkyFactionsReborn.databaseHandler.cachedPlayerIslandID);
        SkyFactionsReborn.databaseHandler.cachedPlayerIslandID++;

        World world = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
        if (world == null) {
            ErrorHandler.handleError(player, "create an island", "WORLD_NOT_EXIST", new IllegalArgumentException("Unknown World: " + Settings.ISLAND_PLAYER_WORLD.getString()));
            return;
        }

        Messages.ISLAND_CREATING.send(player);
        createRegion(player, island, world);

        CompletableFuture.allOf(
                SkyFactionsReborn.databaseHandler.createIsland(player, island),
                pasteIslandSchematic(player, island.getCenter(world), world.getName(), "player")
        ).whenComplete((ignored, ex) -> {
            if (ex != null) {
                ErrorHandler.handleError(player, "create your island", "SQL_ISLAND_CREATE", ex);
                removePlayerIsland(player);
                return;
            }

            islands.put(player.getUniqueId(), island);

            IslandAPI.handlePlayerJoinBorder(player, island); // shift join border
            teleportPlayerToLocation(player, island.getCenter(world));

            ObeliskHandler.spawnPlayerObelisk(player, island);
            Messages.ISLAND_CREATED.send(player);
            SoundUtil.playSound(player, Settings.SOUNDS_ISLAND_CREATE_SUCCESS.getString(), Settings.SOUNDS_ISLAND_CREATE_SUCCESS_PITCH.getInt(), 1f);
        });
    }

    public static CompletableFuture<Boolean> hasIsland(UUID playerUUID) {
        return SkyFactionsReborn.databaseHandler.hasIsland(playerUUID);
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

    public static void teleportPlayerToLocation(Player player, Location location) {
        Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> player.teleport(location));
    }

    private static void createRegion(Player player, PlayerIsland island, World world) {
        Location corner1 = island.getPosition1(null);
        Location corner2 = island.getPosition2(null);

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(world));
        BlockVector3 min = BlockVector3.at(corner1.getBlockX(), -64, corner1.getBlockZ());
        BlockVector3 max = BlockVector3.at(corner2.getBlockX(), 320, corner2.getBlockZ());
        ProtectedRegion region = new ProtectedCuboidRegion(player.getUniqueId().toString(), min, max);

        DefaultDomain owners = region.getOwners();
        owners.addPlayer(player.getUniqueId());

        region.setOwners(owners);
        regionManager.addRegion(region);
    }

    public static void removePlayerIsland(Player player) {
        getPlayerIsland(player.getUniqueId()).whenComplete((island, ex) -> {
            if (ex != null) {
                ErrorHandler.handleError(player, "get your island", "SQL_ISLAND_GET", ex);
                return;
            }

            World world = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
            if (world != null) {
                BlockVector3 bottom = BukkitAdapter.asBlockVector(island.getPosition1(null));
                BlockVector3 top = BukkitAdapter.asBlockVector(island.getPosition2(null));

                Region region = new CuboidRegion(BukkitAdapter.adapt(world), bottom, top);
                cutRegion(region);

                CompletableFuture.allOf(
                        cutRegion(region),
                        SkyFactionsReborn.databaseHandler.removeIsland(player),
                        SkyFactionsReborn.databaseHandler.removeAllTrustedPlayers(island.getId()),
                        SkyFactionsReborn.databaseHandler.removeAllDefences(player.getUniqueId())
                ).whenComplete((ignored, throwable) -> {
                    if (throwable != null) {
                        throwable.printStackTrace();
                        return;
                    }

                    Messages.ISLAND_DELETION_SUCCESS.send(player);
                });
            } else {
                Messages.ERROR.send(player, "%operation%", "delete your island", "%debug%", "WORLD_NOT_EXIST");
            }
        });
    }

    private static CompletableFuture<Void> cutRegion(Region region) {
        return CompletableFuture.runAsync(() -> {
            try (EditSession editSession = WorldEdit.getInstance().newEditSession(region.getWorld())) {
                editSession.setBlocks(region, BlockType.REGISTRY.get("air"));
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
        if (operation == FactionAPI.DefenceOperation.DISABLE && !FactionAPI.isLocationInRegion(Bukkit.getPlayer(playerUUID).getLocation(), playerUUID.toString())) return;

        List<Defence> defences = DefenceHandler.loadedPlayerDefences.get(playerUUID);
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
