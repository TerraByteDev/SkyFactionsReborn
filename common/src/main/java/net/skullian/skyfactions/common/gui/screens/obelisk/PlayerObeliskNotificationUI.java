package net.skullian.skyfactions.common.gui.screens.obelisk;

import lombok.Builder;
import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.GUIEnums;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.EmptyItem;
import net.skullian.skyfactions.common.gui.items.impl.BaseSkyItem;
import net.skullian.skyfactions.common.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.common.gui.items.obelisk.notification.ObeliskNotificationPaginationItem;
import net.skullian.skyfactions.common.gui.screens.PaginatedScreen;
import net.skullian.skyfactions.common.notification.NotificationData;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlayerObeliskNotificationUI extends PaginatedScreen {
    private final List<NotificationData> notifications;

    @Builder
    public PlayerObeliskNotificationUI(SkyUser player, List<NotificationData> notifications) {
        super(GUIEnums.OBELISK_PLAYER_NOTIFICATIONS_GUI.getPath(), player);
        this.notifications = notifications;
    }

    public static void promptPlayer(SkyUser player) {
        List<NotificationData> notifications = SkyApi.getInstance().getNotificationAPI().getNotifications(player.getUniqueId());
        try {
            player.addMetadata("inFactionRelatedUI", true);
            PlayerObeliskNotificationUI.builder().player(player).notifications(notifications).build().show();
        } catch (IllegalArgumentException error) {
            SLogger.fatal("Failed to create Notifications GUI for player {} - {}", player.getUniqueId(), error);
            Messages.ERROR.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "operation", "open the notifications GUI", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    public BaseSkyItem handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID().toLowerCase(Locale.ROOT)) {
            case "prompt", "border" ->
                    new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "back" ->
                    new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "player", player);
            default -> null;
        };
    }

    @NotNull
    @Override
    public List<BaseSkyItem> getModels(SkyUser player, ItemData data) {
        List<BaseSkyItem> items = new ArrayList<>();
        for (NotificationData notification : notifications) {
            items.add(new ObeliskNotificationPaginationItem(data, GUIAPI.createItem(data, player.getUniqueId()), notification, player));
        }

        return items;
    }
}
