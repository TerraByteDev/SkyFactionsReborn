package net.skullian.skyfactions.database.tables.pojos;


import java.io.Serializable;


@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Factionmembers implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String factionname;
    private final String uuid;
    private final String rank;

    public Factionmembers(Factionmembers value) {
        this.factionname = value.factionname;
        this.uuid = value.uuid;
        this.rank = value.rank;
    }

    public Factionmembers(
        String factionname,
        String uuid,
        String rank
    ) {
        this.factionname = factionname;
        this.uuid = uuid;
        this.rank = rank;
    }

    /**
     * Getter for <code>factionMembers.factionName</code>.
     */
    public String getFactionname() {
        return this.factionname;
    }

    /**
     * Getter for <code>factionMembers.uuid</code>.
     */
    public String getUuid() {
        return this.uuid;
    }

    /**
     * Getter for <code>factionMembers.rank</code>.
     */
    public String getRank() {
        return this.rank;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Factionmembers other = (Factionmembers) obj;
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
        if (this.rank == null) {
            if (other.rank != null)
                return false;
        }
        else if (!this.rank.equals(other.rank))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.factionname == null) ? 0 : this.factionname.hashCode());
        result = prime * result + ((this.uuid == null) ? 0 : this.uuid.hashCode());
        result = prime * result + ((this.rank == null) ? 0 : this.rank.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Factionmembers (");

        sb.append(factionname);
        sb.append(", ").append(uuid);
        sb.append(", ").append(rank);

        sb.append(")");
        return sb.toString();
    }
}
