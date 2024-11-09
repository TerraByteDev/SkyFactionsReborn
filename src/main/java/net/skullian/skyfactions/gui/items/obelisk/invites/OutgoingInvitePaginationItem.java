package net.skullian.skyfactions.gui.items.obelisk.invites;

import java.util.List;

import net.skullian.skyfactions.event.PlayerHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.database.struct.InviteData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.util.ErrorUtil;
import net.skullian.skyfactions.util.text.TextUtility;

public class OutgoingInvitePaginationItem extends SkyItem {

    private InviteData DATA;

    public OutgoingInvitePaginationItem(ItemData data, ItemStack stack, InviteData inviteData, Player player) {
        super(data, stack, player, List.of(inviteData).toArray());
        
        this.DATA = inviteData;
    }

    @Override
    public Object[] replacements() {
        InviteData data = (InviteData) getOptionals()[0];

        return List.of(
            "inviter", data.getInviter().getName(),
            "player_name", data.getPlayer().getName(),
            "timestamp", TextUtility.formatExtendedElapsedTime(data.getTimestamp())
        ).toArray();
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (clickType.isRightClick()) {
            event.getInventory().close();

            FactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
                if (faction == null) {
                    Messages.ERROR.send(player, PlayerHandler.getLocale(player.getUniqueId()), "operation", "get your Faction", "FACTION_NOT_FOUND");
                    return;
                } else if (ex != null) {
                    ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                    return;
                }
                faction.revokeInvite(DATA, player);
                Messages.FACTION_INVITE_REVOKE_SUCCESS.send(player, PlayerHandler.getLocale(player.getUniqueId()), "player_name", DATA.getPlayer().getName());
            });
        }
    }

}
