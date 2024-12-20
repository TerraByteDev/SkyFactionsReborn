package net.skullian.skyfactions.common.notification;

import net.skullian.skyfactions.common.config.types.Messages;

public enum NotificationType {

    INVITE_CREATE(Messages.NOTIFICATION_FACTION_INVITE_TITLE, Messages.NOTIFICATION_FACTION_INVITE_DESCRIPTION),
    JOIN_REQUEST_REJECT(Messages.NOTIFICATION_JOIN_REQUEST_REJECT_TITLE, Messages.NOTIFICATION_JOIN_REQUEST_REJECT_DESCRIPTION),
    JOIN_REQUEST_ACCEPT(Messages.NOTIFICATION_JOIN_REQUEST_ACCEPT_TITLE, Messages.NOTIFICATION_JOIN_REQUEST_ACCEPT_DESCRIPTION),
    FACTION_KICKED(Messages.NOTIFICATION_FACTION_KICKED_TITLE, Messages.NOTIFICATION_FACTION_KICKED_DESCRIPTION),
    FACTION_DISBANDED(Messages.NOTIFICATION_FACTION_DISBANDED_TITLE, Messages.NOTIFICATION_FACTION_DISBANDED_DESCRIPTION),
    RANK_UPDATED(Messages.NOTIFICATION_FACTION_RANK_UPDATED_TITLE, Messages.NOTIFICATION_FACTION_RANK_UPDATED_DESCRIPTION);

    private final Messages title;
    private final Messages description;

    NotificationType(Messages title, Messages description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle(String locale) {
        return title.getString(locale);
    }

    public String getDescription(String locale) {
        return description.getString(locale);
    }

}
