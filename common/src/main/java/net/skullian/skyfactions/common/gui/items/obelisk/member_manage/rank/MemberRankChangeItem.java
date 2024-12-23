package net.skullian.skyfactions.common.gui.items.obelisk.member_manage.rank;

import lombok.Setter;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.faction.RankType;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.gui.screens.obelisk.member.MemberManageRankUI;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemberRankChangeItem extends SkyItem {
    @Setter
    private RankType TYPE;

    private final MemberManageRankUI UI;

    public MemberRankChangeItem(ItemData data, SkyItemStack stack, SkyUser player, RankType thisType, Faction faction, SkyUser subject, MemberManageRankUI ui) {
        super(data, stack, player, List.of(thisType, faction, subject).toArray());
        this.UI = ui;
    }

    @Override
    public SkyItemStack.SkyItemStackBuilder process(SkyItemStack.SkyItemStackBuilder builder) {
        SkyUser subject = (SkyUser) getOPTIONALS()[2];
        Faction faction = (Faction) getOPTIONALS()[1];
        RankType thisType = (RankType) getOPTIONALS()[0];

        if ((TYPE == null && faction.getRankType(subject.getUniqueId()).equals(thisType) || (thisType.equals(TYPE)))) {
            builder.enchants(new ArrayList<>(Collections.singleton(new SkyItemStack.EnchantData("LURE", 1, false))));
            builder.itemFlags(new ArrayList<>(Collections.singleton("HIDE_ENCHANTS")));
        }

        return builder;
    }

    @Override
    public Object[] replacements() {
        RankType type = (RankType) getOPTIONALS()[0];
        Faction faction = (Faction) getOPTIONALS()[1];
        SkyUser subject = (SkyUser) getOPTIONALS()[2];

        return List.of(
                "is_selected", ((TYPE == null && faction.getRankType(subject.getUniqueId()).equals(type)) || (type.equals(TYPE))) ? Messages.FACTION_MANAGE_RANK_SELECTED.getString(SkyApi.getInstance().getPlayerAPI().getLocale(getPLAYER().getUniqueId()))
                        : ""
        ).toArray();
    }

    public void onSelect() {
        update();
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        RankType type = (RankType) getOPTIONALS()[0];
        UI.onSelect(type);
    }
}