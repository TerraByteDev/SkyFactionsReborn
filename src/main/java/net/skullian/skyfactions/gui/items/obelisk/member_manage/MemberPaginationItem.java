package net.skullian.skyfactions.gui.items.obelisk.member_manage;

import java.util.List;

import net.skullian.skyfactions.faction.Faction;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.gui.screens.obelisk.member.ManageMemberUI;
import net.skullian.skyfactions.util.text.TextUtility;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public class MemberPaginationItem extends SkyItem {

    private OfflinePlayer SUBJECT;

    public MemberPaginationItem(ItemData data, ItemStack stack, String rankTitle, OfflinePlayer player, Player actor, Faction faction) {
        super(data, stack, actor, List.of(rankTitle, player, faction).toArray());

        this.SUBJECT = player;
    }

    @Override
    public ItemBuilder process(ItemBuilder builder) {
        String locale = PlayerHandler.getLocale(getPLAYER().getUniqueId());
        OfflinePlayer subject = (OfflinePlayer) getOptionals()[1];

        if (subject.getUniqueId().equals(getPLAYER().getUniqueId())) {
            builder.addLoreLines(TextUtility.legacyColor(Messages.FACTION_MANAGE_SELF_DENY_LORE.getString(locale), locale, getPLAYER()));
        }

        return builder;
    }

    @Override
    public Object[] replacements() {
        String rankTitle = (String) getOptionals()[0];

        return List.of(
            "player_rank", rankTitle
        ).toArray();
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        Faction faction = (Faction) getOptionals()[2];

        if (SUBJECT.getUniqueId().equals(player.getUniqueId())) {
            Messages.FACTION_MANAGE_SELF_DENY.send(player, PlayerHandler.getLocale(player.getUniqueId()));
            // todo, block higher ranks
        } else {
            ManageMemberUI.promptPlayer(player, SUBJECT, faction);
        }
    }
}
