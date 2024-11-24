/*
 * This file is generated by jOOQ.
 */
package net.skullian.skyfactions.database.tables.records;


import net.skullian.skyfactions.database.tables.Factions;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class FactionsRecord extends UpdatableRecordImpl<FactionsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>factions.name</code>.
     */
    public FactionsRecord setName(String value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>factions.name</code>.
     */
    public String getName() {
        return (String) get(0);
    }

    /**
     * Setter for <code>factions.motd</code>.
     */
    public FactionsRecord setMotd(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>factions.motd</code>.
     */
    public String getMotd() {
        return (String) get(1);
    }

    /**
     * Setter for <code>factions.level</code>.
     */
    public FactionsRecord setLevel(Integer value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>factions.level</code>.
     */
    public Integer getLevel() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>factions.last_raid</code>.
     */
    public FactionsRecord setLastRaid(Long value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>factions.last_raid</code>.
     */
    public Long getLastRaid() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>factions.locale</code>.
     */
    public FactionsRecord setLocale(String value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>factions.locale</code>.
     */
    public String getLocale() {
        return (String) get(4);
    }

    /**
     * Setter for <code>factions.last_renamed</code>.
     */
    public FactionsRecord setLastRenamed(Long value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>factions.last_renamed</code>.
     */
    public Long getLastRenamed() {
        return (Long) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached FactionsRecord
     */
    public FactionsRecord() {
        super(Factions.FACTIONS);
    }

    /**
     * Create a detached, initialised FactionsRecord
     */
    public FactionsRecord(String name, String motd, Integer level, Long lastRaid, String locale, Long lastRenamed) {
        super(Factions.FACTIONS);

        setName(name);
        setMotd(motd);
        setLevel(level);
        setLastRaid(lastRaid);
        setLocale(locale);
        setLastRenamed(lastRenamed);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised FactionsRecord
     */
    public FactionsRecord(net.skullian.skyfactions.database.tables.pojos.Factions value) {
        super(Factions.FACTIONS);

        if (value != null) {
            setName(value.getName());
            setMotd(value.getMotd());
            setLevel(value.getLevel());
            setLastRaid(value.getLastRaid());
            setLocale(value.getLocale());
            setLastRenamed(value.getLastRenamed());
            resetChangedOnNotNull();
        }
    }
}