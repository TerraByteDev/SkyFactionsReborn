package net.skullian.torrent.skyfactions.api;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.config.types.Messages;
import net.skullian.torrent.skyfactions.config.types.Settings;
import net.skullian.torrent.skyfactions.faction.AuditLogType;
import net.skullian.torrent.skyfactions.faction.Faction;
import net.skullian.torrent.skyfactions.island.FactionIsland;
import net.skullian.torrent.skyfactions.obelisk.ObeliskHandler;
import net.skullian.torrent.skyfactions.util.ErrorHandler;
import net.skullian.torrent.skyfactions.util.SoundUtil;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class FactionAPI {

    private static Map<UUID, Faction> factionCache = new HashMap<>();
    private static Map<String, Faction> factionNameCache = new HashMap<>();

    /**
     * Teleport the player to their faction's island.
     *
     * @param player Player to teleport.
     */
    public static void teleportToFactionIsland(Player player, Faction faction) {
        World world = Bukkit.getWorld(Settings.ISLAND_FACTION_WORLD.getString());

        if (world != null) {
            IslandAPI.teleportPlayerToLocation(player, faction.getIsland().getCenter(world));
        } else {
            Messages.ERROR.send(player, "%operation%", "teleport to your faction's island", "%debug%", "WORLD_NOT_EXIST");
        }
    }

    /**
     * Create a new faction.
     *
     * @param player Owner of the faction.
     * @param name   Name of the faction.
     */
    public static void createFaction(Player player, String name) {
        SkyFactionsReborn.db.registerFaction(player, name).whenComplete((ignored, ex) -> {
            if (ex != null) {
                ErrorHandler.handleError(player, "create a new Faction", "SQL_FACTION_CREATE", ex);
                // todo disband faction?
                return;
            }

            FactionAPI.getFaction(name).whenComplete((faction, exc) -> {
                if (exc != null) {
                    ErrorHandler.handleError(player, "create a new Faction", "SQL_FACTION_CREATE", exc);
                    // todo disband faction?
                    return;
                }

                faction.createAuditLog(player.getUniqueId(), AuditLogType.FACTION_CREATE, "%player_name%", player.getName(), "%faction_name%", name);
                createIsland(player, name);
                NotificationAPI.factionInviteStore.put(faction.getName(), 0);
            });
        });
    }

    /**
     * Check if a player is in a faction.
     * You can alternatively use {@link #getFaction(Player)} and check if the return value is null.
     *
     * @param player Player to check.
     * @return {@link Boolean}
     */
    public static CompletableFuture<Boolean> isInFaction(Player player) {
        if (factionCache.containsKey(player.getUniqueId())) return CompletableFuture.completedFuture(true);
        return SkyFactionsReborn.db.isInFaction(player);
    }

    /**
     * Get the faction from a player.
     *
     * @param player Member of faction (Owner, Moderator, whatever).
     * @return {@link Faction}
     */
    public static CompletableFuture<Faction> getFaction(Player player) {
        if (factionCache.containsKey(player.getUniqueId()))
            return CompletableFuture.completedFuture(factionCache.get(player.getUniqueId()));


        return SkyFactionsReborn.db.getFaction(player).whenComplete((faction, ex) -> {
            if (ex != null) ex.printStackTrace();
            if (faction == null) return;

            factionCache.put(player.getUniqueId(), faction);
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
        if (factionNameCache.containsKey(name)) return CompletableFuture.completedFuture(factionNameCache.get(name));
        return SkyFactionsReborn.db.getFaction(name).whenComplete((faction, ex) -> {
            if (ex != null) ex.printStackTrace();
            if (faction == null) return;

            factionNameCache.put(faction.getName(), faction);
            for (OfflinePlayer player : faction.getAllMembers()) {
                if (!player.isOnline()) return;
                factionCache.put(player.getUniqueId(), faction);
            }
        });
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
                Messages.FACTION_NO_NUMBERS.send(player);
                return false;
            } else if (!Settings.FACTION_CREATION_ALLOW_NON_ENGLISH.getBoolean() && !TextUtility.isEnglish(name)) {
                Messages.FACTION_NON_ENGLISH.send(player);
                return false;
            } else if (!Settings.FACTION_CREATION_ALLOW_SYMBOLS.getBoolean() && TextUtility.hasSymbols(name)) {
                Messages.FACTION_NO_SYMBOLS.send(player);
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
                    Messages.FACTION_NAME_PROHIBITED.send(player);
                    return false;
                } else {
                    return true;
                }
            }

        } else {
            Messages.FACTION_NAME_LENGTH_LIMIT.send(player, "%min%", minimumLength, "%max%", maximumLength);
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
        FactionIsland island = new FactionIsland(SkyFactionsReborn.db.cachedFactionIslandID, 0);
        SkyFactionsReborn.db.cachedFactionIslandID++;

        World world = Bukkit.getWorld(Settings.ISLAND_FACTION_WORLD.getString());
        createRegion(player, island, world, faction_name);

        CompletableFuture.allOf(SkyFactionsReborn.db.createFactionIsland(faction_name, island), IslandAPI.pasteIslandSchematic(player, island.getCenter(world), world.getName(), "faction")).whenComplete((unused, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }

            ObeliskHandler.spawnFactionObelisk(faction_name, island);

            IslandAPI.teleportPlayerToLocation(player, island.getCenter(world));
            SoundUtil.playSound(player, Settings.SOUNDS_ISLAND_CREATE_SUCCESS.getString(), Settings.SOUNDS_ISLAND_CREATE_SUCCESS_PITCH.getInt(), 1f);
            Messages.FACTION_CREATION_SUCCESS.send(player);
        });
    }

    /**
     * Check if a player is in a certain region.
     *
     * @param player     Player to check.
     * @param world      World where region is located.
     * @param regionName Name of region.
     * @return {@link Boolean}
     */
    public static boolean isInRegion(Player player, World world, String regionName) {
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
}
