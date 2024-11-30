/*
 * This file is generated by jOOQ.
 */
package net.skullian.skyfactions.common.database;


import net.skullian.skyfactions.common.database.tables.FactionElections;
import net.skullian.skyfactions.common.database.tables.FlywaySchemaHistory;

import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling indexes of tables in the default schema.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index FACTIONELECTIONSFACTIONNAMEINDEX = Internal.createIndex(DSL.name("factionElectionsFactionNameIndex"), FactionElections.FACTION_ELECTIONS, new OrderField[] { FactionElections.FACTION_ELECTIONS.FACTIONNAME }, false);
    public static final Index FLYWAY_SCHEMA_HISTORY_S_IDX = Internal.createIndex(DSL.name("flyway_schema_history_s_idx"), FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY, new OrderField[] { FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY.SUCCESS }, false);
}