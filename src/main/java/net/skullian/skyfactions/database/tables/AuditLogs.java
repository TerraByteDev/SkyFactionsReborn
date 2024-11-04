
package net.skullian.skyfactions.database.tables;


import net.skullian.skyfactions.database.DefaultSchema;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import java.util.Collection;


@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class AuditLogs extends TableImpl<Record> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>auditLogs</code>
     */
    public static final AuditLogs AUDITLOGS = new AuditLogs();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>auditLogs.factionName</code>.
     */
    public final TableField<Record, String> FACTIONNAME = createField(DSL.name("factionName"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>auditLogs.type</code>.
     */
    public final TableField<Record, String> TYPE = createField(DSL.name("type"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>auditLogs.uuid</code>.
     */
    public final TableField<Record, String> UUID = createField(DSL.name("uuid"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>auditLogs.description</code>.
     */
    public final TableField<Record, String> DESCRIPTION = createField(DSL.name("description"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>auditLogs.timestamp</code>.
     */
    public final TableField<Record, Integer> TIMESTAMP = createField(DSL.name("timestamp"), SQLDataType.INTEGER.nullable(false), this, "");

    private AuditLogs(Name alias, Table<Record> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private AuditLogs(Name alias, Table<Record> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>auditLogs</code> table reference
     */
    public AuditLogs(String alias) {
        this(DSL.name(alias), AUDITLOGS);
    }

    /**
     * Create an aliased <code>auditLogs</code> table reference
     */
    public AuditLogs(Name alias) {
        this(alias, AUDITLOGS);
    }

    /**
     * Create a <code>auditLogs</code> table reference
     */
    public AuditLogs() {
        this(DSL.name("auditLogs"), null);
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
