package net.skullian.skyfactions.database.tables;

import net.skullian.skyfactions.database.DefaultSchema;
import net.skullian.skyfactions.database.Keys;
import net.skullian.skyfactions.database.tables.records.TrustedplayersRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import java.util.Collection;

@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Trustedplayers extends TableImpl<TrustedplayersRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>trustedPlayers</code>
     */
    public static final Trustedplayers TRUSTEDPLAYERS = new Trustedplayers();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TrustedplayersRecord> getRecordType() {
        return TrustedplayersRecord.class;
    }

    /**
     * The column <code>trustedPlayers.island_id</code>.
     */
    public final TableField<TrustedplayersRecord, Integer> ISLAND_ID = createField(DSL.name("island_id"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>trustedPlayers.uuid</code>.
     */
    public final TableField<TrustedplayersRecord, String> UUID = createField(DSL.name("uuid"), SQLDataType.CLOB.nullable(false), this, "");

    private Trustedplayers(Name alias, Table<TrustedplayersRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private Trustedplayers(Name alias, Table<TrustedplayersRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>trustedPlayers</code> table reference
     */
    public Trustedplayers(String alias) {
        this(DSL.name(alias), TRUSTEDPLAYERS);
    }

    /**
     * Create an aliased <code>trustedPlayers</code> table reference
     */
    public Trustedplayers(Name alias) {
        this(alias, TRUSTEDPLAYERS);
    }

    /**
     * Create a <code>trustedPlayers</code> table reference
     */
    public Trustedplayers() {
        this(DSL.name("trustedPlayers"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<TrustedplayersRecord> getPrimaryKey() {
        return Keys.TRUSTEDPLAYERS__PK_TRUSTEDPLAYERS;
    }

    @Override
    public Trustedplayers as(String alias) {
        return new Trustedplayers(DSL.name(alias), this);
    }

    @Override
    public Trustedplayers as(Name alias) {
        return new Trustedplayers(alias, this);
    }

    @Override
    public Trustedplayers as(Table<?> alias) {
        return new Trustedplayers(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Trustedplayers rename(String name) {
        return new Trustedplayers(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Trustedplayers rename(Name name) {
        return new Trustedplayers(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Trustedplayers rename(Table<?> name) {
        return new Trustedplayers(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Trustedplayers where(Condition condition) {
        return new Trustedplayers(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Trustedplayers where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Trustedplayers where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Trustedplayers where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Trustedplayers where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Trustedplayers where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Trustedplayers where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Trustedplayers where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Trustedplayers whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Trustedplayers whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
