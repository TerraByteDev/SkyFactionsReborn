package net.skullian.skyfactions.common.gui.items.obelisk;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.gui.screens.obelisk.member.MemberManagementUI;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;
import net.skullian.skyfactions.common.util.text.TextUtility;

public class ObeliskMemberManagementItem extends SkyItem {

    private Faction FACTION;

    public ObeliskMemberManagementItem(ItemData data, SkyItemStack stack, Faction faction, SkyUser player) {
        super(data, stack, player, null);
        this.FACTION = faction;
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

        if (FACTION == null) {
            Messages.ERROR.send(player, locale, "operation", "get your Faction", "FACTION_NOT_FOUND");
        } else if (!TextUtility.merge(Settings.FACTION_KICK_PERMISSIONS.getList(), Settings.FACTION_BAN_PERMISSIONS.getList(), Settings.FACTION_MANAGE_RANK_PERMISSIONS.getList()).contains(FACTION.getRankType(player.getUniqueId()).getRankValue())) {
            Messages.OBELISK_GUI_DENY.send(player, locale, "rank", Messages.FACTION_ADMIN_TITLE.get(locale));
        } else {
            MemberManagementUI.promptPlayer(player, FACTION);
        }
    }

}
