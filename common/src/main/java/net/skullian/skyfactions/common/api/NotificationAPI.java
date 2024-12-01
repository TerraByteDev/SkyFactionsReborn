package net.skullian.skyfactions.common.api;

import lombok.Getter;
import net.skullian.skyfactions.common.notification.NotificationData;
import net.skullian.skyfactions.common.notification.NotificationType;
import net.skullian.skyfactions.common.faction.AuditLogType;
import net.skullian.skyfactions.common.user.SkyUser;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public abstract class NotificationAPI {

    private final Map<String, Integer> factionInviteStore = new ConcurrentHashMap<>();

    /**
     * Initialise the notification cycle for a Player on join.
     * This will periodically check for unread notifications and send the player a message.
     *
     * @param player Playerc who the notification cycle should be created for [{@link SkyUser}]
     */
    public abstract void createCycle(SkyUser player);

    /**
     * Create a notification for a Player.
     *
     * @param playerUUID   UUID of the player who the notification should be sent to [{@link UUID}]
     * @param type         Type of notification [{@link AuditLogType}]
     * @param replacements Replacements for the notification title / desc.
     */
    public void createNotification(UUID playerUUID, NotificationType type, Object... replacements) {
        NotificationData data = new NotificationData(
                playerUUID,
                type.name(),
                replacements != null ? replacements : new Object[0],
                System.currentTimeMillis()
        );

        addNotification(playerUUID, data);
        SkyApi.getInstance().getCacheService().getEntry(playerUUID).addNotification(data);
    }

    public abstract void addNotification(UUID playerUUID, NotificationData notification);

    public abstract void removeNotification(UUID playerUUID, NotificationData notification);

    /**
     * Get all notifications (unread) of a Player.
     *
     * @param playerUUID UUID of the player who the notifications should be fetched from [{@link UUID}]
     * @return {@link List<NotificationData>}
     */
    @Nullable public abstract List<NotificationData> getNotifications(UUID playerUUID);
}
