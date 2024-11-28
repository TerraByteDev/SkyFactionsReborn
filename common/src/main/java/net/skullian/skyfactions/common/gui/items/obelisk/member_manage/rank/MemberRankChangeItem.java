package net.skullian.skyfactions.common.gui.items.obelisk.member_manage.rank;

import lombok.Setter;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.faction.RankType;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.impl.old.SkyItem;
import net.skullian.skyfactions.core.gui.screens.obelisk.member.MemberManageRankUI;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import java.util.List;

public class MemberRankChangeItem extends SkyItem {
    @Setter
    private RankType TYPE;

    private MemberManageRankUI UI;

    public MemberRankChangeItem(ItemData data, ItemStack stack, Player player, RankType thisType, Faction faction, OfflinePlayer subject, MemberManageRankUI ui) {
        super(data, stack, player, List.of(thisType, faction, subject).toArray());
        this.UI = ui;
    }

    @Override
    public ItemBuilder process(ItemBuilder builder) {
        OfflinePlayer subject = (OfflinePlayer) getOptionals()[2];
        Faction faction = (Faction) getOptionals()[1];
        RankType thisType = (RankType) getOptionals()[0];

        if ((TYPE == null && faction.getRankType(subject.getUniqueId()).equals(thisType) || (thisType.equals(TYPE)))) {
            builder.addEnchantment(Enchantment.LURE, 1, false);
            builder.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        return builder;
    }

    @Override
    public Object[] replacements() {
        RankType type = (RankType) getOptionals()[0];
        Faction faction = (Faction) getOptionals()[1];
        OfflinePlayer subject = (OfflinePlayer) getOptionals()[2];

        return List.of(
                "is_selected", ((TYPE == null && faction.getRankType(subject.getUniqueId()).equals(type)) || (type.equals(TYPE))) ? Messages.FACTION_MANAGE_RANK_SELECTED.getString(SpigotPlayerAPI.getLocale(getPLAYER().getUniqueId()))
                        : ""
        ).toArray();
    }

    public void onSelect() {
        notifyWindows();
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        RankType type = (RankType) getOptionals()[0];

        UI.onSelect(type);
    }
}