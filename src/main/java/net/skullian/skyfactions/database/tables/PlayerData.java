
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
public class PlayerData extends TableImpl<Record> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>playerData</code>
     */
    public static final PlayerData PLAYERDATA = new PlayerData();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>playerData.uuid</code>.
     */
    public final TableField<Record, String> UUID = createField(DSL.name("uuid"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>playerData.faction</code>.
     */
    public final TableField<Record, String> FACTION = createField(DSL.name("faction"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>playerData.discord_id</code>.
     */
    public final TableField<Record, String> DISCORD_ID = createField(DSL.name("discord_id"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>playerData.last_raid</code>.
     */
    public final TableField<Record, Integer> LAST_RAID = createField(DSL.name("last_raid"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>playerData.locale</code>.
     */
    public final TableField<Record, String> LOCALE = createField(DSL.name("locale"), SQLDataType.CLOB.nullable(false), this, "");

    private PlayerData(Name alias, Table<Record> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private PlayerData(Name alias, Table<Record> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>playerData</code> table reference
     */
    public PlayerData(String alias) {
        this(DSL.name(alias), PLAYERDATA);
    }

    /**
     * Create an aliased <code>playerData</code> table reference
     */
    public PlayerData(Name alias) {
        this(alias, PLAYERDATA);
    }

    /**
     * Create a <code>playerData</code> table reference
     */
    public PlayerData() {
        this(DSL.name("playerData"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<Record> getPrimaryKey() {
        return Keys.PLAYERDATA__PK_PLAYERDATA;
    }

    @Override
    public PlayerData as(String alias) {
        return new PlayerData(DSL.name(alias), this);
    }

    @Override
    public PlayerData as(Name alias) {
        return new PlayerData(alias, this);
    }

    @Override
    public PlayerData as(Table<?> alias) {
        return new PlayerData(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public PlayerData rename(String name) {
        return new PlayerData(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public PlayerData rename(Name name) {
        return new PlayerData(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public PlayerData rename(Table<?> name) {
        return new PlayerData(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public PlayerData where(Condition condition) {
        return new PlayerData(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public PlayerData where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public PlayerData where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public PlayerData where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public PlayerData where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public PlayerData where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public PlayerData where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public PlayerData where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public PlayerData whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public PlayerData whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
