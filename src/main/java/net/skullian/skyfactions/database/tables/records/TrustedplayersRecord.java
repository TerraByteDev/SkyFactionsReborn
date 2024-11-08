package net.skullian.skyfactions.database.tables.records;

import net.skullian.skyfactions.database.tables.Trustedplayers;
import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;

@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class TrustedplayersRecord extends UpdatableRecordImpl<TrustedplayersRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>trustedPlayers.island_id</code>.
     */
    public TrustedplayersRecord setIslandId(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>trustedPlayers.island_id</code>.
     */
    public Integer getIslandId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>trustedPlayers.uuid</code>.
     */
    public TrustedplayersRecord setUuid(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>trustedPlayers.uuid</code>.
     */
    public String getUuid() {
        return (String) get(1);
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
     * Create a detached TrustedplayersRecord
     */
    public TrustedplayersRecord() {
        super(Trustedplayers.TRUSTEDPLAYERS);
    }

    /**
     * Create a detached, initialised TrustedplayersRecord
     */
    public TrustedplayersRecord(Integer islandId, String uuid) {
        super(Trustedplayers.TRUSTEDPLAYERS);

        setIslandId(islandId);
        setUuid(uuid);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised TrustedplayersRecord
     */
    public TrustedplayersRecord(net.skullian.skyfactions.database.tables.pojos.Trustedplayers value) {
        super(Trustedplayers.TRUSTEDPLAYERS);

        if (value != null) {
            setIslandId(value.getIslandId());
            setUuid(value.getUuid());
            resetChangedOnNotNull();
        }
    }
}
