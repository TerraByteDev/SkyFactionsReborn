package net.skullian.skyfactions.common.gui.items.faction_leave;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import net.skullian.skyfactions.common.util.SkyItemStack;

public class LeaveConfirmationItem extends SkyItem {

    public LeaveConfirmationItem(ItemData data, SkyItemStack stack, SkyUser player) {
        super(data, stack, player, null);
    }

    @Override
    public SkyItemStack.SkyItemStackBuilder process(SkyItemStack.SkyItemStackBuilder builder) {
        Faction faction = SkyApi.getInstance().getFactionAPI().getCachedFaction(getPLAYER().getUniqueId());

        if (faction != null && faction.isOwner(getPLAYER())) {
            builder.lore(Messages.FACTION_LEAVE_OWNER_CONFIRMATION_LORE.getStringList(SkyApi.getInstance().getPlayerAPI().getLocale(getPLAYER().getUniqueId())));
        }

        return builder;
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        player.closeInventory();
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

        SkyApi.getInstance().getFactionAPI().getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            } else if (faction == null) {
                Messages.NOT_IN_FACTION.send(player, locale);
                return;
            }

            if (SkyApi.getInstance().getRegionAPI().isLocationInRegion(player.getLocation(), "sfr_faction_" + faction.getName())) {
                SkyApi.getInstance().getIslandAPI().getPlayerIsland(player.getUniqueId()).whenComplete((island, throwable) -> {
                    if (throwable != null) {
                        ErrorUtil.handleError(player, "get your island", "SQL_ISLAND_GET", throwable);
                        return;
                    } else if (island != null) SkyApi.getInstance().getIslandAPI().teleportPlayerToIsland(player, island);
                        else player.teleport(SkyApi.getInstance().getRegionAPI().getHubLocation());

                    faction.leaveFaction(player);
                    Messages.FACTION_LEAVE_SUCCESS.send(player, locale, "faction_name", faction.getName());
                });
            }
        });
    }
}
