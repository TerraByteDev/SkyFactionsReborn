package net.skullian.skyfactions.gui.items.obelisk.notification;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.notification.NotificationData;
import net.skullian.skyfactions.util.ErrorHandler;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.List;

public class ObeliskNotificationPaginationItem extends AbstractItem {

    private String NAME;
    private String SOUND;
    private int PITCH;
    private List<String> LORE;
    private ItemStack STACK;
    private NotificationData DATA;

    public ObeliskNotificationPaginationItem(ItemData data, ItemStack stack, NotificationData inviteData) {
        this.NAME = data.getNAME();
        this.SOUND = data.getSOUND();
        this.PITCH = data.getPITCH();
        this.LORE = data.getLORE();
        this.STACK = stack;
        this.DATA = inviteData;
    }

    @Override
    public ItemProvider getItemProvider() {
        ItemBuilder builder = new ItemBuilder(STACK)
                .setDisplayName(TextUtility.color(NAME.replace("%notification_title%", DATA.getTitle())));

        for (String loreLine : LORE) {
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
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.setCancelled(true);

        if (!SOUND.equalsIgnoreCase("none")) {
            SoundUtil.playSound(player, SOUND, PITCH, 1);
        }

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
