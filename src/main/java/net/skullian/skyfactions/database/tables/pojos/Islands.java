package net.skullian.skyfactions.database.tables.pojos;


import java.io.Serializable;


@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Islands implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer id;
    private final String uuid;
    private final Integer level;
    private final Integer gems;
    private final Integer runes;
    private final Integer defencecount;
    private final Long lastRaided;
    private final String lastRaider;

    public Islands(Islands value) {
        this.id = value.id;
        this.uuid = value.uuid;
        this.level = value.level;
        this.gems = value.gems;
        this.runes = value.runes;
        this.defencecount = value.defencecount;
        this.lastRaided = value.lastRaided;
        this.lastRaider = value.lastRaider;
    }

    public Islands(
        Integer id,
        String uuid,
        Integer level,
        Integer gems,
        Integer runes,
        Integer defencecount,
        Long lastRaided,
        String lastRaider
    ) {
        this.id = id;
        this.uuid = uuid;
        this.level = level;
        this.gems = gems;
        this.runes = runes;
        this.defencecount = defencecount;
        this.lastRaided = lastRaided;
        this.lastRaider = lastRaider;
    }

    /**
     * Getter for <code>islands.id</code>.
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * Getter for <code>islands.uuid</code>.
     */
    public String getUuid() {
        return this.uuid;
    }

    /**
     * Getter for <code>islands.level</code>.
     */
    public Integer getLevel() {
        return this.level;
    }

    /**
     * Getter for <code>islands.gems</code>.
     */
    public Integer getGems() {
        return this.gems;
    }

    /**
     * Getter for <code>islands.runes</code>.
     */
    public Integer getRunes() {
        return this.runes;
    }

    /**
     * Getter for <code>islands.defenceCount</code>.
     */
    public Integer getDefencecount() {
        return this.defencecount;
    }

    /**
     * Getter for <code>islands.last_raided</code>.
     */
    public Long getLastRaided() {
        return this.lastRaided;
    }

    /**
     * Getter for <code>islands.last_raider</code>.
     */
    public String getLastRaider() {
        return this.lastRaider;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Islands other = (Islands) obj;
        if (this.id == null) {
            if (other.id != null)
                return false;
        }
        else if (!this.id.equals(other.id))
            return false;
        if (this.uuid == null) {
            if (other.uuid != null)
                return false;
        }
        else if (!this.uuid.equals(other.uuid))
            return false;
        if (this.level == null) {
            if (other.level != null)
                return false;
        }
        else if (!this.level.equals(other.level))
            return false;
        if (this.gems == null) {
            if (other.gems != null)
                return false;
        }
        else if (!this.gems.equals(other.gems))
            return false;
        if (this.runes == null) {
            if (other.runes != null)
                return false;
        }
        else if (!this.runes.equals(other.runes))
            return false;
        if (this.defencecount == null) {
            if (other.defencecount != null)
                return false;
        }
        else if (!this.defencecount.equals(other.defencecount))
            return false;
        if (this.lastRaided == null) {
            if (other.lastRaided != null)
                return false;
        }
        else if (!this.lastRaided.equals(other.lastRaided))
            return false;
        if (this.lastRaider == null) {
            if (other.lastRaider != null)
                return false;
        }
        else if (!this.lastRaider.equals(other.lastRaider))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.uuid == null) ? 0 : this.uuid.hashCode());
        result = prime * result + ((this.level == null) ? 0 : this.level.hashCode());
        result = prime * result + ((this.gems == null) ? 0 : this.gems.hashCode());
        result = prime * result + ((this.runes == null) ? 0 : this.runes.hashCode());
        result = prime * result + ((this.defencecount == null) ? 0 : this.defencecount.hashCode());
        result = prime * result + ((this.lastRaided == null) ? 0 : this.lastRaided.hashCode());
        result = prime * result + ((this.lastRaider == null) ? 0 : this.lastRaider.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Islands (");

        sb.append(id);
        sb.append(", ").append(uuid);
        sb.append(", ").append(level);
        sb.append(", ").append(gems);
        sb.append(", ").append(runes);
        sb.append(", ").append(defencecount);
        sb.append(", ").append(lastRaided);
        sb.append(", ").append(lastRaider);

        sb.append(")");
        return sb.toString();
    }
}
