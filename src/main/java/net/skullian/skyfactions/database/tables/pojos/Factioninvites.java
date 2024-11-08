package net.skullian.skyfactions.database.tables.pojos;

import java.io.Serializable;

@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Factioninvites implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String factionname;
    private final String uuid;
    private final String inviter;
    private final String type;
    private final Boolean accepted;
    private final Long timestamp;

    public Factioninvites(Factioninvites value) {
        this.factionname = value.factionname;
        this.uuid = value.uuid;
        this.inviter = value.inviter;
        this.type = value.type;
        this.accepted = value.accepted;
        this.timestamp = value.timestamp;
    }

    public Factioninvites(
        String factionname,
        String uuid,
        String inviter,
        String type,
        Boolean accepted,
        Long timestamp
    ) {
        this.factionname = factionname;
        this.uuid = uuid;
        this.inviter = inviter;
        this.type = type;
        this.accepted = accepted;
        this.timestamp = timestamp;
    }

    /**
     * Getter for <code>factionInvites.factionName</code>.
     */
    public String getFactionname() {
        return this.factionname;
    }

    /**
     * Getter for <code>factionInvites.uuid</code>.
     */
    public String getUuid() {
        return this.uuid;
    }

    /**
     * Getter for <code>factionInvites.inviter</code>.
     */
    public String getInviter() {
        return this.inviter;
    }

    /**
     * Getter for <code>factionInvites.type</code>.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Getter for <code>factionInvites.accepted</code>.
     */
    public Boolean getAccepted() {
        return this.accepted;
    }

    /**
     * Getter for <code>factionInvites.timestamp</code>.
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
        final Factioninvites other = (Factioninvites) obj;
        if (this.factionname == null) {
            if (other.factionname != null)
                return false;
        }
        else if (!this.factionname.equals(other.factionname))
            return false;
        if (this.uuid == null) {
            if (other.uuid != null)
                return false;
        }
        else if (!this.uuid.equals(other.uuid))
            return false;
        if (this.inviter == null) {
            if (other.inviter != null)
                return false;
        }
        else if (!this.inviter.equals(other.inviter))
            return false;
        if (this.type == null) {
            if (other.type != null)
                return false;
        }
        else if (!this.type.equals(other.type))
            return false;
        if (this.accepted == null) {
            if (other.accepted != null)
                return false;
        }
        else if (!this.accepted.equals(other.accepted))
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
        result = prime * result + ((this.uuid == null) ? 0 : this.uuid.hashCode());
        result = prime * result + ((this.inviter == null) ? 0 : this.inviter.hashCode());
        result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
        result = prime * result + ((this.accepted == null) ? 0 : this.accepted.hashCode());
        result = prime * result + ((this.timestamp == null) ? 0 : this.timestamp.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Factioninvites (");

        sb.append(factionname);
        sb.append(", ").append(uuid);
        sb.append(", ").append(inviter);
        sb.append(", ").append(type);
        sb.append(", ").append(accepted);
        sb.append(", ").append(timestamp);

        sb.append(")");
        return sb.toString();
    }
}
