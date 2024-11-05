package net.skullian.skyfactions.database.tables.pojos;

import java.io.Serializable;

@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Factionbans implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String factionname;
    private final String uuid;

    public Factionbans(Factionbans value) {
        this.factionname = value.factionname;
        this.uuid = value.uuid;
    }

    public Factionbans(
        String factionname,
        String uuid
    ) {
        this.factionname = factionname;
        this.uuid = uuid;
    }

    /**
     * Getter for <code>factionBans.factionName</code>.
     */
    public String getFactionname() {
        return this.factionname;
    }

    /**
     * Getter for <code>factionBans.uuid</code>.
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
        final Factionbans other = (Factionbans) obj;
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
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.factionname == null) ? 0 : this.factionname.hashCode());
        result = prime * result + ((this.uuid == null) ? 0 : this.uuid.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Factionbans (");

        sb.append(factionname);
        sb.append(", ").append(uuid);

        sb.append(")");
        return sb.toString();
    }
}
