package net.skullian.skyfactions.database.tables.pojos;


import java.io.Serializable;


@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Factions implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final String motd;
    private final Integer level;
    private final Long lastRaid;
    private final String locale;

    public Factions(Factions value) {
        this.name = value.name;
        this.motd = value.motd;
        this.level = value.level;
        this.lastRaid = value.lastRaid;
        this.locale = value.locale;
    }

    public Factions(
        String name,
        String motd,
        Integer level,
        Long lastRaid,
        String locale
    ) {
        this.name = name;
        this.motd = motd;
        this.level = level;
        this.lastRaid = lastRaid;
        this.locale = locale;
    }

    /**
     * Getter for <code>factions.name</code>.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Getter for <code>factions.motd</code>.
     */
    public String getMotd() {
        return this.motd;
    }

    /**
     * Getter for <code>factions.level</code>.
     */
    public Integer getLevel() {
        return this.level;
    }

    /**
     * Getter for <code>factions.last_raid</code>.
     */
    public Long getLastRaid() {
        return this.lastRaid;
    }

    /**
     * Getter for <code>factions.locale</code>.
     */
    public String getLocale() {
        return this.locale;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Factions other = (Factions) obj;
        if (this.name == null) {
            if (other.name != null)
                return false;
        }
        else if (!this.name.equals(other.name))
            return false;
        if (this.motd == null) {
            if (other.motd != null)
                return false;
        }
        else if (!this.motd.equals(other.motd))
            return false;
        if (this.level == null) {
            if (other.level != null)
                return false;
        }
        else if (!this.level.equals(other.level))
            return false;
        if (this.lastRaid == null) {
            if (other.lastRaid != null)
                return false;
        }
        else if (!this.lastRaid.equals(other.lastRaid))
            return false;
        if (this.locale == null) {
            if (other.locale != null)
                return false;
        }
        else if (!this.locale.equals(other.locale))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        result = prime * result + ((this.motd == null) ? 0 : this.motd.hashCode());
        result = prime * result + ((this.level == null) ? 0 : this.level.hashCode());
        result = prime * result + ((this.lastRaid == null) ? 0 : this.lastRaid.hashCode());
        result = prime * result + ((this.locale == null) ? 0 : this.locale.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Factions (");

        sb.append(name);
        sb.append(", ").append(motd);
        sb.append(", ").append(level);
        sb.append(", ").append(lastRaid);
        sb.append(", ").append(locale);

        sb.append(")");
        return sb.toString();
    }
}
