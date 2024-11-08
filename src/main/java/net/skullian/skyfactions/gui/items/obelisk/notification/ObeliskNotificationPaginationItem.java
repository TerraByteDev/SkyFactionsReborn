package net.skullian.skyfactions.gui.items.obelisk.notification;

import net.kyori.adventure.text.Component;
import net.skullian.skyfactions.faction.AuditLogType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.notification.NotificationData;
import net.skullian.skyfactions.util.ErrorUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public class ObeliskNotificationPaginationItem extends SkyItem {

    private NotificationData DATA;

    public ObeliskNotificationPaginationItem(ItemData data, ItemStack stack, NotificationData inviteData, Player player) {
        super(data, stack, player, null);

        this.DATA = inviteData;
    }

    @Override
    public ItemProvider getItemProvider() {
        String locale = PlayerHandler.getLocale(getPLAYER().getUniqueId());

        String title = AuditLogType.valueOf(DATA.getType()).getTitle(getPLAYER(), DATA.getReplacements());
        String description = AuditLogType.valueOf(DATA.getType()).getDescription(getPLAYER(), DATA.getReplacements());

        ItemBuilder builder = new ItemBuilder(getSTACK())
                .setDisplayName(TextUtility.legacyColor(getDATA().getNAME().replace("notification_title", title), locale, getPLAYER()));

        for (String loreLine : getDATA().getLORE()) {
            if (loreLine.contains("notification_description")) {
                for (String part : TextUtility.toParts(description)) {
                    builder.addLoreLines(part);
                }

                continue;
            }

            builder.addLoreLines(TextUtility.legacyColor(loreLine
                    .replace("timestamp", Messages.replace(Messages.NOTIFICATION_TIMESTAMP_FORMAT.getString(locale), locale, getPLAYER(), "time", TextUtility.formatExtendedElapsedTime(DATA.getTimestamp()))),
                    locale,
                    getPLAYER()
            ));
        }

        return builder;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (clickType.isRightClick()) {
            player.closeInventory();
            SkyFactionsReborn.databaseManager.notificationManager.removeNotification(player, DATA).whenComplete((ignored, ex) -> {
                if (ex != null) {
                    ErrorUtil.handleError(player, "remove a notification", "SQL_NOTIFICATION_REMOVE", ex);
                    return;
                }

                Messages.NOTIFICATION_DISMISS_SUCCESS.send(player, PlayerHandler.getLocale(player.getUniqueId()));
            });
        }
    }


}
