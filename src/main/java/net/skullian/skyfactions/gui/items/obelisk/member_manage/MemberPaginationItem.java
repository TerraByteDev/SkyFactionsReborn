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

    private String RANK_TITLE;
    private OfflinePlayer SUBJECT;

    public MemberPaginationItem(ItemData data, ItemStack stack, String rankTitle, OfflinePlayer player, Player actor) {
        super(data, stack, actor);

        this.RANK_TITLE = rankTitle;
        this.SUBJECT = player;
    }

    @Override
    public ItemBuilder process(ItemBuilder builder) {
        if (SUBJECT.getUniqueId().equals(getPLAYER().getUniqueId())) {
            builder.addLoreLines(TextUtility.color(Messages.FACTION_MANAGE_SELF_DENY_LORE.get()));
        }

        return builder;
    }

    @Override
    public Object[] replacements() {
        return List.of(
            "%player_rank%", RANK_TITLE
        ).toArray();
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (SUBJECT.getUniqueId().equals(player.getUniqueId())) {
            Messages.FACTION_MANAGE_SELF_DENY.send(player);
        } else {
            ManageMemberUI.promptPlayer(player, SUBJECT);
        }
    }
}
