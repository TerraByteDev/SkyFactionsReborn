
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
public class TrustedPlayers extends TableImpl<Record> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>trustedPlayers</code>
     */
    public static final TrustedPlayers TRUSTEDPLAYERS = new TrustedPlayers();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>trustedPlayers.island_id</code>.
     */
    public final TableField<Record, Integer> ISLAND_ID = createField(DSL.name("island_id"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>trustedPlayers.uuid</code>.
     */
    public final TableField<Record, String> UUID = createField(DSL.name("uuid"), SQLDataType.CLOB.nullable(false), this, "");

    private TrustedPlayers(Name alias, Table<Record> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private TrustedPlayers(Name alias, Table<Record> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>trustedPlayers</code> table reference
     */
    public TrustedPlayers(String alias) {
        this(DSL.name(alias), TRUSTEDPLAYERS);
    }

    /**
     * Create an aliased <code>trustedPlayers</code> table reference
     */
    public TrustedPlayers(Name alias) {
        this(alias, TRUSTEDPLAYERS);
    }

    /**
     * Create a <code>trustedPlayers</code> table reference
     */
    public TrustedPlayers() {
        this(DSL.name("trustedPlayers"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<Record> getPrimaryKey() {
        return Keys.TRUSTEDPLAYERS__PK_TRUSTEDPLAYERS;
    }

    @Override
    public TrustedPlayers as(String alias) {
        return new TrustedPlayers(DSL.name(alias), this);
    }

    @Override
    public TrustedPlayers as(Name alias) {
        return new TrustedPlayers(alias, this);
    }

    @Override
    public TrustedPlayers as(Table<?> alias) {
        return new TrustedPlayers(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public TrustedPlayers rename(String name) {
        return new TrustedPlayers(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TrustedPlayers rename(Name name) {
        return new TrustedPlayers(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public TrustedPlayers rename(Table<?> name) {
        return new TrustedPlayers(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TrustedPlayers where(Condition condition) {
        return new TrustedPlayers(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TrustedPlayers where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TrustedPlayers where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TrustedPlayers where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public TrustedPlayers where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public TrustedPlayers where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public TrustedPlayers where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public TrustedPlayers where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TrustedPlayers whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TrustedPlayers whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
