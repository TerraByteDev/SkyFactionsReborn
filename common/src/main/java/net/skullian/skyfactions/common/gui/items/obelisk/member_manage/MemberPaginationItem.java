package net.skullian.skyfactions.common.gui.items.obelisk.member_manage;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.gui.screens.obelisk.member.ManageMemberUI;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

import java.util.List;

public class MemberPaginationItem extends SkyItem {

    private final SkyUser SUBJECT;

    public MemberPaginationItem(ItemData data, SkyItemStack stack, String rankTitle, SkyUser player, SkyUser actor, Faction faction) {
        super(data, stack, actor, List.of(rankTitle, player, faction).toArray());

        this.SUBJECT = player;
    }

    @Override
    public SkyItemStack.SkyItemStackBuilder process(SkyItemStack.SkyItemStackBuilder builder) {
        Faction faction = (Faction) getOPTIONALS()[2];

        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(getPLAYER().getUniqueId());
        SkyUser subject = (SkyUser) getOPTIONALS()[1];

        if (subject.getUniqueId().equals(getPLAYER().getUniqueId())) {
            builder.lore(Messages.FACTION_MANAGE_SELF_DENY_LORE.getStringList(locale));
        } else if (faction.getRankType(subject.getUniqueId()).getOrder() <= faction.getRankType(getPLAYER().getUniqueId()).getOrder()) {
            builder.lore(Messages.FACTION_MANAGE_HIGHER_RANKS_DENY_LORE.getStringList(locale));
        }


        return builder;
    }

    @Override
    public Object[] replacements() {
        String rankTitle = (String) getOPTIONALS()[0];

        return List.of(
            "player_rank", rankTitle
        ).toArray();
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        Faction faction = (Faction) getOPTIONALS()[2];
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

        if (SUBJECT.getUniqueId().equals(player.getUniqueId())) {
            SkyApi.getInstance().getSoundAPI().playSound(player, Settings.ERROR_SOUND.getString(), Settings.ERROR_SOUND_PITCH.getInt(), 1);
            Messages.FACTION_MANAGE_SELF_DENY.send(player, locale);
        } else if (faction.getRankType(SUBJECT.getUniqueId()).getOrder() <= faction.getRankType(player.getUniqueId()).getOrder()) {
            SkyApi.getInstance().getSoundAPI().playSound(player, Settings.ERROR_SOUND.getString(), Settings.ERROR_SOUND_PITCH.getInt(), 1);
            Messages.FACTION_MANAGE_HIGHER_RANKS_DENY.send(player, locale);
        } else {
            ManageMemberUI.promptPlayer(player, SUBJECT, faction);
        }
    }
}
