package net.skullian.skyfactions.common.api;

import com.sk89q.worldedit.regions.Region;
import lombok.Getter;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.faction.AuditLogType;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.island.IslandModificationAction;
import net.skullian.skyfactions.common.island.impl.FactionIsland;
import net.skullian.skyfactions.common.obelisk.ObeliskHandler;
import net.skullian.skyfactions.common.util.ErrorUtil;
import net.skullian.skyfactions.common.util.SoundUtil;
import net.skullian.skyfactions.common.util.text.TextUtility;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Getter
public abstract class FactionAPI {

    private Map<String, Faction> factionCache = new ConcurrentHashMap<>();
    private HashSet<String> awaitingDeletion = new HashSet<>();

    public void createFaction(Player player, String name) {
        SkyApi.getInstance().getDatabaseManager().getFactionsManager().registerFaction(player, name).whenComplete((ignored, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "create a new Faction", "SQL_FACTION_CREATE", ex);
                // todo disband faction?
                return;
            }
            // Creating & Registering the island in the database before getting the Faction.
            // This stops the Faction retrieval returning null for the island, and causing issues, seeing as the Faction
            // is immediately cached.
            createIsland(player, name);

            SkyApi.getInstance().getFactionAPI().getFaction(name).whenComplete((faction, exc) -> {
                if (exc != null) {
                    ErrorUtil.handleError(player, "create a new Faction", "SQL_FACTION_CREATE", exc);
                    return;
                }

                faction.createAuditLog(player.getUniqueId(), AuditLogType.FACTION_CREATE, "player_name", player.getName(), "faction_name", name);
                SkyApi.getInstance().getNotificationAPI().getFactionInviteStore().put(faction.getName(), 0);
                factionCache.put(faction.getName(), faction);
            });
        });
    }

    public abstract void handleFactionWorldBorder(Player player, FactionIsland island);

    public abstract void teleportToFactionIsland(Player player, Faction faction);

    public abstract void disbandFaction(Player player, Faction faction);

    public abstract void onFactionDisband(Faction faction);

    public abstract Region getFactionRegion(Faction faction);

    public CompletableFuture<Boolean> isInFaction(UUID user) {
        return CompletableFuture.supplyAsync(() -> {
            if (SkyApi.getInstance().getUserManager().isCached(user)) return SkyApi.getInstance().getUserManager().getUser(user).getBelongingFaction().join() != "none";

            return SkyApi.getInstance().getDatabaseManager().getFactionsManager().isInFaction(user).join();
        });
    }

    /**
     * Get the faction from a Faction's name.
     *
     * @param name Name of the faction.
     * @return {@link Faction}
     */
    public CompletableFuture<Faction> getFaction(String name) {
        if (factionCache.containsKey(name)) return CompletableFuture.completedFuture(getCachedFaction(name));

        return SkyApi.getInstance().getDatabaseManager().getFactionsManager().getFaction(name).whenComplete((faction, ex) -> {
            if (ex != null || faction == null) return;

            factionCache.put(faction.getName(), faction);
            for (OfflinePlayer player : faction.getAllMembers()) {
                if (!player.isOnline()) return;
                factionCache.put(faction.getName(), faction);
            }
        });
    }

    /**
     * Get an already cached Faction.
     *
     * @param name Name of the Faction
     * @return {@link Faction}
     */
    @Nullable
    public Faction getCachedFaction(String name) {
        return factionCache.get(name);
    }

    /**
     * @param player Player corresponded to string. Set to null if not needed.
     * @param name   String to check.
     * @return {@link Boolean}
     */
    public boolean hasValidName(Player player, String name) {
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());
        
        int minimumLength = Settings.FACTION_CREATION_MIN_LENGTH.getInt();
        int maximumLength = Settings.FACTION_CREATION_MAX_LENGTH.getInt();
        int length = name.length();
        if (length >= minimumLength && length <= maximumLength) {
            if (!Settings.FACTION_CREATION_ALLOW_NUMBERS.getBoolean() && TextUtility.containsNumbers(name)) {
                Messages.FACTION_NO_NUMBERS.send(player, locale);
                return false;
            } else if (!Settings.FACTION_CREATION_ALLOW_NON_ENGLISH.getBoolean() && !TextUtility.isEnglish(name)) {
                Messages.FACTION_NON_ENGLISH.send(player, locale);
                return false;
            } else if (!Settings.FACTION_CREATION_ALLOW_SYMBOLS.getBoolean() && TextUtility.hasSymbols(name)) {
                Messages.FACTION_NO_SYMBOLS.send(player, locale);
                return false;
            } else if (!SkyApi.getInstance().getDefenceAPI().isFaction(name)) {
                Messages.FACTION_NON_ENGLISH.send(player, locale);
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
                    Messages.FACTION_NAME_PROHIBITED.send(player, locale);
                    return false;
                } else {
                    return true;
                }
            }

        } else {
            Messages.FACTION_NAME_LENGTH_LIMIT.send(player, locale, "min", minimumLength, "max", maximumLength);
            return false;
        }
    }

    public abstract void createRegion(Player player, FactionIsland island, String worldName, String factionName);

    public CompletableFuture<Void> createIsland(Player player, String factionName) {
        FactionIsland island = new FactionIsland(SkyApi.getInstance().getDatabaseManager().getFactionIslandManager().cachedFactionIslandID);
        SkyApi.getInstance().getDatabaseManager().getFactionIslandManager().cachedFactionIslandID++;

        World world = Bukkit.getWorld(Settings.ISLAND_FACTION_WORLD.getString());
        createRegion(player, island, world.getName(), factionName);

        IslandModificationAction action = IslandModificationAction.CREATE;
        action.setId(island.getId());

        return CompletableFuture.allOf(
                SkyApi.getInstance().getRegionAPI().pasteIslandSchematic(player, island.getCenter(world), world.getName(), "faction"),
                SkyApi.getInstance().getDatabaseManager().getFactionIslandManager().createFactionIsland(factionName, action)
        ).whenComplete((ignored, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "create your island", "SQL_ISLAND_CREATE", ex);
                return;
            }

            ObeliskHandler.spawnFactionObelisk(factionName, island);

            handleFactionWorldBorder(player, island);
            SkyApi.getInstance().getIslandAPI().modifyDefenceOperation(DefenceOperation.DISABLE, player.getUniqueId());
            SkyApi.getInstance().getRegionAPI().teleportPlayerToLocation(player.getUniqueId(), island.getCenter(world));

            SoundUtil.playSound(player, Settings.SOUNDS_ISLAND_CREATE_SUCCESS.getString(), Settings.SOUNDS_ISLAND_CREATE_SUCCESS_PITCH.getInt(), 1f);
            Messages.FACTION_CREATION_SUCCESS.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()));
        });
    }

    public abstract boolean isInRegion(Player player, String regionName);

    public void onFactionLoad(Faction faction, Player player) {
        SkyApi.getInstance().getNPCManager().spawnNPC(faction, faction.getIsland());
        modifyDefenceOperation(DefenceOperation.ENABLE, player);
    }

    public void modifyDefenceOperation(DefenceOperation operation, Player player) {
        
    }

    public enum DefenceOperation {
        ENABLE,
        DISABLE
    }

}
