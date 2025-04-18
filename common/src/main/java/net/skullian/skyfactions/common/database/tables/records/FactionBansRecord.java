/*
 * This file is generated by jOOQ.
 */
package net.skullian.skyfactions.common.database.tables.records;


import net.skullian.skyfactions.common.database.tables.FactionBans;

import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class FactionBansRecord extends TableRecordImpl<FactionBansRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>faction_bans.factionName</code>.
     */
    public FactionBansRecord setFactionname(String value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>faction_bans.factionName</code>.
     */
    public String getFactionname() {
        return (String) get(0);
    }

    /**
     * Setter for <code>faction_bans.uuid</code>.
     */
    public FactionBansRecord setUuid(byte[] value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>faction_bans.uuid</code>.
     */
    public byte[] getUuid() {
        return (byte[]) get(1);
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached FactionBansRecord
     */
    public FactionBansRecord() {
        super(FactionBans.FACTION_BANS);
    }

    /**
     * Create a detached, initialised FactionBansRecord
     */
    public FactionBansRecord(String factionname, byte[] uuid) {
        super(FactionBans.FACTION_BANS);

        setFactionname(factionname);
        setUuid(uuid);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised FactionBansRecord
     */
    public FactionBansRecord(net.skullian.skyfactions.common.database.tables.pojos.FactionBans value) {
        super(FactionBans.FACTION_BANS);

        if (value != null) {
            setFactionname(value.getFactionname());
            setUuid(value.getUuid());
            resetChangedOnNotNull();
        }
    }
}
