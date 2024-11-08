package net.skullian.skyfactions.database.tables;

import net.skullian.skyfactions.database.DefaultSchema;
import net.skullian.skyfactions.database.tables.records.FactionbansRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import java.util.Collection;

@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Factionbans extends TableImpl<FactionbansRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>factionBans</code>
     */
    public static final Factionbans FACTIONBANS = new Factionbans();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<FactionbansRecord> getRecordType() {
        return FactionbansRecord.class;
    }

    /**
     * The column <code>factionBans.factionName</code>.
     */
    public final TableField<FactionbansRecord, String> FACTIONNAME = createField(DSL.name("factionName"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>factionBans.uuid</code>.
     */
    public final TableField<FactionbansRecord, String> UUID = createField(DSL.name("uuid"), SQLDataType.CLOB.nullable(false), this, "");

    private Factionbans(Name alias, Table<FactionbansRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private Factionbans(Name alias, Table<FactionbansRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>factionBans</code> table reference
     */
    public Factionbans(String alias) {
        this(DSL.name(alias), FACTIONBANS);
    }

    /**
     * Create an aliased <code>factionBans</code> table reference
     */
    public Factionbans(Name alias) {
        this(alias, FACTIONBANS);
    }

    /**
     * Create a <code>factionBans</code> table reference
     */
    public Factionbans() {
        this(DSL.name("factionBans"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public Factionbans as(String alias) {
        return new Factionbans(DSL.name(alias), this);
    }

    @Override
    public Factionbans as(Name alias) {
        return new Factionbans(alias, this);
    }

    @Override
    public Factionbans as(Table<?> alias) {
        return new Factionbans(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Factionbans rename(String name) {
        return new Factionbans(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Factionbans rename(Name name) {
        return new Factionbans(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Factionbans rename(Table<?> name) {
        return new Factionbans(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Factionbans where(Condition condition) {
        return new Factionbans(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Factionbans where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Factionbans where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Factionbans where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Factionbans where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Factionbans where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Factionbans where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Factionbans where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Factionbans whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Factionbans whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}