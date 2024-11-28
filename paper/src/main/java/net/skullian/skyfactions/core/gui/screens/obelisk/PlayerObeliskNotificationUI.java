package net.skullian.skyfactions.core.gui.screens.obelisk;

import lombok.Builder;
import net.skullian.skyfactions.core.SkyFactionsReborn;
import net.skullian.skyfactions.core.api.SpigotGUIAPI;
import net.skullian.skyfactions.core.api.SpigotNotificationAPI;
import net.skullian.skyfactions.core.config.types.GUIEnums;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.data.PaginationItemData;
import net.skullian.skyfactions.core.gui.items.EmptyItem;
import net.skullian.skyfactions.core.gui.items.PaginationBackItem;
import net.skullian.skyfactions.core.gui.items.PaginationForwardItem;
import net.skullian.skyfactions.core.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.core.gui.items.obelisk.notification.ObeliskNotificationPaginationItem;
import net.skullian.skyfactions.core.gui.screens.PaginatedScreen;
import net.skullian.skyfactions.core.notification.NotificationData;
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
        List<NotificationData> notifications = SpigotNotificationAPI.getNotifications(player);
        try {
            player.setMetadata("inFactionRelatedUI", new FixedMetadataValue(SkyFactionsReborn.getInstance(), true));
            PlayerObeliskNotificationUI.builder().player(player).notifications(notifications).build().show();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "open the notifications GUI", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    protected Item handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID()) {
            case "PROMPT", "BORDER" ->
                    new EmptyItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "BACK" ->
                    new ObeliskBackItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), "player", player);
            default -> null;
        };
    }

    @Nullable
    @Override
    protected Item handlePaginationItem(@NotNull PaginationItemData paginationItem) {
        return switch (paginationItem.getITEM_ID()) {
            case "FORWARD_BUTTON" ->
                    new PaginationForwardItem(paginationItem, SpigotGUIAPI.createItem(paginationItem, player.getUniqueId()));
            case "BACK_BUTTON" ->
                    new PaginationBackItem(paginationItem, SpigotGUIAPI.createItem(paginationItem, player.getUniqueId()));
            default -> null;
        };
    }

    @NotNull
    @Override
    protected List<Item> getModels(Player player, ItemData data) {
        List<Item> items = new ArrayList<>();
        for (NotificationData notification : notifications) {
            items.add(new ObeliskNotificationPaginationItem(data, SpigotGUIAPI.createItem(data, player.getUniqueId()), notification, player));
        }

        return items;
    }
}
