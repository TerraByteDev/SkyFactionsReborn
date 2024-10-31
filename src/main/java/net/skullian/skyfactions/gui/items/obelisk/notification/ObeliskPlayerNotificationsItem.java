package net.skullian.skyfactions.gui.items.obelisk.notification;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.api.NotificationAPI;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.AsyncSkyItem;
import net.skullian.skyfactions.gui.obelisk.PlayerObeliskNotificationUI;
import net.skullian.skyfactions.notification.NotificationData;

public class ObeliskPlayerNotificationsItem extends AsyncSkyItem {

    public ObeliskPlayerNotificationsItem(ItemData data, ItemStack stack, Player player) {
        super(data, stack, player, null);
    }

    @Override
    public Object[] replacements() {
        List<NotificationData> notifications = NotificationAPI.getNotifications(Bukkit.getOfflinePlayer(getPLAYER().getUniqueId())).join();

        return List.of(
            "notification_count", notifications.size()
        ).toArray();
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        PlayerObeliskNotificationUI.promptPlayer(player);
    }


}
