package net.skullian.skyfactions.database.tables;

import net.skullian.skyfactions.database.DefaultSchema;
import net.skullian.skyfactions.database.Keys;
import net.skullian.skyfactions.database.tables.records.FactionmembersRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import java.util.Collection;

@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Factionmembers extends TableImpl<FactionmembersRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>factionMembers</code>
     */
    public static final Factionmembers FACTIONMEMBERS = new Factionmembers();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<FactionmembersRecord> getRecordType() {
        return FactionmembersRecord.class;
    }

    /**
     * The column <code>factionMembers.factionName</code>.
     */
    public final TableField<FactionmembersRecord, String> FACTIONNAME = createField(DSL.name("factionName"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>factionMembers.uuid</code>.
     */
    public final TableField<FactionmembersRecord, String> UUID = createField(DSL.name("uuid"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>factionMembers.rank</code>.
     */
    public final TableField<FactionmembersRecord, String> RANK = createField(DSL.name("rank"), SQLDataType.CLOB.nullable(false), this, "");

    private Factionmembers(Name alias, Table<FactionmembersRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private Factionmembers(Name alias, Table<FactionmembersRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>factionMembers</code> table reference
     */
    public Factionmembers(String alias) {
        this(DSL.name(alias), FACTIONMEMBERS);
    }

    /**
     * Create an aliased <code>factionMembers</code> table reference
     */
    public Factionmembers(Name alias) {
        this(alias, FACTIONMEMBERS);
    }

    /**
     * Create a <code>factionMembers</code> table reference
     */
    public Factionmembers() {
        this(DSL.name("factionMembers"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<FactionmembersRecord> getPrimaryKey() {
        return Keys.FACTIONMEMBERS__PK_FACTIONMEMBERS;
    }

    @Override
    public Factionmembers as(String alias) {
        return new Factionmembers(DSL.name(alias), this);
    }

    @Override
    public Factionmembers as(Name alias) {
        return new Factionmembers(alias, this);
    }

    @Override
    public Factionmembers as(Table<?> alias) {
        return new Factionmembers(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Factionmembers rename(String name) {
        return new Factionmembers(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Factionmembers rename(Name name) {
        return new Factionmembers(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Factionmembers rename(Table<?> name) {
        return new Factionmembers(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Factionmembers where(Condition condition) {
        return new Factionmembers(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Factionmembers where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Factionmembers where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Factionmembers where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Factionmembers where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Factionmembers where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Factionmembers where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Factionmembers where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Factionmembers whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Factionmembers whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
