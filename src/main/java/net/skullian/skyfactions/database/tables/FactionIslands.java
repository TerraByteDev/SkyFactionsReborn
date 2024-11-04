
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
public class FactionIslands extends TableImpl<Record> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>factionIslands</code>
     */
    public static final FactionIslands FACTIONISLANDS = new FactionIslands();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>factionIslands.id</code>.
     */
    public final TableField<Record, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>factionIslands.factionName</code>.
     */
    public final TableField<Record, String> FACTIONNAME = createField(DSL.name("factionName"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>factionIslands.runes</code>.
     */
    public final TableField<Record, Integer> RUNES = createField(DSL.name("runes"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>factionIslands.defenceCount</code>.
     */
    public final TableField<Record, Integer> DEFENCECOUNT = createField(DSL.name("defenceCount"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>factionIslands.gems</code>.
     */
    public final TableField<Record, Integer> GEMS = createField(DSL.name("gems"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>factionIslands.last_raided</code>.
     */
    public final TableField<Record, Integer> LAST_RAIDED = createField(DSL.name("last_raided"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>factionIslands.last_raider</code>.
     */
    public final TableField<Record, String> LAST_RAIDER = createField(DSL.name("last_raider"), SQLDataType.CLOB.nullable(false), this, "");

    private FactionIslands(Name alias, Table<Record> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private FactionIslands(Name alias, Table<Record> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>factionIslands</code> table reference
     */
    public FactionIslands(String alias) {
        this(DSL.name(alias), FACTIONISLANDS);
    }

    /**
     * Create an aliased <code>factionIslands</code> table reference
     */
    public FactionIslands(Name alias) {
        this(alias, FACTIONISLANDS);
    }

    /**
     * Create a <code>factionIslands</code> table reference
     */
    public FactionIslands() {
        this(DSL.name("factionIslands"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<Record> getPrimaryKey() {
        return Keys.FACTIONISLANDS__PK_FACTIONISLANDS;
    }

    @Override
    public FactionIslands as(String alias) {
        return new FactionIslands(DSL.name(alias), this);
    }

    @Override
    public FactionIslands as(Name alias) {
        return new FactionIslands(alias, this);
    }

    @Override
    public FactionIslands as(Table<?> alias) {
        return new FactionIslands(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public FactionIslands rename(String name) {
        return new FactionIslands(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public FactionIslands rename(Name name) {
        return new FactionIslands(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public FactionIslands rename(Table<?> name) {
        return new FactionIslands(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public FactionIslands where(Condition condition) {
        return new FactionIslands(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public FactionIslands where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public FactionIslands where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public FactionIslands where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public FactionIslands where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public FactionIslands where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public FactionIslands where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public FactionIslands where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public FactionIslands whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public FactionIslands whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
