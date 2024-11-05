package net.skullian.skyfactions.database.tables;

import net.skullian.skyfactions.database.DefaultSchema;
import net.skullian.skyfactions.database.Keys;
import net.skullian.skyfactions.database.tables.records.PlayerdataRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import java.util.Collection;

@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Playerdata extends TableImpl<PlayerdataRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>playerData</code>
     */
    public static final Playerdata PLAYERDATA = new Playerdata();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PlayerdataRecord> getRecordType() {
        return PlayerdataRecord.class;
    }

    /**
     * The column <code>playerData.uuid</code>.
     */
    public final TableField<PlayerdataRecord, String> UUID = createField(DSL.name("uuid"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>playerData.faction</code>.
     */
    public final TableField<PlayerdataRecord, String> FACTION = createField(DSL.name("faction"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>playerData.discord_id</code>.
     */
    public final TableField<PlayerdataRecord, String> DISCORD_ID = createField(DSL.name("discord_id"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>playerData.last_raid</code>.
     */
    public final TableField<PlayerdataRecord, Long> LAST_RAID = createField(DSL.name("last_raid"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>playerData.locale</code>.
     */
    public final TableField<PlayerdataRecord, String> LOCALE = createField(DSL.name("locale"), SQLDataType.CLOB.nullable(false), this, "");

    private Playerdata(Name alias, Table<PlayerdataRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private Playerdata(Name alias, Table<PlayerdataRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>playerData</code> table reference
     */
    public Playerdata(String alias) {
        this(DSL.name(alias), PLAYERDATA);
    }

    /**
     * Create an aliased <code>playerData</code> table reference
     */
    public Playerdata(Name alias) {
        this(alias, PLAYERDATA);
    }

    /**
     * Create a <code>playerData</code> table reference
     */
    public Playerdata() {
        this(DSL.name("playerData"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<PlayerdataRecord> getPrimaryKey() {
        return Keys.PLAYERDATA__PK_PLAYERDATA;
    }

    @Override
    public Playerdata as(String alias) {
        return new Playerdata(DSL.name(alias), this);
    }

    @Override
    public Playerdata as(Name alias) {
        return new Playerdata(alias, this);
    }

    @Override
    public Playerdata as(Table<?> alias) {
        return new Playerdata(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Playerdata rename(String name) {
        return new Playerdata(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Playerdata rename(Name name) {
        return new Playerdata(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Playerdata rename(Table<?> name) {
        return new Playerdata(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Playerdata where(Condition condition) {
        return new Playerdata(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Playerdata where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Playerdata where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Playerdata where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Playerdata where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Playerdata where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Playerdata where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Playerdata where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Playerdata whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Playerdata whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
