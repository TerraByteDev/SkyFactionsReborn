/*
 * This file is generated by jOOQ.
 */
package net.skullian.skyfactions.common.database.tables.pojos;


import java.io.Serializable;
import java.util.Arrays;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class AuditLogs implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String factionname;
    private final String type;
    private final byte[] uuid;
    private final String replacements;
    private final Long timestamp;

    public AuditLogs(AuditLogs value) {
        this.factionname = value.factionname;
        this.type = value.type;
        this.uuid = value.uuid;
        this.replacements = value.replacements;
        this.timestamp = value.timestamp;
    }

    public AuditLogs(
        String factionname,
        String type,
        byte[] uuid,
        String replacements,
        Long timestamp
    ) {
        this.factionname = factionname;
        this.type = type;
        this.uuid = uuid;
        this.replacements = replacements;
        this.timestamp = timestamp;
    }

    /**
     * Getter for <code>audit_logs.factionName</code>.
     */
    public String getFactionname() {
        return this.factionname;
    }

    /**
     * Getter for <code>audit_logs.type</code>.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Getter for <code>audit_logs.uuid</code>.
     */
    public byte[] getUuid() {
        return this.uuid;
    }

    /**
     * Getter for <code>audit_logs.replacements</code>.
     */
    public String getReplacements() {
        return this.replacements;
    }

    /**
     * Getter for <code>audit_logs.timestamp</code>.
     */
    public Long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final AuditLogs other = (AuditLogs) obj;
        if (this.factionname == null) {
            if (other.factionname != null)
                return false;
        }
        else if (!this.factionname.equals(other.factionname))
            return false;
        if (this.type == null) {
            if (other.type != null)
                return false;
        }
        else if (!this.type.equals(other.type))
            return false;
        if (this.uuid == null) {
            if (other.uuid != null)
                return false;
        }
        else if (!Arrays.equals(this.uuid, other.uuid))
            return false;
        if (this.replacements == null) {
            if (other.replacements != null)
                return false;
        }
        else if (!this.replacements.equals(other.replacements))
            return false;
        if (this.timestamp == null) {
            if (other.timestamp != null)
                return false;
        }
        else if (!this.timestamp.equals(other.timestamp))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.factionname == null) ? 0 : this.factionname.hashCode());
        result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
        result = prime * result + ((this.uuid == null) ? 0 : Arrays.hashCode(this.uuid));
        result = prime * result + ((this.replacements == null) ? 0 : this.replacements.hashCode());
        result = prime * result + ((this.timestamp == null) ? 0 : this.timestamp.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("AuditLogs (");

        sb.append(factionname);
        sb.append(", ").append(type);
        sb.append(", ").append("[binary...]");
        sb.append(", ").append(replacements);
        sb.append(", ").append(timestamp);

        sb.append(")");
        return sb.toString();
    }
}
