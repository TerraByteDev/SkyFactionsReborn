package net.skullian.skyfactions.gui.items.obelisk.member_manage;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.gui.obelisk.member.ManageMemberUI;
import net.skullian.skyfactions.util.text.TextUtility;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public class MemberPaginationItem extends SkyItem {

    private OfflinePlayer SUBJECT;

    public MemberPaginationItem(ItemData data, ItemStack stack, String rankTitle, OfflinePlayer player, Player actor) {
        super(data, stack, actor, List.of(rankTitle, player).toArray());

        this.SUBJECT = player;
    }

    @Override
    public ItemBuilder process(ItemBuilder builder) {
        OfflinePlayer subject = (OfflinePlayer) getOptionals()[1];

        if (subject.getUniqueId().equals(getPLAYER().getUniqueId())) {
            builder.addLoreLines(TextUtility.color(Messages.FACTION_MANAGE_SELF_DENY_LORE.get(getPLAYER().locale()), getPLAYER()));
        }

        return builder;
    }

    @Override
    public Object[] replacements() {
        String rankTitle = (String) getOptionals()[0];

        return List.of(
            "%player_rank%", rankTitle
        ).toArray();
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (SUBJECT.getUniqueId().equals(player.getUniqueId())) {
            Messages.FACTION_MANAGE_SELF_DENY.send(player, player.locale());
        } else {
            ManageMemberUI.promptPlayer(player, SUBJECT);
        }
    }
}
