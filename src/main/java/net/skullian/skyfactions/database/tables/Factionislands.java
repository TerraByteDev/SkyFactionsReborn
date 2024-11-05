package net.skullian.skyfactions.database.tables;

import net.skullian.skyfactions.database.DefaultSchema;
import net.skullian.skyfactions.database.Keys;
import net.skullian.skyfactions.database.tables.records.FactionislandsRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import java.util.Collection;

@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Factionislands extends TableImpl<FactionislandsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>factionIslands</code>
     */
    public static final Factionislands FACTIONISLANDS = new Factionislands();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<FactionislandsRecord> getRecordType() {
        return FactionislandsRecord.class;
    }

    /**
     * The column <code>factionIslands.id</code>.
     */
    public final TableField<FactionislandsRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>factionIslands.factionName</code>.
     */
    public final TableField<FactionislandsRecord, String> FACTIONNAME = createField(DSL.name("factionName"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>factionIslands.runes</code>.
     */
    public final TableField<FactionislandsRecord, Integer> RUNES = createField(DSL.name("runes"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>factionIslands.defenceCount</code>.
     */
    public final TableField<FactionislandsRecord, Integer> DEFENCECOUNT = createField(DSL.name("defenceCount"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>factionIslands.gems</code>.
     */
    public final TableField<FactionislandsRecord, Integer> GEMS = createField(DSL.name("gems"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>factionIslands.last_raided</code>.
     */
    public final TableField<FactionislandsRecord, Integer> LAST_RAIDED = createField(DSL.name("last_raided"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>factionIslands.last_raider</code>.
     */
    public final TableField<FactionislandsRecord, String> LAST_RAIDER = createField(DSL.name("last_raider"), SQLDataType.CLOB.nullable(false), this, "");

    private Factionislands(Name alias, Table<FactionislandsRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private Factionislands(Name alias, Table<FactionislandsRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>factionIslands</code> table reference
     */
    public Factionislands(String alias) {
        this(DSL.name(alias), FACTIONISLANDS);
    }

    /**
     * Create an aliased <code>factionIslands</code> table reference
     */
    public Factionislands(Name alias) {
        this(alias, FACTIONISLANDS);
    }

    /**
     * Create a <code>factionIslands</code> table reference
     */
    public Factionislands() {
        this(DSL.name("factionIslands"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<FactionislandsRecord> getPrimaryKey() {
        return Keys.FACTIONISLANDS__PK_FACTIONISLANDS;
    }

    @Override
    public Factionislands as(String alias) {
        return new Factionislands(DSL.name(alias), this);
    }

    @Override
    public Factionislands as(Name alias) {
        return new Factionislands(alias, this);
    }

    @Override
    public Factionislands as(Table<?> alias) {
        return new Factionislands(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Factionislands rename(String name) {
        return new Factionislands(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Factionislands rename(Name name) {
        return new Factionislands(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Factionislands rename(Table<?> name) {
        return new Factionislands(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Factionislands where(Condition condition) {
        return new Factionislands(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Factionislands where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Factionislands where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Factionislands where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Factionislands where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Factionislands where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Factionislands where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Factionislands where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Factionislands whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Factionislands whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
