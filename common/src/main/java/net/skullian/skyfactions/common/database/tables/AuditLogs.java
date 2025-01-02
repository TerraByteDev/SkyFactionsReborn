/*
 * This file is generated by jOOQ.
 */
package net.skullian.skyfactions.common.database.tables;


import java.util.Collection;

import net.skullian.skyfactions.common.database.DefaultSchema;
import net.skullian.skyfactions.common.database.tables.records.AuditLogsRecord;

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
public class AuditLogs extends TableImpl<AuditLogsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>audit_logs</code>
     */
    public static final AuditLogs AUDIT_LOGS = new AuditLogs();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AuditLogsRecord> getRecordType() {
        return AuditLogsRecord.class;
    }

    /**
     * The column <code>audit_logs.factionName</code>.
     */
    public final TableField<AuditLogsRecord, String> FACTIONNAME = createField(DSL.name("factionName"), SQLDataType.VARCHAR(65535), this, "");

    /**
     * The column <code>audit_logs.type</code>.
     */
    public final TableField<AuditLogsRecord, String> TYPE = createField(DSL.name("type"), SQLDataType.VARCHAR(128), this, "");

    /**
     * The column <code>audit_logs.uuid</code>.
     */
    public final TableField<AuditLogsRecord, byte[]> UUID = createField(DSL.name("uuid"), SQLDataType.BLOB, this, "");

    /**
     * The column <code>audit_logs.replacements</code>.
     */
    public final TableField<AuditLogsRecord, String> REPLACEMENTS = createField(DSL.name("replacements"), SQLDataType.VARCHAR(5120), this, "");

    /**
     * The column <code>audit_logs.timestamp</code>.
     */
    public final TableField<AuditLogsRecord, Long> TIMESTAMP = createField(DSL.name("timestamp"), SQLDataType.BIGINT, this, "");

    private AuditLogs(Name alias, Table<AuditLogsRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private AuditLogs(Name alias, Table<AuditLogsRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>audit_logs</code> table reference
     */
    public AuditLogs(String alias) {
        this(DSL.name(alias), AUDIT_LOGS);
    }

    /**
     * Create an aliased <code>audit_logs</code> table reference
     */
    public AuditLogs(Name alias) {
        this(alias, AUDIT_LOGS);
    }

    /**
     * Create a <code>audit_logs</code> table reference
     */
    public AuditLogs() {
        this(DSL.name("audit_logs"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public AuditLogs as(String alias) {
        return new AuditLogs(DSL.name(alias), this);
    }

    @Override
    public AuditLogs as(Name alias) {
        return new AuditLogs(alias, this);
    }

    @Override
    public AuditLogs as(Table<?> alias) {
        return new AuditLogs(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public AuditLogs rename(String name) {
        return new AuditLogs(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public AuditLogs rename(Name name) {
        return new AuditLogs(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public AuditLogs rename(Table<?> name) {
        return new AuditLogs(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public AuditLogs where(Condition condition) {
        return new AuditLogs(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public AuditLogs where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public AuditLogs where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public AuditLogs where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public AuditLogs where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public AuditLogs where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public AuditLogs where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public AuditLogs where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public AuditLogs whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public AuditLogs whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
