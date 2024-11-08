package net.skullian.skyfactions.database.tables.pojos;


import java.io.Serializable;


@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Trustedplayers implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer islandId;
    private final String uuid;

    public Trustedplayers(Trustedplayers value) {
        this.islandId = value.islandId;
        this.uuid = value.uuid;
    }

    public Trustedplayers(
        Integer islandId,
        String uuid
    ) {
        this.islandId = islandId;
        this.uuid = uuid;
    }

    /**
     * Getter for <code>trustedPlayers.island_id</code>.
     */
    public Integer getIslandId() {
        return this.islandId;
    }

    /**
     * Getter for <code>trustedPlayers.uuid</code>.
     */
    public String getUuid() {
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
        final Trustedplayers other = (Trustedplayers) obj;
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
        else if (!this.uuid.equals(other.uuid))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.islandId == null) ? 0 : this.islandId.hashCode());
        result = prime * result + ((this.uuid == null) ? 0 : this.uuid.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Trustedplayers (");

        sb.append(islandId);
        sb.append(", ").append(uuid);

        sb.append(")");
        return sb.toString();
    }
}
