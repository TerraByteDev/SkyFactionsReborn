package net.skullian.torrent.skyfactions.notification;

import net.skullian.torrent.skyfactions.config.Messages;

public enum NotificationType {

    INVITE_CREATE(Messages.NOTIFICATION_FACTION_INVITE_TITLE, Messages.NOTIFICATION_FACTION_INVITE_DESCRIPTION),
    JOIN_REQUEST_REJECT(Messages.NOTIFICATION_JOIN_REQUEST_REJECT_TITLE, Messages.NOTIFICATION_JOIN_REQUEST_REJECT_DESCRIPTION);

    private final Messages title;
    private final Messages description;
    NotificationType(Messages title, Messages description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle(Object... replacements) {
        return title.get(replacements);
    }

    public String getDescription(Object... replacements) {
        return description.get(replacements);
    }

}
