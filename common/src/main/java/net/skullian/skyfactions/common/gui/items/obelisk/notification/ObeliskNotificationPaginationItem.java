package net.skullian.skyfactions.common.gui.items.obelisk.notification;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.notification.NotificationData;
import net.skullian.skyfactions.common.notification.NotificationType;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;
import net.skullian.skyfactions.common.util.text.TextUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObeliskNotificationPaginationItem extends SkyItem {

    private NotificationData DATA;

    public ObeliskNotificationPaginationItem(ItemData data, SkyItemStack stack, NotificationData inviteData, SkyUser player) {
        super(data, stack, player, List.of(inviteData).toArray());

        this.DATA = inviteData;
    }

    @Override
    public SkyItemStack getItemStack() {
        NotificationData data = (NotificationData) getOPTIONALS()[0];
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(getPLAYER().getUniqueId());

        String title = NotificationType.valueOf(data.getType()).getTitle(locale);
        String description = NotificationType.valueOf(data.getType()).getDescription(locale);

        SkyItemStack.SkyItemStackBuilder builder = SkyItemStack.builder()
                .displayName(getDATA().getNAME().replace("<notification_title>", title));

        for (String loreLine : getDATA().getLORE()) {
            if (loreLine.contains("notification_description")) {
                builder.lore(Messages.replace(TextUtility.toParts(description), getPLAYER(), data.getReplacements()));

                continue;
            }

            builder.lore(new ArrayList<>(Collections.singleton(loreLine
                            .replace("<timestamp>", Messages.replace(Messages.NOTIFICATION_TIMESTAMP_FORMAT.getString(locale), getPLAYER(), "<time>", TextUtility.formatExtendedElapsedTime(data.getTimestamp())))
            )));
        }

        return builder.build();
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        if (clickType.isRightClick()) {
            player.closeInventory();

            SkyApi.getInstance().getCacheService().getEntry(player.getUniqueId()).removeNotification(DATA);
            Messages.NOTIFICATION_DISMISS_SUCCESS.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()));
        }
    }


}
