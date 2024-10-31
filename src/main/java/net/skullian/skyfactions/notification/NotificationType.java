package net.skullian.skyfactions.notification;

import java.util.Locale;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.skullian.skyfactions.config.types.Messages;

public enum NotificationType {

    INVITE_CREATE(Messages.NOTIFICATION_FACTION_INVITE_TITLE, Messages.NOTIFICATION_FACTION_INVITE_DESCRIPTION),
    JOIN_REQUEST_REJECT(Messages.NOTIFICATION_JOIN_REQUEST_REJECT_TITLE, Messages.NOTIFICATION_JOIN_REQUEST_REJECT_DESCRIPTION),
    JOIN_REQUEST_ACCEPT(Messages.NOTIFICATION_JOIN_REQUEST_ACCEPT_TITLE, Messages.NOTIFICATION_JOIN_REQUEST_ACCEPT_DESCRIPTION);

    private final Messages title;
    private final Messages description;

    NotificationType(Messages title, Messages description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle(String locale, Object... replacements) {
        return LegacyComponentSerializer.legacySection().serialize(title.get(locale, replacements));
    }

    public String getDescription(String locale, Object... replacements) {
        return LegacyComponentSerializer.legacySection().serialize(description.get(locale, replacements));
    }

}
