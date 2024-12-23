package net.skullian.skyfactions.common.gui.items.obelisk.member_manage.rank;

import lombok.Setter;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.faction.RankType;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

import java.util.List;

@Setter
public class MemberRankChangeConfirmationItem extends SkyItem {

    private RankType SELECTED;

    public MemberRankChangeConfirmationItem(ItemData data, SkyItemStack stack, SkyUser player, Faction faction, SkyUser subject, RankType currentType) {
        super(data, stack, player, List.of(faction, subject).toArray());

        this.SELECTED = currentType;
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        if (SELECTED == null) return;
        player.closeInventory();

        Faction faction = (Faction) getOPTIONALS()[0];
        SkyUser subject = (SkyUser) getOPTIONALS()[1];

        faction.modifyPlayerRank(subject, SELECTED, player);
        Messages.RANK_CHANGE_SUCCESS.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "player_name", subject.getName(), "new_rank", faction.getRank(subject.getUniqueId()));
    }


}
