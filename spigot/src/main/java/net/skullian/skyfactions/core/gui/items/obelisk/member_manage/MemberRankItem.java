package net.skullian.skyfactions.core.gui.items.obelisk.member_manage;

import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.config.types.Settings;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.impl.SkyItem;
import net.skullian.skyfactions.core.gui.screens.obelisk.member.MemberManageRankUI;
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
            builder.addLoreLines(toList(Messages.FACTION_MANAGE_NO_PERMISSIONS_LORE.getStringList(SpigotPlayerAPI.getLocale(getPLAYER().getUniqueId()))));
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
