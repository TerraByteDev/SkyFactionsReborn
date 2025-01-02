/*
 * This file is generated by jOOQ.
 */
package net.skullian.skyfactions.common.database.tables;


import java.util.Collection;

import net.skullian.skyfactions.common.database.DefaultSchema;
import net.skullian.skyfactions.common.database.Keys;
import net.skullian.skyfactions.common.database.tables.records.PlayerDataRecord;

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
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class PlayerData extends TableImpl<PlayerDataRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>player_data</code>
     */
    public static final PlayerData PLAYER_DATA = new PlayerData();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PlayerDataRecord> getRecordType() {
        return PlayerDataRecord.class;
    }

    /**
     * The column <code>player_data.uuid</code>.
     */
    public final TableField<PlayerDataRecord, String> UUID = createField(DSL.name("uuid"), SQLDataType.VARCHAR(36), this, "");

    /**
     * The column <code>player_data.discord_id</code>.
     */
    public final TableField<PlayerDataRecord, String> DISCORD_ID = createField(DSL.name("discord_id"), SQLDataType.VARCHAR(18), this, "");

    /**
     * The column <code>player_data.last_raid</code>.
     */
    public final TableField<PlayerDataRecord, Long> LAST_RAID = createField(DSL.name("last_raid"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>player_data.locale</code>.
     */
    public final TableField<PlayerDataRecord, String> LOCALE = createField(DSL.name("locale"), SQLDataType.VARCHAR(4), this, "");

    private PlayerData(Name alias, Table<PlayerDataRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private PlayerData(Name alias, Table<PlayerDataRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>player_data</code> table reference
     */
    public PlayerData(String alias) {
        this(DSL.name(alias), PLAYER_DATA);
    }

    /**
     * Create an aliased <code>player_data</code> table reference
     */
    public PlayerData(Name alias) {
        this(alias, PLAYER_DATA);
    }

    /**
     * Create a <code>player_data</code> table reference
     */
    public PlayerData() {
        this(DSL.name("player_data"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<PlayerDataRecord> getPrimaryKey() {
        return Keys.PLAYER_DATA__PK_PLAYER_DATA;
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
