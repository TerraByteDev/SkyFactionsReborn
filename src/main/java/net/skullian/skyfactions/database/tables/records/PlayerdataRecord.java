package net.skullian.skyfactions.database.tables.records;

import net.skullian.skyfactions.database.tables.Playerdata;
import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;

@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class PlayerdataRecord extends UpdatableRecordImpl<PlayerdataRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>playerData.uuid</code>.
     */
    public PlayerdataRecord setUuid(String value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>playerData.uuid</code>.
     */
    public String getUuid() {
        return (String) get(0);
    }

    /**
     * Setter for <code>playerData.faction</code>.
     */
    public PlayerdataRecord setFaction(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>playerData.faction</code>.
     */
    public String getFaction() {
        return (String) get(1);
    }

    /**
     * Setter for <code>playerData.discord_id</code>.
     */
    public PlayerdataRecord setDiscordId(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>playerData.discord_id</code>.
     */
    public String getDiscordId() {
        return (String) get(2);
    }

    /**
     * Setter for <code>playerData.last_raid</code>.
     */
    public PlayerdataRecord setLastRaid(Long value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>playerData.last_raid</code>.
     */
    public Long getLastRaid() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>playerData.locale</code>.
     */
    public PlayerdataRecord setLocale(String value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>playerData.locale</code>.
     */
    public String getLocale() {
        return (String) get(4);
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
     * Create a detached PlayerdataRecord
     */
    public PlayerdataRecord() {
        super(Playerdata.PLAYERDATA);
    }

    /**
     * Create a detached, initialised PlayerdataRecord
     */
    public PlayerdataRecord(String uuid, String faction, String discordId, Long lastRaid, String locale) {
        super(Playerdata.PLAYERDATA);

        setUuid(uuid);
        setFaction(faction);
        setDiscordId(discordId);
        setLastRaid(lastRaid);
        setLocale(locale);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised PlayerdataRecord
     */
    public PlayerdataRecord(net.skullian.skyfactions.database.tables.pojos.Playerdata value) {
        super(Playerdata.PLAYERDATA);

        if (value != null) {
            setUuid(value.getUuid());
            setFaction(value.getFaction());
            setDiscordId(value.getDiscordId());
            setLastRaid(value.getLastRaid());
            setLocale(value.getLocale());
            resetChangedOnNotNull();
        }
    }
}
