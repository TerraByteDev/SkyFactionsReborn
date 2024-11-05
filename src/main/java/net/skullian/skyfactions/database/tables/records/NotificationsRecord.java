package net.skullian.skyfactions.database.tables.records;

import net.skullian.skyfactions.database.tables.Notifications;
import org.jooq.impl.TableRecordImpl;

@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class NotificationsRecord extends TableRecordImpl<NotificationsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>notifications.uuid</code>.
     */
    public NotificationsRecord setUuid(String value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>notifications.uuid</code>.
     */
    public String getUuid() {
        return (String) get(0);
    }

    /**
     * Setter for <code>notifications.type</code>.
     */
    public NotificationsRecord setType(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>notifications.type</code>.
     */
    public String getType() {
        return (String) get(1);
    }

    /**
     * Setter for <code>notifications.description</code>.
     */
    public NotificationsRecord setDescription(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>notifications.description</code>.
     */
    public String getDescription() {
        return (String) get(2);
    }

    /**
     * Setter for <code>notifications.timestamp</code>.
     */
    public NotificationsRecord setTimestamp(Long value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>notifications.timestamp</code>.
     */
    public Long getTimestamp() {
        return (Long) get(3);
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached NotificationsRecord
     */
    public NotificationsRecord() {
        super(Notifications.NOTIFICATIONS);
    }

    /**
     * Create a detached, initialised NotificationsRecord
     */
    public NotificationsRecord(String uuid, String type, String description, Long timestamp) {
        super(Notifications.NOTIFICATIONS);

        setUuid(uuid);
        setType(type);
        setDescription(description);
        setTimestamp(timestamp);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised NotificationsRecord
     */
    public NotificationsRecord(net.skullian.skyfactions.database.tables.pojos.Notifications value) {
        super(Notifications.NOTIFICATIONS);

        if (value != null) {
            setUuid(value.getUuid());
            setType(value.getType());
            setDescription(value.getDescription());
            setTimestamp(value.getTimestamp());
            resetChangedOnNotNull();
        }
    }
}
