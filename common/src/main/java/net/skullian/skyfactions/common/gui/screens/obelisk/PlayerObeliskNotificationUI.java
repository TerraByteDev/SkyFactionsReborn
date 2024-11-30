package net.skullian.skyfactions.common.gui.screens.obelisk;

import lombok.Builder;
import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.GUIEnums;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.PaginationItemData;
import net.skullian.skyfactions.common.gui.items.EmptyItem;
import net.skullian.skyfactions.common.gui.items.PaginationBackItem;
import net.skullian.skyfactions.common.gui.items.PaginationForwardItem;
import net.skullian.skyfactions.common.gui.items.impl.BaseSkyItem;
import net.skullian.skyfactions.common.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.common.gui.items.obelisk.notification.ObeliskNotificationPaginationItem;
import net.skullian.skyfactions.common.gui.screens.PaginatedScreen;
import net.skullian.skyfactions.common.notification.NotificationData;
import net.skullian.skyfactions.common.user.SkyUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PlayerObeliskNotificationUI extends PaginatedScreen {
    private final List<NotificationData> notifications;

    @Builder
    public PlayerObeliskNotificationUI(SkyUser player, List<NotificationData> notifications) {
        super(GUIEnums.OBELISK_PLAYER_NOTIFICATIONS_GUI.getPath(), player);
        this.notifications = notifications;

        ;
    }

    public static void promptPlayer(SkyUser player) {
        List<NotificationData> notifications = SkyApi.getInstance().getNotificationAPI().getNotifications(player.getUniqueId());
        try {
            player.addMetadata("inFactionRelatedUI");
            PlayerObeliskNotificationUI.builder().player(player).notifications(notifications).build().show();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "operation", "open the notifications GUI", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    public BaseSkyItem handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID()) {
            case "PROMPT", "BORDER" ->
                    new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "BACK" ->
                    new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "player", player);
            default -> null;
        };
    }

    @Nullable
    @Override
    protected BaseSkyItem handlePaginationItem(@NotNull PaginationItemData paginationItem) {
        return switch (paginationItem.getITEM_ID()) {
            case "FORWARD_BUTTON" ->
                    new PaginationForwardItem(paginationItem, player);
            case "BACK_BUTTON" ->
                    new PaginationBackItem(paginationItem, player);
            default -> null;
        };
    }

    @NotNull
    @Override
    protected List<BaseSkyItem> getModels(SkyUser player, ItemData data) {
        List<BaseSkyItem> items = new ArrayList<>();
        for (NotificationData notification : notifications) {
            items.add(new ObeliskNotificationPaginationItem(data, GUIAPI.createItem(data, player.getUniqueId()), notification, player));
        }

        return items;
    }
}
