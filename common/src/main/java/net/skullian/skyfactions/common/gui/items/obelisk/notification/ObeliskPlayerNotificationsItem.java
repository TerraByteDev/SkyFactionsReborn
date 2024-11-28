package net.skullian.skyfactions.common.gui.items.obelisk.notification;

import net.skullian.skyfactions.core.api.SpigotNotificationAPI;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.impl.old.SkyItem;
import net.skullian.skyfactions.core.gui.screens.obelisk.PlayerObeliskNotificationUI;
import net.skullian.skyfactions.core.notification.NotificationData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ObeliskPlayerNotificationsItem extends SkyItem {

    public ObeliskPlayerNotificationsItem(ItemData data, ItemStack stack, Player player) {
        super(data, stack, player, null);
    }

    @Override
    public Object[] replacements() {
        List<NotificationData> notifications = SpigotNotificationAPI.getNotifications(Bukkit.getOfflinePlayer(getPLAYER().getUniqueId()));

        return List.of(
            "notification_count", notifications.size()
        ).toArray();
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        PlayerObeliskNotificationUI.promptPlayer(player);
    }


}
