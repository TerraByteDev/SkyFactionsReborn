package net.skullian.skyfactions.database.tables;

import net.skullian.skyfactions.database.DefaultSchema;
import net.skullian.skyfactions.database.tables.records.NotificationsRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import java.util.Collection;

@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Notifications extends TableImpl<NotificationsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>notifications</code>
     */
    public static final Notifications NOTIFICATIONS = new Notifications();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<NotificationsRecord> getRecordType() {
        return NotificationsRecord.class;
    }

    /**
     * The column <code>notifications.uuid</code>.
     */
    public final TableField<NotificationsRecord, String> UUID = createField(DSL.name("uuid"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>notifications.type</code>.
     */
    public final TableField<NotificationsRecord, String> TYPE = createField(DSL.name("type"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>notifications.replacements</code>.
     */
    public final TableField<NotificationsRecord, String> REPLACEMENTS = createField(DSL.name("replacements"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>notifications.timestamp</code>.
     */
    public final TableField<NotificationsRecord, Long> TIMESTAMP = createField(DSL.name("timestamp"), SQLDataType.BIGINT.nullable(false), this, "");

    private Notifications(Name alias, Table<NotificationsRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private Notifications(Name alias, Table<NotificationsRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>notifications</code> table reference
     */
    public Notifications(String alias) {
        this(DSL.name(alias), NOTIFICATIONS);
    }

    /**
     * Create an aliased <code>notifications</code> table reference
     */
    public Notifications(Name alias) {
        this(alias, NOTIFICATIONS);
    }

    /**
     * Create a <code>notifications</code> table reference
     */
    public Notifications() {
        this(DSL.name("notifications"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public Notifications as(String alias) {
        return new Notifications(DSL.name(alias), this);
    }

    @Override
    public Notifications as(Name alias) {
        return new Notifications(alias, this);
    }

    @Override
    public Notifications as(Table<?> alias) {
        return new Notifications(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Notifications rename(String name) {
        return new Notifications(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Notifications rename(Name name) {
        return new Notifications(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Notifications rename(Table<?> name) {
        return new Notifications(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Notifications where(Condition condition) {
        return new Notifications(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Notifications where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Notifications where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Notifications where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Notifications where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Notifications where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Notifications where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Notifications where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Notifications whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Notifications whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
