package net.skullian.skyfactions.core.gui.items.obelisk.invites;

import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.common.faction.AuditLogType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.core.api.SpigotFactionAPI;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.impl.SkyItem;
import net.skullian.skyfactions.core.gui.screens.obelisk.invites.JoinRequestsUI;
import net.skullian.skyfactions.core.util.ErrorUtil;

public class FactionJoinRequestAcceptItem extends SkyItem {

    private InviteData DATA;

    public FactionJoinRequestAcceptItem(ItemData data, ItemStack stack, InviteData inviteData, Player player) {
        super(data, stack, player, null);

        this.DATA = inviteData;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.getInventory().close();

        SpigotFactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (faction == null) {
                Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "get your Faction", "FACTION_NOT_FOUND");
                return;
            } else if (ex != null) {
                ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            }

            faction.revokeInvite(DATA, AuditLogType.JOIN_REQUEST_ACCEPT, "player_name", DATA.getPlayer().getName(), "member", player.getName());
            JoinRequestsUI.promptPlayer(player);

            Messages.FACTION_JOIN_REQUEST_ACCEPT_SUCCESS.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "player_name", DATA.getPlayer().getName());
        });
    }


}
