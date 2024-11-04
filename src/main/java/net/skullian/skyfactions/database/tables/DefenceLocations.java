
package net.skullian.skyfactions.database.tables;


import net.skullian.skyfactions.database.DefaultSchema;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import java.util.Collection;


@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class DefenceLocations extends TableImpl<Record> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>defenceLocations</code>
     */
    public static final DefenceLocations DEFENCELOCATIONS = new DefenceLocations();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>defenceLocations.uuid</code>.
     */
    public final TableField<Record, String> UUID = createField(DSL.name("uuid"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>defenceLocations.type</code>.
     */
    public final TableField<Record, String> TYPE = createField(DSL.name("type"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>defenceLocations.factionName</code>.
     */
    public final TableField<Record, String> FACTIONNAME = createField(DSL.name("factionName"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>defenceLocations.x</code>.
     */
    public final TableField<Record, Integer> X = createField(DSL.name("x"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>defenceLocations.y</code>.
     */
    public final TableField<Record, Integer> Y = createField(DSL.name("y"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>defenceLocations.z</code>.
     */
    public final TableField<Record, Integer> Z = createField(DSL.name("z"), SQLDataType.INTEGER.nullable(false), this, "");

    private DefenceLocations(Name alias, Table<Record> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private DefenceLocations(Name alias, Table<Record> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>defenceLocations</code> table reference
     */
    public DefenceLocations(String alias) {
        this(DSL.name(alias), DEFENCELOCATIONS);
    }

    /**
     * Create an aliased <code>defenceLocations</code> table reference
     */
    public DefenceLocations(Name alias) {
        this(alias, DEFENCELOCATIONS);
    }

    /**
     * Create a <code>defenceLocations</code> table reference
     */
    public DefenceLocations() {
        this(DSL.name("defenceLocations"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public DefenceLocations as(String alias) {
        return new DefenceLocations(DSL.name(alias), this);
    }

    @Override
    public DefenceLocations as(Name alias) {
        return new DefenceLocations(alias, this);
    }

    @Override
    public DefenceLocations as(Table<?> alias) {
        return new DefenceLocations(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public DefenceLocations rename(String name) {
        return new DefenceLocations(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public DefenceLocations rename(Name name) {
        return new DefenceLocations(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public DefenceLocations rename(Table<?> name) {
        return new DefenceLocations(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public DefenceLocations where(Condition condition) {
        return new DefenceLocations(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public DefenceLocations where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public DefenceLocations where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public DefenceLocations where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public DefenceLocations where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public DefenceLocations where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public DefenceLocations where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public DefenceLocations where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public DefenceLocations whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public DefenceLocations whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
