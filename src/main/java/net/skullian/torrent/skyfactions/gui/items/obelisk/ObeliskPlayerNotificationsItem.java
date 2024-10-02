package net.skullian.torrent.skyfactions.gui.items.obelisk;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.gui.data.ItemData;
import net.skullian.torrent.skyfactions.gui.obelisk.PlayerObeliskNotificationUI;
import net.skullian.torrent.skyfactions.util.ErrorHandler;
import net.skullian.torrent.skyfactions.util.SoundUtil;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ObeliskPlayerNotificationsItem extends AbstractItem {

    private String NAME;
    private String SOUND;
    private int PITCH;
    private List<String> LORE;
    private ItemStack STACK;
    private Player PLAYER;

    public ObeliskPlayerNotificationsItem(ItemData data, ItemStack stack, Player player) {
        this.NAME = data.getNAME();
        this.SOUND = data.getSOUND();
        this.PITCH = data.getPITCH();
        this.LORE = data.getLORE();
        this.STACK = stack;
        this.PLAYER = player;
    }

    @Override
    public ItemProvider getItemProvider() {
        ItemBuilder builder = new ItemBuilder(STACK)
                .setDisplayName(TextUtility.color(NAME));
        CompletableFuture.runAsync(() -> SkyFactionsReborn.db.getNotifications(Bukkit.getOfflinePlayer(PLAYER.getUniqueId())).whenComplete((notifications, ex) -> {
            if (ex != null) {
                ErrorHandler.handleError(PLAYER, "to get notifications", "SQL_NOTIFICATION_GET", ex);
            } else {

                for (String loreLine : LORE) {
                    builder.addLoreLines(TextUtility.color(loreLine
                            .replace("%notification_count%", String.valueOf(notifications.size()))
                    ));
                }
            }
        })).thenApply(ignored -> builder);

        return builder;
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
