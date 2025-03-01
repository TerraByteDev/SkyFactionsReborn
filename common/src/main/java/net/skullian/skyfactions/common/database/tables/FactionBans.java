/*
 * This file is generated by jOOQ.
 */
package net.skullian.skyfactions.common.database.tables;


import java.util.Collection;

import net.skullian.skyfactions.common.database.DefaultSchema;
import net.skullian.skyfactions.common.database.tables.records.FactionBansRecord;

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
public class FactionBans extends TableImpl<FactionBansRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>faction_bans</code>
     */
    public static final FactionBans FACTION_BANS = new FactionBans();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<FactionBansRecord> getRecordType() {
        return FactionBansRecord.class;
    }

    /**
     * The column <code>faction_bans.factionName</code>.
     */
    public final TableField<FactionBansRecord, String> FACTIONNAME = createField(DSL.name("factionName"), SQLDataType.VARCHAR(65535), this, "");

    /**
     * The column <code>faction_bans.uuid</code>.
     */
    public final TableField<FactionBansRecord, byte[]> UUID = createField(DSL.name("uuid"), SQLDataType.BLOB, this, "");

    private FactionBans(Name alias, Table<FactionBansRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private FactionBans(Name alias, Table<FactionBansRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>faction_bans</code> table reference
     */
    public FactionBans(String alias) {
        this(DSL.name(alias), FACTION_BANS);
    }

    /**
     * Create an aliased <code>faction_bans</code> table reference
     */
    public FactionBans(Name alias) {
        this(alias, FACTION_BANS);
    }

    /**
     * Create a <code>faction_bans</code> table reference
     */
    public FactionBans() {
        this(DSL.name("faction_bans"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public FactionBans as(String alias) {
        return new FactionBans(DSL.name(alias), this);
    }

    @Override
    public FactionBans as(Name alias) {
        return new FactionBans(alias, this);
    }

    @Override
    public FactionBans as(Table<?> alias) {
        return new FactionBans(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public FactionBans rename(String name) {
        return new FactionBans(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public FactionBans rename(Name name) {
        return new FactionBans(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public FactionBans rename(Table<?> name) {
        return new FactionBans(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public FactionBans where(Condition condition) {
        return new FactionBans(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public FactionBans where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public FactionBans where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public FactionBans where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public FactionBans where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public FactionBans where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public FactionBans where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public FactionBans where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public FactionBans whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public FactionBans whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
