package net.skullian.skyfactions.gui.items.obelisk.member_manage;

import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.faction.Faction;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.faction.AuditLogType;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import java.util.List;

public class MemberBanItem extends SkyItem {

    private OfflinePlayer SUBJECT;

    public MemberBanItem(ItemData data, ItemStack stack, OfflinePlayer player, Player viewer, Faction faction) {
        super(data, stack, viewer, List.of(faction).toArray());

        this.SUBJECT = player;
    }

    @Override
    public ItemBuilder process(ItemBuilder builder) {
        Faction faction = (Faction) getOptionals()[0];

        if (!Settings.FACTION_BAN_PERMISSIONS.getList().contains(faction.getRankType(getPLAYER().getUniqueId()).getRankValue())) {
            builder.addLoreLines(toList(Messages.FACTION_MANAGE_NO_PERMISSIONS_LORE.getStringList(PlayerHandler.getLocale(getPLAYER().getUniqueId()))));
        }

        return builder;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        Faction faction = (Faction) getOptionals()[0];
        event.getInventory().close();

        if (faction.getAllMembers().contains(SUBJECT)) {
            faction.createAuditLog(SUBJECT.getUniqueId(), AuditLogType.PLAYER_BAN, "banned", SUBJECT.getName(), "player", player.getName());
            faction.banPlayer(SUBJECT, player);

            Messages.FACTION_MANAGE_BAN_SUCCESS.send(player, PlayerHandler.getLocale(player.getUniqueId()), "player", SUBJECT.getName());
        } else {
            Messages.ERROR.send(player, PlayerHandler.getLocale(player.getUniqueId()), "operation", "ban a player", "debug", "FACTION_MEMBER_UNKNOWN");
            event.getInventory().close();
        }
    }

}
