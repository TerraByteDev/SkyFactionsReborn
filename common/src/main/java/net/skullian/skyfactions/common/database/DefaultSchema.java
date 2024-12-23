/*
 * This file is generated by jOOQ.
 */
package net.skullian.skyfactions.common.database;


import java.util.Arrays;
import java.util.List;

import net.skullian.skyfactions.common.database.tables.AuditLogs;
import net.skullian.skyfactions.common.database.tables.DefenceLocations;
import net.skullian.skyfactions.common.database.tables.ElectionVotes;
import net.skullian.skyfactions.common.database.tables.FactionBans;
import net.skullian.skyfactions.common.database.tables.FactionElections;
import net.skullian.skyfactions.common.database.tables.FactionInvites;
import net.skullian.skyfactions.common.database.tables.FactionIslands;
import net.skullian.skyfactions.common.database.tables.FactionMembers;
import net.skullian.skyfactions.common.database.tables.Factions;
import net.skullian.skyfactions.common.database.tables.FlywaySchemaHistory;
import net.skullian.skyfactions.common.database.tables.Islands;
import net.skullian.skyfactions.common.database.tables.Notifications;
import net.skullian.skyfactions.common.database.tables.PlayerData;
import net.skullian.skyfactions.common.database.tables.TrustedPlayers;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class DefaultSchema extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>DEFAULT_SCHEMA</code>
     */
    public static final DefaultSchema DEFAULT_SCHEMA = new DefaultSchema();

    /**
     * The table <code>audit_logs</code>.
     */
    public final AuditLogs AUDIT_LOGS = AuditLogs.AUDIT_LOGS;

    /**
     * The table <code>defence_locations</code>.
     */
    public final DefenceLocations DEFENCE_LOCATIONS = DefenceLocations.DEFENCE_LOCATIONS;

    /**
     * The table <code>election_votes</code>.
     */
    public final ElectionVotes ELECTION_VOTES = ElectionVotes.ELECTION_VOTES;

    /**
     * The table <code>faction_bans</code>.
     */
    public final FactionBans FACTION_BANS = FactionBans.FACTION_BANS;

    /**
     * The table <code>faction_elections</code>.
     */
    public final FactionElections FACTION_ELECTIONS = FactionElections.FACTION_ELECTIONS;

    /**
     * The table <code>faction_invites</code>.
     */
    public final FactionInvites FACTION_INVITES = FactionInvites.FACTION_INVITES;

    /**
     * The table <code>faction_islands</code>.
     */
    public final FactionIslands FACTION_ISLANDS = FactionIslands.FACTION_ISLANDS;

    /**
     * The table <code>faction_members</code>.
     */
    public final FactionMembers FACTION_MEMBERS = FactionMembers.FACTION_MEMBERS;

    /**
     * The table <code>factions</code>.
     */
    public final Factions FACTIONS = Factions.FACTIONS;

    /**
     * The table <code>flyway_schema_history</code>.
     */
    public final FlywaySchemaHistory FLYWAY_SCHEMA_HISTORY = FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY;

    /**
     * The table <code>islands</code>.
     */
    public final Islands ISLANDS = Islands.ISLANDS;

    /**
     * The table <code>notifications</code>.
     */
    public final Notifications NOTIFICATIONS = Notifications.NOTIFICATIONS;

    /**
     * The table <code>player_data</code>.
     */
    public final PlayerData PLAYER_DATA = PlayerData.PLAYER_DATA;

    /**
     * The table <code>trusted_players</code>.
     */
    public final TrustedPlayers TRUSTED_PLAYERS = TrustedPlayers.TRUSTED_PLAYERS;

    /**
     * No further instances allowed
     */
    private DefaultSchema() {
        super("", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
            AuditLogs.AUDIT_LOGS,
            DefenceLocations.DEFENCE_LOCATIONS,
            ElectionVotes.ELECTION_VOTES,
            FactionBans.FACTION_BANS,
            FactionElections.FACTION_ELECTIONS,
            FactionInvites.FACTION_INVITES,
            FactionIslands.FACTION_ISLANDS,
            FactionMembers.FACTION_MEMBERS,
            Factions.FACTIONS,
            FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY,
            Islands.ISLANDS,
            Notifications.NOTIFICATIONS,
            PlayerData.PLAYER_DATA,
            TrustedPlayers.TRUSTED_PLAYERS
        );
    }
}
