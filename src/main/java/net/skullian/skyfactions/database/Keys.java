package net.skullian.skyfactions.database;

import net.skullian.skyfactions.database.tables.*;
import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;

/**
 * A class modelling foreign key relationships and constraints of tables in the
 * default schema.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<Record> FACTIONISLANDS__PK_FACTIONISLANDS = Internal.createUniqueKey(FactionIslands.FACTIONISLANDS, DSL.name("pk_factionIslands"), new TableField[] { FactionIslands.FACTIONISLANDS.ID }, true);
    public static final UniqueKey<Record> FACTIONMEMBERS__PK_FACTIONMEMBERS = Internal.createUniqueKey(FactionMembers.FACTIONMEMBERS, DSL.name("pk_factionMembers"), new TableField[] { FactionMembers.FACTIONMEMBERS.UUID }, true);
    public static final UniqueKey<Record> FACTIONS__PK_FACTIONS = Internal.createUniqueKey(Factions.FACTIONS, DSL.name("pk_factions"), new TableField[] { Factions.FACTIONS.NAME }, true);
    public static final UniqueKey<Record> ISLANDS__PK_ISLANDS = Internal.createUniqueKey(Islands.ISLANDS, DSL.name("pk_islands"), new TableField[] { Islands.ISLANDS.ID }, true);
    public static final UniqueKey<Record> PLAYERDATA__PK_PLAYERDATA = Internal.createUniqueKey(PlayerData.PLAYERDATA, DSL.name("pk_playerData"), new TableField[] { PlayerData.PLAYERDATA.UUID }, true);
    public static final UniqueKey<Record> TRUSTEDPLAYERS__PK_TRUSTEDPLAYERS = Internal.createUniqueKey(TrustedPlayers.TRUSTEDPLAYERS, DSL.name("pk_trustedPlayers"), new TableField[] { TrustedPlayers.TRUSTEDPLAYERS.ISLAND_ID }, true);
}
