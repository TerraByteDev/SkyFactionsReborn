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
public class FactionInvites implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String factionname;
    private final byte[] uuid;
    private final byte[] inviter;
    private final String type;
    private final Long timestamp;

    public FactionInvites(FactionInvites value) {
        this.factionname = value.factionname;
        this.uuid = value.uuid;
        this.inviter = value.inviter;
        this.type = value.type;
        this.timestamp = value.timestamp;
    }

    public FactionInvites(
        String factionname,
        byte[] uuid,
        byte[] inviter,
        String type,
        Long timestamp
    ) {
        this.factionname = factionname;
        this.uuid = uuid;
        this.inviter = inviter;
        this.type = type;
        this.timestamp = timestamp;
    }

    /**
     * Getter for <code>faction_invites.factionName</code>.
     */
    public String getFactionname() {
        return this.factionname;
    }

    /**
     * Getter for <code>faction_invites.uuid</code>.
     */
    public byte[] getUuid() {
        return this.uuid;
    }

    /**
     * Getter for <code>faction_invites.inviter</code>.
     */
    public byte[] getInviter() {
        return this.inviter;
    }

    /**
     * Getter for <code>faction_invites.type</code>.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Getter for <code>faction_invites.timestamp</code>.
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
        final FactionInvites other = (FactionInvites) obj;
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
        else if (!Arrays.equals(this.uuid, other.uuid))
            return false;
        if (this.inviter == null) {
            if (other.inviter != null)
                return false;
        }
        else if (!Arrays.equals(this.inviter, other.inviter))
            return false;
        if (this.type == null) {
            if (other.type != null)
                return false;
        }
        else if (!this.type.equals(other.type))
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
        result = prime * result + ((this.uuid == null) ? 0 : Arrays.hashCode(this.uuid));
        result = prime * result + ((this.inviter == null) ? 0 : Arrays.hashCode(this.inviter));
        result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
        result = prime * result + ((this.timestamp == null) ? 0 : this.timestamp.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("FactionInvites (");

        sb.append(factionname);
        sb.append(", ").append("[binary...]");
        sb.append(", ").append("[binary...]");
        sb.append(", ").append(type);
        sb.append(", ").append(timestamp);

        sb.append(")");
        return sb.toString();
    }
}
