
package net.skullian.skyfactions.database.tables;


import net.skullian.skyfactions.database.DefaultSchema;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import java.util.Collection;


@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class FactionBans extends TableImpl<Record> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>factionBans</code>
     */
    public static final FactionBans FACTIONBANS = new FactionBans();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>factionBans.factionName</code>.
     */
    public final TableField<Record, String> FACTIONNAME = createField(DSL.name("factionName"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>factionBans.uuid</code>.
     */
    public final TableField<Record, String> UUID = createField(DSL.name("uuid"), SQLDataType.CLOB.nullable(false), this, "");

    private FactionBans(Name alias, Table<Record> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private FactionBans(Name alias, Table<Record> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>factionBans</code> table reference
     */
    public FactionBans(String alias) {
        this(DSL.name(alias), FACTIONBANS);
    }

    /**
     * Create an aliased <code>factionBans</code> table reference
     */
    public FactionBans(Name alias) {
        this(alias, FACTIONBANS);
    }

    /**
     * Create a <code>factionBans</code> table reference
     */
    public FactionBans() {
        this(DSL.name("factionBans"), null);
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
