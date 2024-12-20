package net.skullian.skyfactions.common.gui.items.obelisk;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.gui.screens.obelisk.FactionAuditLogUI;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import net.skullian.skyfactions.common.util.SkyItemStack;

public class ObeliskAuditLogItem extends SkyItem {

    public ObeliskAuditLogItem(ItemData data, SkyItemStack stack, SkyUser player) {
        super(data, stack, player, null);
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

        SkyApi.getInstance().getFactionAPI().getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            } else if (faction == null) {
                Messages.ERROR.send(player, locale, "operation", "get your Faction", "FACTION_NOT_FOUND");
                return;
            }

            if (faction.getOwner().equals(player) || faction.getAdmins().contains(player)) {
                FactionAuditLogUI.promptPlayer(player, faction);
            } else {
                Messages.OBELISK_GUI_DENY.send(player, locale, "rank", Messages.FACTION_ADMIN_TITLE.get(locale));
            }
        });
    }

}
