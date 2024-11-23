package net.skullian.skyfactions.gui.screens.obelisk;

import lombok.Builder;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.GUIAPI;
import net.skullian.skyfactions.api.NotificationAPI;
import net.skullian.skyfactions.config.types.GUIEnums;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.api.PlayerAPI;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.data.PaginationItemData;
import net.skullian.skyfactions.gui.items.EmptyItem;
import net.skullian.skyfactions.gui.items.PaginationBackItem;
import net.skullian.skyfactions.gui.items.PaginationForwardItem;
import net.skullian.skyfactions.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.gui.items.obelisk.notification.ObeliskNotificationPaginationItem;
import net.skullian.skyfactions.gui.screens.PaginatedScreen;
import net.skullian.skyfactions.notification.NotificationData;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;

import java.util.ArrayList;
import java.util.List;

public class PlayerObeliskNotificationUI extends PaginatedScreen {
    private final List<NotificationData> notifications;

    @Builder
    public PlayerObeliskNotificationUI(Player player, List<NotificationData> notifications) {
        super(player, GUIEnums.OBELISK_PLAYER_NOTIFICATIONS_GUI.getPath());
        this.notifications = notifications;

        initWindow();
    }

    public static void promptPlayer(Player player) {
        List<NotificationData> notifications = NotificationAPI.getNotifications(player);
        try {
            player.setMetadata("inFactionRelatedUI", new FixedMetadataValue(SkyFactionsReborn.getInstance(), true));
            PlayerObeliskNotificationUI.builder().player(player).notifications(notifications).build().show();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, PlayerAPI.getLocale(player.getUniqueId()), "operation", "open the notifications GUI", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    protected Item handleItem(@NotNull ItemData itemData) {
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
    protected Item handlePaginationItem(@NotNull PaginationItemData paginationItem) {
        return switch (paginationItem.getITEM_ID()) {
            case "FORWARD_BUTTON" ->
                    new PaginationForwardItem(paginationItem, GUIAPI.createItem(paginationItem, player.getUniqueId()));
            case "BACK_BUTTON" ->
                    new PaginationBackItem(paginationItem, GUIAPI.createItem(paginationItem, player.getUniqueId()));
            default -> null;
        };
    }

    @NotNull
    @Override
    protected List<Item> getModels(Player player, ItemData data) {
        List<Item> items = new ArrayList<>();
        for (NotificationData notification : notifications) {
            items.add(new ObeliskNotificationPaginationItem(data, GUIAPI.createItem(data, player.getUniqueId()), notification, player));
        }

        return items;
    }
}
