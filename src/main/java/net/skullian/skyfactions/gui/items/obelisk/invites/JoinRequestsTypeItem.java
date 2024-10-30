package net.skullian.skyfactions.gui.items.obelisk.invites;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.gui.obelisk.invites.JoinRequestsUI;
import net.skullian.skyfactions.gui.obelisk.invites.PlayerOutgoingRequestManageUI;
import net.skullian.skyfactions.util.ErrorHandler;

public class JoinRequestsTypeItem extends SkyItem {

    private String TYPE;

    public JoinRequestsTypeItem(ItemData data, ItemStack stack, String type) {
        super(data, stack, null, null);
        
        this.TYPE = type;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (TYPE.equals("faction")) {
            JoinRequestsUI.promptPlayer(player);
        } else if (TYPE.equals("player")) {
            SkyFactionsReborn.databaseHandler.getPlayerOutgoingJoinRequest(player).whenComplete((joinRequest, ex) -> {
                if (ex != null) {
                    ErrorHandler.handleError(player, "get your outgoing join request", "SQL_JOIN_REQUEST_GET", ex);
                    return;
                }

                if (joinRequest == null) {
                    Messages.FACTION_JOIN_REQUEST_NOT_EXIST.send(player, player.locale());
                } else {
                    PlayerOutgoingRequestManageUI.promptPlayer(player, joinRequest);
                }
            });
        }
    }

}
