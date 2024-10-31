package net.skullian.skyfactions.gui.items.obelisk.invites;

import net.skullian.skyfactions.event.PlayerHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.db.InviteData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.gui.obelisk.invites.JoinRequestsUI;
import net.skullian.skyfactions.util.ErrorHandler;

public class FactionJoinRequestAcceptItem extends SkyItem {

    private InviteData DATA;

    public FactionJoinRequestAcceptItem(ItemData data, ItemStack stack, InviteData inviteData) {
        super(data, stack, null, null);

        this.DATA = inviteData;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.getInventory().close();

        FactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (faction == null) {
                Messages.ERROR.send(player, PlayerHandler.getLocale(player.getUniqueId()), "operation", "get your Faction", "FACTION_NOT_FOUND");
                return;
            } else if (ex != null) {
                ErrorHandler.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            }

            faction.acceptJoinRequest(DATA, player);
            JoinRequestsUI.promptPlayer(player);

            Messages.FACTION_JOIN_REQUEST_ACCEPT_SUCCESS.send(player, PlayerHandler.getLocale(player.getUniqueId()), "player_name", DATA.getPlayer().getName());
        });
    }


}
