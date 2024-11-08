package net.skullian.skyfactions.database.tables.records;

import net.skullian.skyfactions.database.tables.Factionislands;
import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;

@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class FactionislandsRecord extends UpdatableRecordImpl<FactionislandsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>factionIslands.id</code>.
     */
    public FactionislandsRecord setId(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>factionIslands.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>factionIslands.factionName</code>.
     */
    public FactionislandsRecord setFactionname(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>factionIslands.factionName</code>.
     */
    public String getFactionname() {
        return (String) get(1);
    }

    /**
     * Setter for <code>factionIslands.runes</code>.
     */
    public FactionislandsRecord setRunes(Integer value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>factionIslands.runes</code>.
     */
    public Integer getRunes() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>factionIslands.defenceCount</code>.
     */
    public FactionislandsRecord setDefencecount(Integer value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>factionIslands.defenceCount</code>.
     */
    public Integer getDefencecount() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>factionIslands.gems</code>.
     */
    public FactionislandsRecord setGems(Integer value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>factionIslands.gems</code>.
     */
    public Integer getGems() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>factionIslands.last_raided</code>.
     */
    public FactionislandsRecord setLastRaided(Long value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>factionIslands.last_raided</code>.
     */
    public Long getLastRaided() {
        return (Long) get(5);
    }

    /**
     * Setter for <code>factionIslands.last_raider</code>.
     */
    public FactionislandsRecord setLastRaider(String value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>factionIslands.last_raider</code>.
     */
    public String getLastRaider() {
        return (String) get(6);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached FactionislandsRecord
     */
    public FactionislandsRecord() {
        super(Factionislands.FACTIONISLANDS);
    }

    /**
     * Create a detached, initialised FactionislandsRecord
     */
    public FactionislandsRecord(Integer id, String factionname, Integer runes, Integer defencecount, Integer gems, Long lastRaided, String lastRaider) {
        super(Factionislands.FACTIONISLANDS);

        setId(id);
        setFactionname(factionname);
        setRunes(runes);
        setDefencecount(defencecount);
        setGems(gems);
        setLastRaided(lastRaided);
        setLastRaider(lastRaider);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised FactionislandsRecord
     */
    public FactionislandsRecord(net.skullian.skyfactions.database.tables.pojos.Factionislands value) {
        super(Factionislands.FACTIONISLANDS);

        if (value != null) {
            setId(value.getId());
            setFactionname(value.getFactionname());
            setRunes(value.getRunes());
            setDefencecount(value.getDefencecount());
            setGems(value.getGems());
            setLastRaided(value.getLastRaided());
            setLastRaider(value.getLastRaider());
            resetChangedOnNotNull();
        }
    }
}
