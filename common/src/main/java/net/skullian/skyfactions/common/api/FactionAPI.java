package net.skullian.skyfactions.common.api;

import lombok.Getter;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.defence.Defence;
import net.skullian.skyfactions.common.faction.AuditLogType;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.island.IslandModificationAction;
import net.skullian.skyfactions.common.island.impl.FactionIsland;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import net.skullian.skyfactions.common.util.text.TextUtility;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Getter
public abstract class FactionAPI {

    private final Map<UUID, String> factionUserCache = new ConcurrentHashMap<>();
    private final Map<String, Faction> factionCache = new ConcurrentHashMap<>();

    private final List<String> awaitingDeletion = new ArrayList<>();

    /**
     * Create a new faction.
     *
     * @param player Owner of the faction.
     * @param name   Name of the faction.
     */
    public void createFaction(SkyUser player, String name) {
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

    public abstract void handleFactionWorldBorder(SkyUser player, FactionIsland island);

    /**
     * Teleport the player to their faction's island.
     *
     * @param player Player to teleport.
     */
    public abstract void teleportToFactionIsland(SkyUser player, Faction faction);

    public abstract void disbandFaction(SkyUser player, Faction faction);

    public abstract void onFactionDisband(Faction faction);

    /**
     * Check if a player is in a faction.
     * You can alternatively use {@link #getFaction(String)} and check if the return value is null.
     *
     * @param user Player to check.
     * @return {@link Boolean}
     */
    public CompletableFuture<Boolean> isInFaction(UUID user) {
        return CompletableFuture.supplyAsync(() -> {
            if (factionUserCache.containsKey(user)) return true;

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
            for (SkyUser player : faction.getAllMembers()) {

            }
            for (SkyUser player : faction.getAllMembers()) {
                if (!player.isOnline()) return;
                factionCache.put(faction.getName(), faction);
            }
        });
    }

    /**
     * Get the faction from a player's UUID.
     *
     * @param playerUUID UUID of the player.
     * @return {@link Faction}
     */
    public CompletableFuture<Faction> getFaction(UUID playerUUID) {
        if (factionUserCache.containsKey(playerUUID)) return CompletableFuture.completedFuture(getCachedFaction(factionUserCache.get(playerUUID)));

        return SkyApi.getInstance().getDatabaseManager().getFactionsManager().getFaction(playerUUID).whenComplete((faction, ex) -> {
            if (ex != null || faction == null) return;

            factionCache.put(faction.getName(), faction);
            factionUserCache.put(playerUUID, faction.getName());
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

    @Nullable
    public Faction getCachedFaction(UUID playerUUID) {
        return factionCache.get(factionUserCache.get(playerUUID));
    }

    /**
     * @param player Player corresponded to string. Set to null if not needed.
     * @param name   String to check.
     * @return {@link Boolean}
     */
    public boolean hasValidName(SkyUser player, String name) {
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
                List<String> blacklistedNames = Settings.BLACKLISTED_PHRASES.getList();

                for (String blacklistedName : blacklistedNames) {
                    if (Pattern.compile(blacklistedName).matcher(name).find()) {
                        Messages.BLACKLISTED_PHRASE.send(player, locale);
                        break;
                    }
                }
            }

        } else {
            Messages.FACTION_NAME_LENGTH_LIMIT.send(player, locale, "min", minimumLength, "max", maximumLength);
        }

        return false;
    }

    public abstract void createRegion(SkyUser player, FactionIsland island, String worldName, String factionName);

    public CompletableFuture<Void> createIsland(SkyUser player, String factionName) {
        FactionIsland island = new FactionIsland(SkyApi.getInstance().getDatabaseManager().getFactionIslandManager().cachedFactionIslandID);
        SkyApi.getInstance().getDatabaseManager().getFactionIslandManager().cachedFactionIslandID++;

        String worldName = Settings.ISLAND_FACTION_WORLD.getString();
        createRegion(player, island, worldName, factionName);

        IslandModificationAction action = IslandModificationAction.CREATE;
        action.setId(island.getId());

        return CompletableFuture.allOf(
                SkyApi.getInstance().getRegionAPI().pasteIslandSchematic(player, island.getCenter(worldName), worldName, "faction"),
                SkyApi.getInstance().getDatabaseManager().getFactionIslandManager().createFactionIsland(factionName, action)
        ).whenComplete((ignored, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "create your island", "SQL_ISLAND_CREATE", ex);
                return;
            }

            SkyApi.getInstance().getObeliskAPI().spawnFactionObelisk(factionName, island);

            handleFactionWorldBorder(player, island);
            SkyApi.getInstance().getIslandAPI().modifyDefenceOperation(DefenceOperation.DISABLE, player);
            player.teleport(island.getCenter(worldName));

            SkyApi.getInstance().getSoundAPI().playSound(player, Settings.SOUNDS_ISLAND_CREATE_SUCCESS.getString(), Settings.SOUNDS_ISLAND_CREATE_SUCCESS_PITCH.getInt(), 1f);
            Messages.FACTION_CREATION_SUCCESS.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()));
        });
    }

    /**
     * Check if a player is in a certain region.
     *
     * @param player     Player to check.
     * @param regionName Name of region.
     * @return {@link Boolean}
     */
    public abstract boolean isInRegion(SkyUser player, String regionName);

    public void onFactionLoad(Faction faction, SkyUser player) {
        SkyApi.getInstance().getNPCManager().spawnNPC(faction, faction.getIsland());
        modifyDefenceOperation(DefenceOperation.ENABLE, player);
    }

    public void modifyDefenceOperation(DefenceOperation operation, SkyUser player) {
        getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (ex != null) return;
            if (operation == DefenceOperation.DISABLE && !SkyApi.getInstance().getRegionAPI().isLocationInRegion(player.getLocation(), "sfr_faction_" + faction.getName())) return;

            List<Defence> defences = SkyApi.getInstance().getDefenceAPI().getLoadedFactionDefences().get(faction.getName());
            if (defences != null && !defences.isEmpty()) {
                for (Defence defence : defences)
                    if (operation == DefenceOperation.ENABLE) defence.onLoad(faction.getName());
                        else defence.disable();
            }
        });
    }

    public void onFactionRename(String oldName, String newName) {
        if (awaitingDeletion.contains(oldName)) {
            awaitingDeletion.remove(oldName);
            awaitingDeletion.add(newName);
        }

        factionUserCache.replaceAll((uuid, name) -> name.equals(oldName) ? newName : name);
    }

    public abstract void removeMemberFromRegion(SkyUser user, Faction faction);

    public abstract void addMemberToRegion(SkyUser user, Faction faction);

    public enum DefenceOperation {
        ENABLE,
        DISABLE
    }

}
