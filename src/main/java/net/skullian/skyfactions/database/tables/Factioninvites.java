package net.skullian.skyfactions.database.tables;

import net.skullian.skyfactions.database.DefaultSchema;
import net.skullian.skyfactions.database.tables.records.FactioninvitesRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import java.util.Collection;

@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Factioninvites extends TableImpl<FactioninvitesRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>factionInvites</code>
     */
    public static final Factioninvites FACTIONINVITES = new Factioninvites();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<FactioninvitesRecord> getRecordType() {
        return FactioninvitesRecord.class;
    }

    /**
     * The column <code>factionInvites.factionName</code>.
     */
    public final TableField<FactioninvitesRecord, String> FACTIONNAME = createField(DSL.name("factionName"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>factionInvites.uuid</code>.
     */
    public final TableField<FactioninvitesRecord, String> UUID = createField(DSL.name("uuid"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>factionInvites.inviter</code>.
     */
    public final TableField<FactioninvitesRecord, String> INVITER = createField(DSL.name("inviter"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>factionInvites.type</code>.
     */
    public final TableField<FactioninvitesRecord, String> TYPE = createField(DSL.name("type"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>factionInvites.accepted</code>.
     */
    public final TableField<FactioninvitesRecord, Boolean> ACCEPTED = createField(DSL.name("accepted"), SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>factionInvites.timestamp</code>.
     */
    public final TableField<FactioninvitesRecord, Long> TIMESTAMP = createField(DSL.name("timestamp"), SQLDataType.BIGINT.nullable(false), this, "");

    private Factioninvites(Name alias, Table<FactioninvitesRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private Factioninvites(Name alias, Table<FactioninvitesRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>factionInvites</code> table reference
     */
    public Factioninvites(String alias) {
        this(DSL.name(alias), FACTIONINVITES);
    }

    /**
     * Create an aliased <code>factionInvites</code> table reference
     */
    public Factioninvites(Name alias) {
        this(alias, FACTIONINVITES);
    }

    /**
     * Create a <code>factionInvites</code> table reference
     */
    public Factioninvites() {
        this(DSL.name("factionInvites"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public Factioninvites as(String alias) {
        return new Factioninvites(DSL.name(alias), this);
    }

    @Override
    public Factioninvites as(Name alias) {
        return new Factioninvites(alias, this);
    }

    @Override
    public Factioninvites as(Table<?> alias) {
        return new Factioninvites(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Factioninvites rename(String name) {
        return new Factioninvites(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Factioninvites rename(Name name) {
        return new Factioninvites(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Factioninvites rename(Table<?> name) {
        return new Factioninvites(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Factioninvites where(Condition condition) {
        return new Factioninvites(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Factioninvites where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Factioninvites where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Factioninvites where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Factioninvites where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Factioninvites where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Factioninvites where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Factioninvites where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Factioninvites whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Factioninvites whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}