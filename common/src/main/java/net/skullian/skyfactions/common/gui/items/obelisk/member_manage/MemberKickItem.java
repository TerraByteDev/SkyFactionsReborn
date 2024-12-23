package net.skullian.skyfactions.common.gui.items.obelisk.member_manage;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.faction.AuditLogType;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import net.skullian.skyfactions.common.util.SkyItemStack;

import java.util.List;

public class MemberKickItem extends SkyItem {

    private final SkyUser SUBJECT;

    public MemberKickItem(ItemData data, SkyItemStack stack, SkyUser player, SkyUser viewer, Faction faction) {
        super(data, stack, viewer, List.of(faction).toArray());
        
        this.SUBJECT = player;
    }

    @Override
    public SkyItemStack.SkyItemStackBuilder process(SkyItemStack.SkyItemStackBuilder builder) {
        Faction faction = (Faction) getOPTIONALS()[0];

        if (!Settings.FACTION_KICK_PERMISSIONS.getList().contains(faction.getRankType(getPLAYER().getUniqueId()).getRankValue())) {
            builder.lore(Messages.FACTION_MANAGE_NO_PERMISSIONS_LORE.getStringList(SkyApi.getInstance().getPlayerAPI().getLocale(getPLAYER().getUniqueId())));
        }

        return builder;
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

        SkyApi.getInstance().getFactionAPI().getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            player.closeInventory();
            if (ex != null) {
                ErrorUtil.handleError(player, "kick a player", "SQL_FACTION_GET", ex);
                return;
            }

            if (faction != null) {
                if (faction.getAllMembers().contains(SUBJECT)) {
                    // todo discord support & or announce in chat?
                    faction.createAuditLog(SUBJECT.getUniqueId(), AuditLogType.PLAYER_KICK, "kicked", SUBJECT.getName(), "player", player.getName());
                    faction.kickPlayer(SUBJECT, player);

                    Messages.FACTION_MANAGE_KICK_SUCCESS.send(player, locale, "player", SUBJECT.getName());
                } else {
                    Messages.ERROR.send(player, locale, "operation", "kick a player", "debug", "FACTION_MEMBER_UNKNOWN");
                }
            } else {
                Messages.ERROR.send(player, locale, "operation", "kick a player", "debug", "FACTION_NOT_EXIST");
            }
        });
    }
}
