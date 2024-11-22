package net.skullian.skyfactions.gui.items.obelisk.member_manage;

import java.util.List;

import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.util.SoundUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.api.PlayerAPI;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.gui.screens.obelisk.member.ManageMemberUI;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public class MemberPaginationItem extends SkyItem {

    private OfflinePlayer SUBJECT;

    public MemberPaginationItem(ItemData data, ItemStack stack, String rankTitle, OfflinePlayer player, Player actor, Faction faction) {
        super(data, stack, actor, List.of(rankTitle, player, faction).toArray());

        this.SUBJECT = player;
    }

    @Override
    public ItemBuilder process(ItemBuilder builder) {
        Faction faction = (Faction) getOptionals()[2];

        String locale = PlayerAPI.getLocale(getPLAYER().getUniqueId());
        OfflinePlayer subject = (OfflinePlayer) getOptionals()[1];

        if (subject.getUniqueId().equals(getPLAYER().getUniqueId())) {
            builder.addLoreLines(toList(Messages.FACTION_MANAGE_SELF_DENY_LORE.getStringList(locale)));
        } else if (faction.getRankType(subject.getUniqueId()).getOrder() <= faction.getRankType(getPLAYER().getUniqueId()).getOrder()) {
            builder.addLoreLines(toList(Messages.FACTION_MANAGE_HIGHER_RANKS_DENY_LORE.getStringList(locale)));
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
        String locale = PlayerAPI.getLocale(player.getUniqueId());

        if (SUBJECT.getUniqueId().equals(player.getUniqueId())) {
            SoundUtil.playSound(player, Settings.ERROR_SOUND.getString(), Settings.ERROR_SOUND.getInt(), 1);
            Messages.FACTION_MANAGE_SELF_DENY.send(player, locale);
        } else if (faction.getRankType(SUBJECT.getUniqueId()).getOrder() <= faction.getRankType(player.getUniqueId()).getOrder()) {
            SoundUtil.playSound(player, Settings.ERROR_SOUND.getString(), Settings.ERROR_SOUND.getInt(), 1);
            Messages.FACTION_MANAGE_HIGHER_RANKS_DENY.send(player, locale);
        } else {
            ManageMemberUI.promptPlayer(player, SUBJECT, faction);
        }
    }
}
