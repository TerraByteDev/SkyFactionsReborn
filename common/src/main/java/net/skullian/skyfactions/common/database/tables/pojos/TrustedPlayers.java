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
public class TrustedPlayers implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer islandId;
    private final byte[] uuid;

    public TrustedPlayers(TrustedPlayers value) {
        this.islandId = value.islandId;
        this.uuid = value.uuid;
    }

    public TrustedPlayers(
        Integer islandId,
        byte[] uuid
    ) {
        this.islandId = islandId;
        this.uuid = uuid;
    }

    /**
     * Getter for <code>trusted_players.island_id</code>.
     */
    public Integer getIslandId() {
        return this.islandId;
    }

    /**
     * Getter for <code>trusted_players.uuid</code>.
     */
    public byte[] getUuid() {
        return this.uuid;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final TrustedPlayers other = (TrustedPlayers) obj;
        if (this.islandId == null) {
            if (other.islandId != null)
                return false;
        }
        else if (!this.islandId.equals(other.islandId))
            return false;
        if (this.uuid == null) {
            if (other.uuid != null)
                return false;
        }
        else if (!Arrays.equals(this.uuid, other.uuid))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.islandId == null) ? 0 : this.islandId.hashCode());
        result = prime * result + ((this.uuid == null) ? 0 : Arrays.hashCode(this.uuid));
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TrustedPlayers (");

        sb.append(islandId);
        sb.append(", ").append("[binary...]");

        sb.append(")");
        return sb.toString();
    }
}
