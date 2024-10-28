package net.skullian.skyfactions.gui.items.obelisk.notification;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.notification.NotificationData;
import net.skullian.skyfactions.util.ErrorHandler;
import net.skullian.skyfactions.util.text.TextUtility;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public class ObeliskNotificationPaginationItem extends SkyItem {

    private NotificationData DATA;

    public ObeliskNotificationPaginationItem(ItemData data, ItemStack stack, NotificationData inviteData) {
        super(data, stack, null, null);

        this.DATA = inviteData;
    }

    @Override
    public ItemProvider getItemProvider() {
        ItemBuilder builder = new ItemBuilder(getSTACK())
                .setDisplayName(TextUtility.color(getDATA().getNAME().replace("%notification_title%", DATA.getTitle())));

        for (String loreLine : getDATA().getLORE()) {
            if (loreLine.contains("%notification_description%")) {
                for (String part : TextUtility.toParts(DATA.getDescription())) {
                    builder.addLoreLines(part);
                }

                continue;
            }

            builder.addLoreLines(TextUtility.color(loreLine
                    .replace("%timestamp%", Messages.NOTIFICATION_TIMESTAMP_FORMAT.get("%time%", TextUtility.formatExtendedElapsedTime(DATA.getTimestamp())))
            ));
        }

        return builder;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (clickType.isRightClick()) {
            player.closeInventory();
            SkyFactionsReborn.databaseHandler.removeNotification(player, DATA).whenComplete((ignored, ex) -> {
                if (ex != null) {
                    ErrorHandler.handleError(player, "remove a notification", "SQL_NOTIFICATION_REMOVE", ex);
                    return;
                }

                Messages.NOTIFICATION_DISMISS_SUCCESS.send(player);
            });
        }
    }


}
