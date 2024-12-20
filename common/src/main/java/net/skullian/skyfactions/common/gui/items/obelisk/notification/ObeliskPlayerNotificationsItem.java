package net.skullian.skyfactions.common.gui.items.obelisk.notification;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.gui.screens.obelisk.PlayerObeliskNotificationUI;
import net.skullian.skyfactions.common.notification.NotificationData;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

import java.util.List;

public class ObeliskPlayerNotificationsItem extends SkyItem {

    public ObeliskPlayerNotificationsItem(ItemData data, SkyItemStack stack, SkyUser player) {
        super(data, stack, player, null);
    }

    @Override
    public Object[] replacements() {
        List<NotificationData> notifications = SkyApi.getInstance().getNotificationAPI().getNotifications(getPLAYER().getUniqueId());

        return List.of(
            "notification_count", notifications != null ? notifications.size() : "0"
        ).toArray();
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        PlayerObeliskNotificationUI.promptPlayer(player);
    }


}
