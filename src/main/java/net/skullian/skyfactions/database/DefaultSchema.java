package net.skullian.skyfactions.database;

import net.skullian.skyfactions.database.tables.*;
import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class DefaultSchema extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>DEFAULT_SCHEMA</code>
     */
    public static final DefaultSchema DEFAULT_SCHEMA = new DefaultSchema();

    /**
     * The table <code>auditLogs</code>.
     */
    public final AuditLogs AUDITLOGS = AuditLogs.AUDITLOGS;

    /**
     * The table <code>defenceLocations</code>.
     */
    public final DefenceLocations DEFENCELOCATIONS = DefenceLocations.DEFENCELOCATIONS;

    /**
     * The table <code>factionBans</code>.
     */
    public final FactionBans FACTIONBANS = FactionBans.FACTIONBANS;

    /**
     * The table <code>factionInvites</code>.
     */
    public final FactionInvites FACTIONINVITES = FactionInvites.FACTIONINVITES;

    /**
     * The table <code>factionIslands</code>.
     */
    public final FactionIslands FACTIONISLANDS = FactionIslands.FACTIONISLANDS;

    /**
     * The table <code>factionMembers</code>.
     */
    public final FactionMembers FACTIONMEMBERS = FactionMembers.FACTIONMEMBERS;

    /**
     * The table <code>factions</code>.
     */
    public final Factions FACTIONS = Factions.FACTIONS;

    /**
     * The table <code>islands</code>.
     */
    public final Islands ISLANDS = Islands.ISLANDS;

    /**
     * The table <code>notifications</code>.
     */
    public final Notifications NOTIFICATIONS = Notifications.NOTIFICATIONS;

    /**
     * The table <code>playerData</code>.
     */
    public final PlayerData PLAYERDATA = PlayerData.PLAYERDATA;

    /**
     * The table <code>trustedPlayers</code>.
     */
    public final TrustedPlayers TRUSTEDPLAYERS = TrustedPlayers.TRUSTEDPLAYERS;

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
            AuditLogs.AUDITLOGS,
            DefenceLocations.DEFENCELOCATIONS,
            FactionBans.FACTIONBANS,
            FactionInvites.FACTIONINVITES,
            FactionIslands.FACTIONISLANDS,
            FactionMembers.FACTIONMEMBERS,
            Factions.FACTIONS,
            Islands.ISLANDS,
            Notifications.NOTIFICATIONS,
            PlayerData.PLAYERDATA,
            TrustedPlayers.TRUSTEDPLAYERS
        );
    }
}
