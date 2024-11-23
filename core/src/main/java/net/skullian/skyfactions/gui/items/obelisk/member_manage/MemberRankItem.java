package net.skullian.skyfactions.gui.items.obelisk.member_manage;

import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.api.PlayerAPI;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.gui.screens.obelisk.member.MemberManageRankUI;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import java.util.List;

public class MemberRankItem extends SkyItem {

    public MemberRankItem(ItemData data, ItemStack stack, OfflinePlayer player, Player actor, Faction faction) {
        super(data, stack, actor, List.of(faction, player).toArray());
    }

    @Override
    public ItemBuilder process(ItemBuilder builder) {
        Faction faction = (Faction) getOptionals()[0];

        if (!Settings.FACTION_MANAGE_RANK_PERMISSIONS.getList().contains(faction.getRankType(getPLAYER().getUniqueId()).getRankValue())) {
            builder.addLoreLines(toList(Messages.FACTION_MANAGE_NO_PERMISSIONS_LORE.getStringList(PlayerAPI.getLocale(getPLAYER().getUniqueId()))));
        }

        return builder;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        Faction faction = (Faction) getOptionals()[0];
        OfflinePlayer subject = (OfflinePlayer) getOptionals()[1];
        MemberManageRankUI.promptPlayer(player, faction, subject);
    }
}
