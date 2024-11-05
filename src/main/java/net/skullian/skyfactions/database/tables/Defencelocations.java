package net.skullian.skyfactions.database.tables;
import net.skullian.skyfactions.database.DefaultSchema;
import net.skullian.skyfactions.database.tables.records.DefencelocationsRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import java.util.Collection;

@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Defencelocations extends TableImpl<DefencelocationsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>defenceLocations</code>
     */
    public static final Defencelocations DEFENCELOCATIONS = new Defencelocations();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DefencelocationsRecord> getRecordType() {
        return DefencelocationsRecord.class;
    }

    /**
     * The column <code>defenceLocations.uuid</code>.
     */
    public final TableField<DefencelocationsRecord, String> UUID = createField(DSL.name("uuid"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>defenceLocations.type</code>.
     */
    public final TableField<DefencelocationsRecord, String> TYPE = createField(DSL.name("type"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>defenceLocations.factionName</code>.
     */
    public final TableField<DefencelocationsRecord, String> FACTIONNAME = createField(DSL.name("factionName"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>defenceLocations.x</code>.
     */
    public final TableField<DefencelocationsRecord, Integer> X = createField(DSL.name("x"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>defenceLocations.y</code>.
     */
    public final TableField<DefencelocationsRecord, Integer> Y = createField(DSL.name("y"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>defenceLocations.z</code>.
     */
    public final TableField<DefencelocationsRecord, Integer> Z = createField(DSL.name("z"), SQLDataType.INTEGER.nullable(false), this, "");

    private Defencelocations(Name alias, Table<DefencelocationsRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private Defencelocations(Name alias, Table<DefencelocationsRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>defenceLocations</code> table reference
     */
    public Defencelocations(String alias) {
        this(DSL.name(alias), DEFENCELOCATIONS);
    }

    /**
     * Create an aliased <code>defenceLocations</code> table reference
     */
    public Defencelocations(Name alias) {
        this(alias, DEFENCELOCATIONS);
    }

    /**
     * Create a <code>defenceLocations</code> table reference
     */
    public Defencelocations() {
        this(DSL.name("defenceLocations"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public Defencelocations as(String alias) {
        return new Defencelocations(DSL.name(alias), this);
    }

    @Override
    public Defencelocations as(Name alias) {
        return new Defencelocations(alias, this);
    }

    @Override
    public Defencelocations as(Table<?> alias) {
        return new Defencelocations(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Defencelocations rename(String name) {
        return new Defencelocations(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Defencelocations rename(Name name) {
        return new Defencelocations(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Defencelocations rename(Table<?> name) {
        return new Defencelocations(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Defencelocations where(Condition condition) {
        return new Defencelocations(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Defencelocations where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Defencelocations where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Defencelocations where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Defencelocations where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Defencelocations where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Defencelocations where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Defencelocations where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Defencelocations whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Defencelocations whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
