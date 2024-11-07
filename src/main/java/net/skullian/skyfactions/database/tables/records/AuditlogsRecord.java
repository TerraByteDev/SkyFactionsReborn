package net.skullian.skyfactions.database.tables.records;

import net.skullian.skyfactions.database.tables.Auditlogs;
import org.jooq.impl.TableRecordImpl;

@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class AuditlogsRecord extends TableRecordImpl<AuditlogsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>auditLogs.factionName</code>.
     */
    public AuditlogsRecord setFactionname(String value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>auditLogs.factionName</code>.
     */
    public String getFactionname() {
        return (String) get(0);
    }

    /**
     * Setter for <code>auditLogs.type</code>.
     */
    public AuditlogsRecord setType(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>auditLogs.type</code>.
     */
    public String getType() {
        return (String) get(1);
    }

    /**
     * Setter for <code>auditLogs.uuid</code>.
     */
    public AuditlogsRecord setUuid(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>auditLogs.uuid</code>.
     */
    public String getUuid() {
        return (String) get(2);
    }

    /**
     * Setter for <code>auditLogs.replacements</code>.
     */
    public AuditlogsRecord setReplacements(String value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>auditLogs.replacements</code>.
     */
    public String getReplacements() {
        return (String) get(3);
    }

    /**
     * Setter for <code>auditLogs.timestamp</code>.
     */
    public AuditlogsRecord setTimestamp(Long value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>auditLogs.timestamp</code>.
     */
    public Long getTimestamp() {
        return (Long) get(4);
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached AuditlogsRecord
     */
    public AuditlogsRecord() {
        super(Auditlogs.AUDITLOGS);
    }

    /**
     * Create a detached, initialised AuditlogsRecord
     */
    public AuditlogsRecord(String factionname, String type, String uuid, String replacements, Long timestamp) {
        super(Auditlogs.AUDITLOGS);

        setFactionname(factionname);
        setType(type);
        setUuid(uuid);
        setReplacements(replacements);
        setTimestamp(timestamp);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised AuditlogsRecord
     */
    public AuditlogsRecord(net.skullian.skyfactions.database.tables.pojos.Auditlogs value) {
        super(Auditlogs.AUDITLOGS);

        if (value != null) {
            setFactionname(value.getFactionname());
            setType(value.getType());
            setUuid(value.getUuid());
            setReplacements(value.getDescription());
            setTimestamp(value.getTimestamp());
            resetChangedOnNotNull();
        }
    }
}
