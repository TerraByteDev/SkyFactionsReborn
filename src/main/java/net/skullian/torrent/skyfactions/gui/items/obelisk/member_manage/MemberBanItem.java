package net.skullian.torrent.skyfactions.gui.items.obelisk.member_manage;

import net.skullian.torrent.skyfactions.api.FactionAPI;
import net.skullian.torrent.skyfactions.config.types.Messages;
import net.skullian.torrent.skyfactions.faction.AuditLogType;
import net.skullian.torrent.skyfactions.gui.data.ItemData;
import net.skullian.torrent.skyfactions.util.ErrorHandler;
import net.skullian.torrent.skyfactions.util.SoundUtil;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.List;

public class MemberBanItem extends AbstractItem {

    private String NAME;
    private String SOUND;
    private int PITCH;
    private List<String> LORE;
    private ItemStack STACK;
    private OfflinePlayer SUBJECT;

    public MemberBanItem(ItemData data, ItemStack stack, OfflinePlayer player) {
        this.NAME = data.getNAME();
        this.SOUND = data.getSOUND();
        this.PITCH = data.getPITCH();
        this.LORE = data.getLORE();
        this.STACK = stack;
        this.SUBJECT = player;
    }

    @Override
    public ItemProvider getItemProvider() {
        ItemBuilder builder = new ItemBuilder(STACK)
                .setDisplayName(TextUtility.color(NAME));

        for (String loreLine : LORE) {
            builder.addLoreLines(TextUtility.color(loreLine));
        }

        return builder;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.setCancelled(true);

        if (!SOUND.equalsIgnoreCase("none")) {
            SoundUtil.playSound(player, SOUND, PITCH, 1);
        }

        FactionAPI.getFaction(player).whenCompleteAsync((faction, exc) -> {
            if (exc != null) {
                ErrorHandler.handleError(player, "ban a player", "SQL_FACTION_GET", exc);
                return;
            }

            if (faction != null) {
                if (faction.getAllMembers().contains(SUBJECT)) {
                    faction.createAuditLog(SUBJECT.getUniqueId(), AuditLogType.PLAYER_BAN, "%banned%", SUBJECT.getName(), "%player%", player.getName());
                    faction.banPlayer(SUBJECT, player);

                    Messages.FACTION_MANAGE_BAN_SUCCESS.send(player, "%player%", SUBJECT.getName());
                } else {
                    Messages.ERROR.send(player, "%operation%", "ban a player", "%debug%", "FACTION_MEMBER_UNKNOWN");
                    event.getInventory().close();
                }
            } else {
                Messages.ERROR.send(player, "%operation%", "ban a player", "%debug%", "FACTION_NOT_EXIST");
                event.getInventory().close();
            }
        });

    }

}
