package net.skullian.skyfactions.api;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.database.tables.Notifications;
import net.skullian.skyfactions.defence.Defence;
import net.skullian.skyfactions.event.defence.DefencePlacementHandler;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.faction.AuditLogType;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.island.FactionIsland;
import net.skullian.skyfactions.notification.NotificationType;
import net.skullian.skyfactions.obelisk.ObeliskHandler;
import net.skullian.skyfactions.util.ErrorUtil;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import net.skullian.skyfactions.util.worldborder.WorldBorder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class FactionAPI {

    public static Map<UUID, String> factionCache = new HashMap<>();
    public static Map<String, Faction> factionNameCache = new HashMap<>();

    public static HashSet<Faction> awaitingDeletion = new HashSet<>();

    public static void handleFactionWorldBorder(Player player, FactionIsland island) {
        Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> {
            World world = Bukkit.getWorld(Settings.ISLAND_FACTION_WORLD.getString());
            if (world == null) return;
            Location center = island.getCenter(world);

            SkyFactionsReborn.worldBorderApi.setWorldBorder(player, (island.getSize() * 2), new WorldBorder.BorderPos(center.getX(), center.getZ()));
        });
    }

    /**
     * Teleport the player to their faction's island.
     *
     * @param player Player to teleport.
     */
    public static void teleportToFactionIsland(Player player, Faction faction) {
        World world = Bukkit.getWorld(Settings.ISLAND_FACTION_WORLD.getString());

        if (world != null) {
            RegionAPI.teleportPlayerToLocation(player, faction.getIsland().getCenter(world));
            onFactionLoad(faction, player);
        } else {
            Messages.ERROR.send(player, PlayerHandler.getLocale(player.getUniqueId()), "operation", "teleport to your faction's island", "debug", "WORLD_NOT_EXIST");
        }
    }

    /**
     * Create a new faction.
     *
     * @param player Owner of the faction.
     * @param name   Name of the faction.
     */
    public static void createFaction(Player player, String name) {
        SkyFactionsReborn.databaseManager.factionsManager.registerFaction(player, name).whenComplete((ignored, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "create a new Faction", "SQL_FACTION_CREATE", ex);
                // todo disband faction?
                return;
            }
            // Creating & Registering the island in the database before getting the Faction.
            // This stops the Faction retrieval returning null for the island, and causing issues, seeing as the Faction
            // is immediately cached.
            createIsland(player, name);

            FactionAPI.getFaction(name).whenComplete((faction, exc) -> {
                if (exc != null) {
                    ErrorUtil.handleError(player, "create a new Faction", "SQL_FACTION_CREATE", exc);
                    return;
                }

                faction.createAuditLog(player.getUniqueId(), AuditLogType.FACTION_CREATE, "player_name", player.getName(), "faction_name", name);
                NotificationAPI.factionInviteStore.put(faction.getName(), 0);
                factionCache.put(player.getUniqueId(), faction.getName());
                factionNameCache.put(faction.getName(), faction);
            });
        });
    }

    public static void disbandFaction(Player player, Faction faction) {
        FactionIsland island = faction.getIsland();
        Region region = getFactionRegion(faction);

        World world = Bukkit.getWorld(Settings.ISLAND_FACTION_WORLD.getString());
        if (world != null && region != null) {
            faction.createBroadcast(player, Messages.FACTION_DISBAND_BROADCAST);
            onFactionDisband(faction);

            CompletableFuture.allOf(
                    RegionAPI.cutRegion(region),
                    SkyFactionsReborn.databaseManager.factionsManager.removeFaction(faction.getName()),
                    SkyFactionsReborn.databaseManager.defencesManager.removeAllDefences(faction.getName(), true)
            ).whenComplete((ignored, ex) -> {
                if (ex != null) {
                    ErrorUtil.handleError(player, "disband your faction", "SQL_FACTION_DISBAND", ex);
                    return;
                }

                Messages.FACTION_DISBAND_SUCCESS.send(player, PlayerHandler.getLocale(player.getUniqueId()));
            });
        }
    }

    private static void onFactionDisband(Faction faction) {
        for (OfflinePlayer member : faction.getAllMembers()) {
            NotificationAPI.createNotification(member.getUniqueId(), NotificationType.FACTION_DISBANDED);

            if (member.isOnline() && RegionAPI.isLocationInRegion(member.getPlayer().getLocation(), "SFR_FACTION_" + faction.getName())) {
                RegionAPI.teleportPlayerToLocation(member.getPlayer(), RegionAPI.getHubLocation());
            }
        }
    }

    public static Region getFactionRegion(Faction faction) {
        World world = Bukkit.getWorld(Settings.ISLAND_FACTION_WORLD.getString());
        if (world != null) {
            BlockVector3 bottom = BukkitAdapter.asBlockVector(faction.getIsland().getPosition1(null));
            BlockVector3 top = BukkitAdapter.asBlockVector(faction.getIsland().getPosition2(null));

            return new CuboidRegion(BukkitAdapter.adapt(world), bottom, top);
        }

        return null;
    }

    /**
     * Check if a player is in a faction.
     * You can alternatively use {@link #getFaction(String)} and check if the return value is null.
     *
     * @param player Player to check.
     * @return {@link Boolean}
     */
    public static CompletableFuture<Boolean> isInFaction(Player player) {
        if (factionCache.containsKey(player.getUniqueId())) return CompletableFuture.completedFuture(true);
        return SkyFactionsReborn.databaseManager.factionsManager.isInFaction(player);
    }

    /**
     * Get the faction from a player.
     *
     * @param playerUUID UUID of member of faction (Owner, Moderator, whatever).
     * @return {@link Faction}
     */
    public static CompletableFuture<Faction> getFaction(UUID playerUUID) {
        if (factionCache.containsKey(playerUUID))
            return CompletableFuture.completedFuture(getCachedFaction(playerUUID));


        return SkyFactionsReborn.databaseManager.factionsManager.getFaction(playerUUID).whenComplete((faction, ex) -> {
            if (ex != null) ex.printStackTrace();
            if (faction == null) return;

            factionCache.put(playerUUID, faction.getName());
            factionNameCache.put(faction.getName(), faction);
        });
    }

    /**
     * Get the faction from a Faction's name.
     *
     * @param name Name of the faction.
     * @return {@link Faction}
     */
    public static CompletableFuture<Faction> getFaction(String name) {
        if (factionNameCache.containsKey(name)) return CompletableFuture.completedFuture(getCachedFaction(name));

        return SkyFactionsReborn.databaseManager.factionsManager.getFaction(name).whenComplete((faction, ex) -> {
            if (ex != null) ex.printStackTrace();
            if (faction == null) return;

            factionNameCache.put(faction.getName(), faction);
            for (OfflinePlayer player : faction.getAllMembers()) {
                if (!player.isOnline()) return;
                factionCache.put(player.getUniqueId(), faction.getName());
            }
        });
    }

    /**
     * Get an already cached Faction.
     *
     * @param name Name of the Faction
     * @return {@link Faction}
     */
    public static Faction getCachedFaction(String name) {
        return factionNameCache.get(name);
    }

    /**
     * Get an already cached Faction.
     *
     * @param playerUUID UUID of a faction member
     * @return {@link Faction}
     */
    public static Faction getCachedFaction(UUID playerUUID) {
        return factionNameCache.get(factionCache.get(playerUUID));
    }

    /**
     * @param player Player corresponded to string. Set to null if not needed.
     * @param name   String to check.
     * @return {@link Boolean}
     */
    public static boolean hasValidName(Player player, String name) {
        int minimumLength = Settings.FACTION_CREATION_MIN_LENGTH.getInt();
        int maximumLength = Settings.FACTION_CREATION_MAX_LENGTH.getInt();
        int length = name.length();
        if (length >= minimumLength && length <= maximumLength) {
            if (!Settings.FACTION_CREATION_ALLOW_NUMBERS.getBoolean() && TextUtility.containsNumbers(name)) {
                Messages.FACTION_NO_NUMBERS.send(player, PlayerHandler.getLocale(player.getUniqueId()));
                return false;
            } else if (!Settings.FACTION_CREATION_ALLOW_NON_ENGLISH.getBoolean() && !TextUtility.isEnglish(name)) {
                Messages.FACTION_NON_ENGLISH.send(player, PlayerHandler.getLocale(player.getUniqueId()));
                return false;
            } else if (!Settings.FACTION_CREATION_ALLOW_SYMBOLS.getBoolean() && TextUtility.hasSymbols(name)) {
                Messages.FACTION_NO_SYMBOLS.send(player, PlayerHandler.getLocale(player.getUniqueId()));
                return false;
            } else {
                boolean regexMatch = false;
                List<String> blacklistedNames = Settings.FACTION_CREATION_BLACKLISTED_NAMES.getList();

                for (String blacklistedName : blacklistedNames) {
                    if (Pattern.compile(blacklistedName).matcher(name).find()) {
                        regexMatch = true;
                        break;
                    }
                }

                if (regexMatch) {
                    Messages.FACTION_NAME_PROHIBITED.send(player, PlayerHandler.getLocale(player.getUniqueId()));
                    return false;
                } else {
                    return true;
                }
            }

        } else {
            Messages.FACTION_NAME_LENGTH_LIMIT.send(player, PlayerHandler.getLocale(player.getUniqueId()), "min", minimumLength, "max", maximumLength);
            return false;
        }
    }

    private static void createRegion(Player player, FactionIsland island, World world, String faction_name) {
        Location corner1 = island.getPosition1(null);
        Location corner2 = island.getPosition2(null);

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(world));
        BlockVector3 min = BlockVector3.at(corner1.getBlockX(), -64, corner1.getBlockZ());
        BlockVector3 max = BlockVector3.at(corner2.getBlockX(), 320, corner2.getBlockZ());
        ProtectedRegion region = new ProtectedCuboidRegion(faction_name, min, max);

        DefaultDomain owners = region.getOwners();
        owners.addPlayer(player.getUniqueId());

        region.setOwners(owners);
        regionManager.addRegion(region);
    }

    private static void createIsland(Player player, String faction_name) {
        FactionIsland island = new FactionIsland(SkyFactionsReborn.databaseManager.factionIslandManager.cachedFactionIslandID, System.currentTimeMillis() + Settings.RAIDING_FACTION_IMMUNITY.getInt());
        SkyFactionsReborn.databaseManager.factionIslandManager.cachedFactionIslandID++;

        World world = Bukkit.getWorld(Settings.ISLAND_FACTION_WORLD.getString());
        createRegion(player, island, world, faction_name);

        CompletableFuture.allOf(SkyFactionsReborn.databaseManager.factionIslandManager.createFactionIsland(faction_name, island), RegionAPI.pasteIslandSchematic(player, island.getCenter(world), world.getName(), "faction")).whenComplete((unused, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }

            ObeliskHandler.spawnFactionObelisk(faction_name, island);

            handleFactionWorldBorder(player, island);
            IslandAPI.modifyDefenceOperation(DefenceOperation.DISABLE, player.getUniqueId());
            RegionAPI.teleportPlayerToLocation(player, island.getCenter(world));

            SoundUtil.playSound(player, Settings.SOUNDS_ISLAND_CREATE_SUCCESS.getString(), Settings.SOUNDS_ISLAND_CREATE_SUCCESS_PITCH.getInt(), 1f);
            Messages.FACTION_CREATION_SUCCESS.send(player, PlayerHandler.getLocale(player.getUniqueId()));
        });
    }

    /**
     * Check if a player is in a certain region.
     *
     * @param player     Player to check.
     * @param regionName Name of region.
     * @return {@link Boolean}
     */
    public static boolean isInRegion(Player player, String regionName) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        com.sk89q.worldedit.util.Location location = BukkitAdapter.adapt(player.getLocation());
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(location);

        for (ProtectedRegion region : set) {
            if (region.getId().equalsIgnoreCase(regionName)) {
                return true;
            }
        }

        return false;
    }

    public static void onFactionLoad(Faction faction, Player player) {
        SkyFactionsReborn.npcManager.spawnNPC(faction, faction.getIsland());
        modifyDefenceOperation(DefenceOperation.ENABLE, player);
    }

    public static void modifyDefenceOperation(DefenceOperation operation, Player player) {
        getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            } else if (operation == DefenceOperation.DISABLE && !RegionAPI.isLocationInRegion(player.getLocation(), faction.getName())) return;

            List<Defence> defences = DefencePlacementHandler.loadedFactionDefences.get(faction.getName());
            if (defences != null && !defences.isEmpty()) {
                for (Defence defence : defences) {
                    if (operation == DefenceOperation.ENABLE) {
                        defence.onLoad(faction.getName());
                    } else {
                        defence.disable();
                    }
                }
            }
        });
    }

    public enum DefenceOperation {
        ENABLE,
        DISABLE
    }
}
