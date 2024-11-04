
package net.skullian.skyfactions.database.tables;


import net.skullian.skyfactions.database.DefaultSchema;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import java.util.Collection;


@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class FactionInvites extends TableImpl<Record> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>factionInvites</code>
     */
    public static final FactionInvites FACTIONINVITES = new FactionInvites();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>factionInvites.factionName</code>.
     */
    public final TableField<Record, String> FACTIONNAME = createField(DSL.name("factionName"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>factionInvites.uuid</code>.
     */
    public final TableField<Record, String> UUID = createField(DSL.name("uuid"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>factionInvites.inviter</code>.
     */
    public final TableField<Record, String> INVITER = createField(DSL.name("inviter"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>factionInvites.type</code>.
     */
    public final TableField<Record, String> TYPE = createField(DSL.name("type"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>factionInvites.accepted</code>.
     */
    public final TableField<Record, Integer> ACCEPTED = createField(DSL.name("accepted"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>factionInvites.timestamp</code>.
     */
    public final TableField<Record, Integer> TIMESTAMP = createField(DSL.name("timestamp"), SQLDataType.INTEGER.nullable(false), this, "");

    private FactionInvites(Name alias, Table<Record> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private FactionInvites(Name alias, Table<Record> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>factionInvites</code> table reference
     */
    public FactionInvites(String alias) {
        this(DSL.name(alias), FACTIONINVITES);
    }

    /**
     * Create an aliased <code>factionInvites</code> table reference
     */
    public FactionInvites(Name alias) {
        this(alias, FACTIONINVITES);
    }

    /**
     * Create a <code>factionInvites</code> table reference
     */
    public FactionInvites() {
        this(DSL.name("factionInvites"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public FactionInvites as(String alias) {
        return new FactionInvites(DSL.name(alias), this);
    }

    @Override
    public FactionInvites as(Name alias) {
        return new FactionInvites(alias, this);
    }

    @Override
    public FactionInvites as(Table<?> alias) {
        return new FactionInvites(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public FactionInvites rename(String name) {
        return new FactionInvites(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public FactionInvites rename(Name name) {
        return new FactionInvites(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public FactionInvites rename(Table<?> name) {
        return new FactionInvites(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public FactionInvites where(Condition condition) {
        return new FactionInvites(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public FactionInvites where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public FactionInvites where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public FactionInvites where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public FactionInvites where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public FactionInvites where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public FactionInvites where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public FactionInvites where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public FactionInvites whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public FactionInvites whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
