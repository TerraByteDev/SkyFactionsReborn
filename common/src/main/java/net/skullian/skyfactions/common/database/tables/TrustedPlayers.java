/*
 * This file is generated by jOOQ.
 */
package net.skullian.skyfactions.common.database.tables;


import java.util.Collection;

import net.skullian.skyfactions.common.database.DefaultSchema;
import net.skullian.skyfactions.common.database.tables.records.TrustedPlayersRecord;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.PlainSQL;
import org.jooq.QueryPart;
import org.jooq.SQL;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.Stringly;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class TrustedPlayers extends TableImpl<TrustedPlayersRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>trusted_players</code>
     */
    public static final TrustedPlayers TRUSTED_PLAYERS = new TrustedPlayers();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TrustedPlayersRecord> getRecordType() {
        return TrustedPlayersRecord.class;
    }

    /**
     * The column <code>trusted_players.island_id</code>.
     */
    public final TableField<TrustedPlayersRecord, Integer> ISLAND_ID = createField(DSL.name("island_id"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>trusted_players.uuid</code>.
     */
    public final TableField<TrustedPlayersRecord, String> UUID = createField(DSL.name("uuid"), SQLDataType.VARCHAR(36), this, "");

    private TrustedPlayers(Name alias, Table<TrustedPlayersRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private TrustedPlayers(Name alias, Table<TrustedPlayersRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>trusted_players</code> table reference
     */
    public TrustedPlayers(String alias) {
        this(DSL.name(alias), TRUSTED_PLAYERS);
    }

    /**
     * Create an aliased <code>trusted_players</code> table reference
     */
    public TrustedPlayers(Name alias) {
        this(alias, TRUSTED_PLAYERS);
    }

    /**
     * Create a <code>trusted_players</code> table reference
     */
    public TrustedPlayers() {
        this(DSL.name("trusted_players"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public TrustedPlayers as(String alias) {
        return new TrustedPlayers(DSL.name(alias), this);
    }

    @Override
    public TrustedPlayers as(Name alias) {
        return new TrustedPlayers(alias, this);
    }

    @Override
    public TrustedPlayers as(Table<?> alias) {
        return new TrustedPlayers(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public TrustedPlayers rename(String name) {
        return new TrustedPlayers(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TrustedPlayers rename(Name name) {
        return new TrustedPlayers(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public TrustedPlayers rename(Table<?> name) {
        return new TrustedPlayers(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TrustedPlayers where(Condition condition) {
        return new TrustedPlayers(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TrustedPlayers where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TrustedPlayers where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TrustedPlayers where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public TrustedPlayers where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public TrustedPlayers where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public TrustedPlayers where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public TrustedPlayers where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TrustedPlayers whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TrustedPlayers whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
