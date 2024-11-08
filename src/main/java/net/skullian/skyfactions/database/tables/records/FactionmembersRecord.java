package net.skullian.skyfactions.database.tables.records;

import net.skullian.skyfactions.database.tables.Factionmembers;
import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;

@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class FactionmembersRecord extends UpdatableRecordImpl<FactionmembersRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>factionMembers.factionName</code>.
     */
    public FactionmembersRecord setFactionname(String value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>factionMembers.factionName</code>.
     */
    public String getFactionname() {
        return (String) get(0);
    }

    /**
     * Setter for <code>factionMembers.uuid</code>.
     */
    public FactionmembersRecord setUuid(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>factionMembers.uuid</code>.
     */
    public String getUuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>factionMembers.rank</code>.
     */
    public FactionmembersRecord setRank(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>factionMembers.rank</code>.
     */
    public String getRank() {
        return (String) get(2);
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
     * Create a detached FactionmembersRecord
     */
    public FactionmembersRecord() {
        super(Factionmembers.FACTIONMEMBERS);
    }

    /**
     * Create a detached, initialised FactionmembersRecord
     */
    public FactionmembersRecord(String factionname, String uuid, String rank) {
        super(Factionmembers.FACTIONMEMBERS);

        setFactionname(factionname);
        setUuid(uuid);
        setRank(rank);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised FactionmembersRecord
     */
    public FactionmembersRecord(net.skullian.skyfactions.database.tables.pojos.Factionmembers value) {
        super(Factionmembers.FACTIONMEMBERS);

        if (value != null) {
            setFactionname(value.getFactionname());
            setUuid(value.getUuid());
            setRank(value.getRank());
            resetChangedOnNotNull();
        }
    }
}