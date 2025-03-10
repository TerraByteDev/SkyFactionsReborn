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
public class Notifications implements Serializable {

    private static final long serialVersionUID = 1L;

    private final byte[] uuid;
    private final String type;
    private final String replacements;
    private final Long timestamp;

    public Notifications(Notifications value) {
        this.uuid = value.uuid;
        this.type = value.type;
        this.replacements = value.replacements;
        this.timestamp = value.timestamp;
    }

    public Notifications(
        byte[] uuid,
        String type,
        String replacements,
        Long timestamp
    ) {
        this.uuid = uuid;
        this.type = type;
        this.replacements = replacements;
        this.timestamp = timestamp;
    }

    /**
     * Getter for <code>notifications.uuid</code>.
     */
    public byte[] getUuid() {
        return this.uuid;
    }

    /**
     * Getter for <code>notifications.type</code>.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Getter for <code>notifications.replacements</code>.
     */
    public String getReplacements() {
        return this.replacements;
    }

    /**
     * Getter for <code>notifications.timestamp</code>.
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
        final Notifications other = (Notifications) obj;
        if (this.uuid == null) {
            if (other.uuid != null)
                return false;
        }
        else if (!Arrays.equals(this.uuid, other.uuid))
            return false;
        if (this.type == null) {
            if (other.type != null)
                return false;
        }
        else if (!this.type.equals(other.type))
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
        result = prime * result + ((this.uuid == null) ? 0 : Arrays.hashCode(this.uuid));
        result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
        result = prime * result + ((this.replacements == null) ? 0 : this.replacements.hashCode());
        result = prime * result + ((this.timestamp == null) ? 0 : this.timestamp.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Notifications (");

        sb.append("[binary...]");
        sb.append(", ").append(type);
        sb.append(", ").append(replacements);
        sb.append(", ").append(timestamp);

        sb.append(")");
        return sb.toString();
    }
}
