
package net.skullian.skyfactions.database.tables;


import net.skullian.skyfactions.database.DefaultSchema;
import net.skullian.skyfactions.database.Keys;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import java.util.Collection;


@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class FactionMembers extends TableImpl<Record> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>factionMembers</code>
     */
    public static final FactionMembers FACTIONMEMBERS = new FactionMembers();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>factionMembers.factionName</code>.
     */
    public final TableField<Record, String> FACTIONNAME = createField(DSL.name("factionName"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>factionMembers.uuid</code>.
     */
    public final TableField<Record, String> UUID = createField(DSL.name("uuid"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>factionMembers.rank</code>.
     */
    public final TableField<Record, String> RANK = createField(DSL.name("rank"), SQLDataType.CLOB.nullable(false), this, "");

    private FactionMembers(Name alias, Table<Record> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private FactionMembers(Name alias, Table<Record> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>factionMembers</code> table reference
     */
    public FactionMembers(String alias) {
        this(DSL.name(alias), FACTIONMEMBERS);
    }

    /**
     * Create an aliased <code>factionMembers</code> table reference
     */
    public FactionMembers(Name alias) {
        this(alias, FACTIONMEMBERS);
    }

    /**
     * Create a <code>factionMembers</code> table reference
     */
    public FactionMembers() {
        this(DSL.name("factionMembers"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<Record> getPrimaryKey() {
        return Keys.FACTIONMEMBERS__PK_FACTIONMEMBERS;
    }

    @Override
    public FactionMembers as(String alias) {
        return new FactionMembers(DSL.name(alias), this);
    }

    @Override
    public FactionMembers as(Name alias) {
        return new FactionMembers(alias, this);
    }

    @Override
    public FactionMembers as(Table<?> alias) {
        return new FactionMembers(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public FactionMembers rename(String name) {
        return new FactionMembers(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public FactionMembers rename(Name name) {
        return new FactionMembers(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public FactionMembers rename(Table<?> name) {
        return new FactionMembers(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public FactionMembers where(Condition condition) {
        return new FactionMembers(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public FactionMembers where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public FactionMembers where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public FactionMembers where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public FactionMembers where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public FactionMembers where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public FactionMembers where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public FactionMembers where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public FactionMembers whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public FactionMembers whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
