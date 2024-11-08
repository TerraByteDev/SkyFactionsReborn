package net.skullian.skyfactions.database.tables.records;

import net.skullian.skyfactions.database.tables.Factionbans;
import org.jooq.impl.TableRecordImpl;

@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class FactionbansRecord extends TableRecordImpl<FactionbansRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>factionBans.factionName</code>.
     */
    public FactionbansRecord setFactionname(String value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>factionBans.factionName</code>.
     */
    public String getFactionname() {
        return (String) get(0);
    }

    /**
     * Setter for <code>factionBans.uuid</code>.
     */
    public FactionbansRecord setUuid(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>factionBans.uuid</code>.
     */
    public String getUuid() {
        return (String) get(1);
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached FactionbansRecord
     */
    public FactionbansRecord() {
        super(Factionbans.FACTIONBANS);
    }

    /**
     * Create a detached, initialised FactionbansRecord
     */
    public FactionbansRecord(String factionname, String uuid) {
        super(Factionbans.FACTIONBANS);

        setFactionname(factionname);
        setUuid(uuid);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised FactionbansRecord
     */
    public FactionbansRecord(net.skullian.skyfactions.database.tables.pojos.Factionbans value) {
        super(Factionbans.FACTIONBANS);

        if (value != null) {
            setFactionname(value.getFactionname());
            setUuid(value.getUuid());
            resetChangedOnNotNull();
        }
    }
}