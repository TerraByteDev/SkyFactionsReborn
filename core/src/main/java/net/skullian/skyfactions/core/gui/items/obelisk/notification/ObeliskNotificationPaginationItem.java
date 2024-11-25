package net.skullian.skyfactions.core.gui.items.obelisk.notification;

import net.skullian.skyfactions.core.notification.NotificationType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.core.SkyFactionsReborn;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.impl.SkyItem;
import net.skullian.skyfactions.core.notification.NotificationData;
import net.skullian.skyfactions.common.util.text.TextUtility;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import java.util.List;

public class ObeliskNotificationPaginationItem extends SkyItem {

    private NotificationData DATA;

    public ObeliskNotificationPaginationItem(ItemData data, ItemStack stack, NotificationData inviteData, Player player) {
        super(data, stack, player, List.of(inviteData).toArray());

        this.DATA = inviteData;
    }

    @Override
    public ItemProvider getItemProvider() {
        NotificationData data = (NotificationData) getOptionals()[0];
        String locale = SpigotPlayerAPI.getLocale(getPLAYER().getUniqueId());

        String title = NotificationType.valueOf(data.getType()).getTitle(locale);
        String description = NotificationType.valueOf(data.getType()).getDescription(locale);

        ItemBuilder builder = new ItemBuilder(getSTACK())
                .setDisplayName(TextUtility.legacyColor(getDATA().getNAME().replace("<notification_title>", title), locale, getPLAYER(), data.getReplacements()));

        for (String loreLine : getDATA().getLORE()) {
            if (loreLine.contains("notification_description")) {
                builder.addLoreLines(toList(TextUtility.toParts(description), data.getReplacements()));

                continue;
            }

            builder.addLoreLines(TextUtility.legacyColor(loreLine
                    .replace("<timestamp>", Messages.replace(Messages.NOTIFICATION_TIMESTAMP_FORMAT.getString(locale), locale, getPLAYER(), "<time>", TextUtility.formatExtendedElapsedTime(data.getTimestamp()))),
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

            SkyFactionsReborn.getCacheService().getEntry(player.getUniqueId()).removeNotification(DATA);
            Messages.NOTIFICATION_DISMISS_SUCCESS.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
        }
    }


}
