package net.skullian.skyfactions.gui.items.obelisk;

import net.skullian.skyfactions.api.NotificationAPI;
import net.skullian.skyfactions.config.types.ObeliskConfig;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.obelisk.PlayerObeliskNotificationUI;
import net.skullian.skyfactions.notification.NotificationData;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AsyncItem;

import java.util.List;

public class ObeliskPlayerNotificationsItem extends AsyncItem {

    private String SOUND;
    private int PITCH;

    public ObeliskPlayerNotificationsItem(ItemData data, ItemStack stack, Player player) {
        super(
                ObeliskConfig.getLoadingItem(),
                () -> {
                    return new ItemProvider() {
                        @Override
                        public @NotNull ItemStack get(@Nullable String s) {
                            ItemBuilder builder = new ItemBuilder(stack)
                                    .setDisplayName(TextUtility.color(data.getNAME()));

                            List<NotificationData> notifications = NotificationAPI.getNotifications(Bukkit.getOfflinePlayer(player.getUniqueId())).join();
                            for (String loreLine : data.getLORE()) {
                                builder.addLoreLines(TextUtility.color(loreLine
                                        .replace("%notification_count%", String.valueOf(notifications.size()))
                                ));
                            }

                            return builder.get();
                        }
                    };
                }
        );

        this.SOUND = data.getSOUND();
        this.PITCH = data.getPITCH();
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.setCancelled(true);

        if (!SOUND.equalsIgnoreCase("none")) {
            SoundUtil.playSound(player, SOUND, PITCH, 1);
        }

        PlayerObeliskNotificationUI.promptPlayer(player);
    }


}
