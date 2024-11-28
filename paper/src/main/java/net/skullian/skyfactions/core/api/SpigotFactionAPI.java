package net.skullian.skyfactions.core.api;

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
import net.skullian.skyfactions.common.api.FactionAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.island.impl.FactionIsland;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyLocation;
import net.skullian.skyfactions.common.util.text.TextUtility;
import net.skullian.skyfactions.common.util.worldborder.BorderPos;
import net.skullian.skyfactions.core.SkyFactionsReborn;
import net.skullian.skyfactions.core.api.adapter.SpigotAdapter;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.config.types.Settings;
import net.skullian.skyfactions.common.defence.Defence;
import net.skullian.skyfactions.core.event.defence.DefencePlacementHandler;
import net.skullian.skyfactions.common.faction.AuditLogType;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.core.island.IslandModificationAction;
import net.skullian.skyfactions.core.island.impl.FactionIsland;
import net.skullian.skyfactions.core.notification.NotificationType;
import net.skullian.skyfactions.core.obelisk.ObeliskHandler;
import net.skullian.skyfactions.core.util.ErrorUtil;
import net.skullian.skyfactions.core.util.SoundUtil;
import net.skullian.skyfactions.core.util.worldborder.BorderPos;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class SpigotFactionAPI extends FactionAPI {

    public static Map<UUID, String> factionCache = new ConcurrentHashMap<>();
    public static Map<String, Faction> factionNameCache = new ConcurrentHashMap<>();

    public static HashSet<String> awaitingDeletion = new HashSet<>();

    @Override
    public void handleFactionWorldBorder(Player player, FactionIsland island) {
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
            SpigotRegionAPI.teleportPlayerToLocation(player, faction.getIsland().getCenter(world.getName()));
            onFactionLoad(faction, player);
        } else {
            Messages.ERROR.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "operation", "teleport to your faction's island", "debug", "WORLD_NOT_EXIST");
        }
    }

    /**
     * Create a new faction.
     *
     * @param player Owner of the faction.
     * @param name   Name of the faction.
     */
    public static void createFaction(Player player, String name) {
        SkyFactionsReborn.getDatabaseManager().getFactionsManager().registerFaction(player, name).whenComplete((ignored, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "create a new Faction", "SQL_FACTION_CREATE", ex);
                // todo disband faction?
                return;
            }
            // Creating & Registering the island in the database before getting the Faction.
            // This stops the Faction retrieval returning null for the island, and causing issues, seeing as the Faction
            // is immediately cached.
            createIsland(player, name);

            SpigotFactionAPI.getFaction(name).whenComplete((faction, exc) -> {
                if (exc != null) {
                    ErrorUtil.handleError(player, "create a new Faction", "SQL_FACTION_CREATE", exc);
                    return;
                }

                faction.createAuditLog(player.getUniqueId(), AuditLogType.FACTION_CREATE, "player_name", player.getName(), "faction_name", name);
                SpigotNotificationAPI.factionInviteStore.put(faction.getName(), 0);
                factionCache.put(player.getUniqueId(), faction.getName());
                factionNameCache.put(faction.getName(), faction);
            });
        });
    }

    public static void disbandFaction(Player player, Faction faction) {
        Region region = getFactionRegion(faction);

        World world = Bukkit.getWorld(Settings.ISLAND_FACTION_WORLD.getString());
        if (world != null && region != null) {
            faction.createBroadcast(player, Messages.FACTION_DISBAND_BROADCAST);
            onFactionDisband(faction);

            CompletableFuture.allOf(
                    SpigotRegionAPI.cutRegion(region),
                    SpigotRegionAPI.removeRegion("sfr_faction_" + faction.getName(), world),
                    SkyFactionsReborn.getDatabaseManager().getFactionsManager().removeFaction(faction.getName()),
                    SkyFactionsReborn.getDatabaseManager().getDefencesManager().removeAllDefences(faction.getName(), true)
            ).whenComplete((ignored, ex) -> {
                if (ex != null) {
                    ErrorUtil.handleError(player, "disband your faction", "SQL_FACTION_DISBAND", ex);
                    return;
                }


                Messages.FACTION_DISBAND_SUCCESS.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
            });
        }
    }

    private static void onFactionDisband(Faction faction) {
        for (OfflinePlayer member : faction.getAllMembers()) {
            factionCache.remove(member.getUniqueId());
            factionNameCache.remove(faction.getName());

            SpigotNotificationAPI.createNotification(member.getUniqueId(), NotificationType.FACTION_DISBANDED);

            if (member.isOnline() && SpigotRegionAPI.isLocationInRegion(member.getPlayer().getLocation(), "sfr_faction_" + faction.getName())) {
                SpigotRegionAPI.teleportPlayerToLocation(member.getPlayer(), SpigotRegionAPI.getHubLocation());
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
        return SkyFactionsReborn.getDatabaseManager().getFactionsManager().isInFaction(player);
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


        return SkyFactionsReborn.getDatabaseManager().getFactionsManager().getFaction(playerUUID).whenComplete((faction, ex) -> {
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

        return SkyFactionsReborn.getDatabaseManager().getFactionsManager().getFaction(name).whenComplete((faction, ex) -> {
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
                Messages.FACTION_NO_NUMBERS.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
                return false;
            } else if (!Settings.FACTION_CREATION_ALLOW_NON_ENGLISH.getBoolean() && !TextUtility.isEnglish(name)) {
                Messages.FACTION_NON_ENGLISH.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
                return false;
            } else if (!Settings.FACTION_CREATION_ALLOW_SYMBOLS.getBoolean() && TextUtility.hasSymbols(name)) {
                Messages.FACTION_NO_SYMBOLS.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
                return false;
            } else if (!SkyApi.getInstance().getDefenceAPI().isFaction(name)) {
                Messages.FACTION_NON_ENGLISH.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
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
                    Messages.FACTION_NAME_PROHIBITED.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
                    return false;
                } else {
                    return true;
                }
            }

        } else {
            Messages.FACTION_NAME_LENGTH_LIMIT.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "min", minimumLength, "max", maximumLength);
            return false;
        }
    }

    private static void createRegion(Player player, FactionIsland island, String worldName, String faction_name) {
        SkyLocation corner1 = island.getPosition1(worldName);
        SkyLocation corner2 = island.getPosition2(worldName);

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager manager = container.get(BukkitAdapter.adapt(Bukkit.getWorld(worldName)));

        BlockVector3 min = BlockVector3.at(corner1.getX(), -64, corner1.getZ());
        BlockVector3 max = BlockVector3.at(corner2.getX(), 320, corner2.getZ());
        ProtectedRegion region = new ProtectedCuboidRegion("sfr_faction_" + faction_name, min, max);
        region.getMembers().addPlayer(player.getUniqueId());

        manager.addRegion(region);
    }

    private static CompletableFuture<Void> createIsland(Player player, String faction_name) {
        FactionIsland island = new FactionIsland(SkyFactionsReborn.getDatabaseManager().getFactionIslandManager().cachedFactionIslandID);
        SkyFactionsReborn.getDatabaseManager().getFactionIslandManager().cachedFactionIslandID++;

        World world = Bukkit.getWorld(Settings.ISLAND_FACTION_WORLD.getString());
        createRegion(player, island, world, faction_name);

        IslandModificationAction action = IslandModificationAction.CREATE;
        action.setId(island.getId());

        return CompletableFuture.allOf(
                SpigotRegionAPI.pasteIslandSchematic(player, island.getCenter(world), world.getName(), "faction"),
                SkyFactionsReborn.getDatabaseManager().getFactionIslandManager().createFactionIsland(faction_name, action)
        ).whenComplete((ignored, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "create your island", "SQL_ISLAND_CREATE", ex);
                return;
            }

            ObeliskHandler.spawnFactionObelisk(faction_name, island);

            handleFactionWorldBorder(player, island);
            SpigotIslandAPI.modifyDefenceOperation(DefenceOperation.DISABLE, player.getUniqueId());
            SpigotRegionAPI.teleportPlayerToLocation(player, island.getCenter(world));

            SoundUtil.playSound(player, Settings.SOUNDS_ISLAND_CREATE_SUCCESS.getString(), Settings.SOUNDS_ISLAND_CREATE_SUCCESS_PITCH.getInt(), 1f);
            Messages.FACTION_CREATION_SUCCESS.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
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
        SkyFactionsReborn.getNpcManager().spawnNPC(faction, faction.getIsland());
        modifyDefenceOperation(DefenceOperation.ENABLE, player);
    }

    public static void modifyDefenceOperation(DefenceOperation operation, Player player) {
        getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            } else if (operation == DefenceOperation.DISABLE && !SpigotRegionAPI.isLocationInRegion(player.getLocation(), "sfr_faction_" + faction.getName())) return;

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

    public static void onFactionRename(String oldName, String newName) {
        if (awaitingDeletion.contains(oldName)) {
            awaitingDeletion.remove(oldName);
            awaitingDeletion.add(newName);
        }

        factionCache.replaceAll((uuid, name) -> name.equals(oldName) ? newName : name);
    }

    public enum DefenceOperation {
        ENABLE,
        DISABLE
    }
}
