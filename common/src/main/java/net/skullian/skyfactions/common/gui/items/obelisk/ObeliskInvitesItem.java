package net.skullian.skyfactions.common.gui.items.obelisk;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.gui.screens.obelisk.invites.FactionInviteTypeSelectionUI;
import net.skullian.skyfactions.common.gui.screens.obelisk.invites.PlayerInviteTypeSelectionUI;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

public class ObeliskInvitesItem extends SkyItem {

    private final String TYPE;
    private final Faction FACTION;

    public ObeliskInvitesItem(ItemData data, SkyItemStack stack, String type, Faction faction, SkyUser player) {
        super(data, stack, player, null);
        
        this.TYPE = type;
        this.FACTION = faction;
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

        if (TYPE.equals("faction")) {
            if (FACTION.isOwner(player) || FACTION.isAdmin(player) || FACTION.isModerator(player)) {
                FactionInviteTypeSelectionUI.promptPlayer(player);
            } else {
                Messages.OBELISK_GUI_DENY.send(player, locale, "rank", Messages.FACTION_MODERATOR_TITLE.get(locale));
            }
        } else if (TYPE.equals("player")) {
            PlayerInviteTypeSelectionUI.promptPlayer(player);
        }
    }


}
