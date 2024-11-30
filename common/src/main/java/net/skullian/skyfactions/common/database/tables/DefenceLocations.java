/*
 * This file is generated by jOOQ.
 */
package net.skullian.skyfactions.common.database.tables;


import java.util.Collection;

import net.skullian.skyfactions.common.database.DefaultSchema;
import net.skullian.skyfactions.common.database.tables.records.DefenceLocationsRecord;

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
public class DefenceLocations extends TableImpl<DefenceLocationsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>defence_locations</code>
     */
    public static final DefenceLocations DEFENCE_LOCATIONS = new DefenceLocations();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DefenceLocationsRecord> getRecordType() {
        return DefenceLocationsRecord.class;
    }

    /**
     * The column <code>defence_locations.uuid</code>.
     */
    public final TableField<DefenceLocationsRecord, String> UUID = createField(DSL.name("uuid"), SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>defence_locations.type</code>.
     */
    public final TableField<DefenceLocationsRecord, String> TYPE = createField(DSL.name("type"), SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>defence_locations.factionName</code>.
     */
    public final TableField<DefenceLocationsRecord, String> FACTIONNAME = createField(DSL.name("factionName"), SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>defence_locations.x</code>.
     */
    public final TableField<DefenceLocationsRecord, Integer> X = createField(DSL.name("x"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>defence_locations.y</code>.
     */
    public final TableField<DefenceLocationsRecord, Integer> Y = createField(DSL.name("y"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>defence_locations.z</code>.
     */
    public final TableField<DefenceLocationsRecord, Integer> Z = createField(DSL.name("z"), SQLDataType.INTEGER, this, "");

    private DefenceLocations(Name alias, Table<DefenceLocationsRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private DefenceLocations(Name alias, Table<DefenceLocationsRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>defence_locations</code> table reference
     */
    public DefenceLocations(String alias) {
        this(DSL.name(alias), DEFENCE_LOCATIONS);
    }

    /**
     * Create an aliased <code>defence_locations</code> table reference
     */
    public DefenceLocations(Name alias) {
        this(alias, DEFENCE_LOCATIONS);
    }

    /**
     * Create a <code>defence_locations</code> table reference
     */
    public DefenceLocations() {
        this(DSL.name("defence_locations"), null);
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