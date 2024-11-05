package net.skullian.skyfactions.database.tables.pojos;

import java.io.Serializable;

@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Auditlogs implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String factionname;
    private final String type;
    private final String uuid;
    private final String description;
    private final Long timestamp;

    public Auditlogs(Auditlogs value) {
        this.factionname = value.factionname;
        this.type = value.type;
        this.uuid = value.uuid;
        this.description = value.description;
        this.timestamp = value.timestamp;
    }

    public Auditlogs(
        String factionname,
        String type,
        String uuid,
        String description,
        Long timestamp
    ) {
        this.factionname = factionname;
        this.type = type;
        this.uuid = uuid;
        this.description = description;
        this.timestamp = timestamp;
    }

    /**
     * Getter for <code>auditLogs.factionName</code>.
     */
    public String getFactionname() {
        return this.factionname;
    }

    /**
     * Getter for <code>auditLogs.type</code>.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Getter for <code>auditLogs.uuid</code>.
     */
    public String getUuid() {
        return this.uuid;
    }

    /**
     * Getter for <code>auditLogs.description</code>.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Getter for <code>auditLogs.timestamp</code>.
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
        final Auditlogs other = (Auditlogs) obj;
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
        else if (!this.uuid.equals(other.uuid))
            return false;
        if (this.description == null) {
            if (other.description != null)
                return false;
        }
        else if (!this.description.equals(other.description))
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
        result = prime * result + ((this.uuid == null) ? 0 : this.uuid.hashCode());
        result = prime * result + ((this.description == null) ? 0 : this.description.hashCode());
        result = prime * result + ((this.timestamp == null) ? 0 : this.timestamp.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Auditlogs (");

        sb.append(factionname);
        sb.append(", ").append(type);
        sb.append(", ").append(uuid);
        sb.append(", ").append(description);
        sb.append(", ").append(timestamp);

        sb.append(")");
        return sb.toString();
    }
}
