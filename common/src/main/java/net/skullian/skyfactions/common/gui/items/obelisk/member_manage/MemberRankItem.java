package net.skullian.skyfactions.common.gui.items.obelisk.member_manage;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.gui.screens.obelisk.member.MemberManageRankUI;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

import java.util.List;

public class MemberRankItem extends SkyItem {

    public MemberRankItem(ItemData data, SkyItemStack stack, SkyUser player, SkyUser actor, Faction faction) {
        super(data, stack, actor, List.of(faction, player).toArray());
    }

    @Override
    public SkyItemStack.SkyItemStackBuilder process(SkyItemStack.SkyItemStackBuilder builder) {
        Faction faction = (Faction) getOPTIONALS()[0];

        if (!Settings.FACTION_MANAGE_RANK_PERMISSIONS.getList().contains(faction.getRankType(getPLAYER().getUniqueId()).getRankValue())) {
            builder.lore(Messages.FACTION_MANAGE_NO_PERMISSIONS_LORE.getStringList(SkyApi.getInstance().getPlayerAPI().getLocale(getPLAYER().getUniqueId())));
        }

        return builder;
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        Faction faction = (Faction) getOPTIONALS()[0];
        SkyUser subject = (SkyUser) getOPTIONALS()[1];
        MemberManageRankUI.promptPlayer(player, faction, subject);
    }
}
