package net.skullian.skyfactions.gui.items.obelisk.invites;

import java.util.concurrent.CompletableFuture;

import net.skullian.skyfactions.event.PlayerHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.database.struct.InviteData;
import net.skullian.skyfactions.faction.AuditLogType;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.util.ErrorUtil;

public class PlayerIncomingInviteDeny extends SkyItem {

    private InviteData DATA;

    public PlayerIncomingInviteDeny(ItemData data, ItemStack stack, InviteData inviteData, Player player) {
        super(data, stack, player, null);

        this.DATA = inviteData;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.getInventory().close();
        FactionAPI.getFaction(DATA.getFactionName()).whenComplete((faction, ex) -> {
            if (faction == null) {
                Messages.ERROR.send(player, PlayerHandler.getLocale(player.getUniqueId()), "operation", "get the Faction", "FACTION_NOT_FOUND");
                return;
            } else if (ex != null) {
                ErrorUtil.handleError(player, "get the Faction", "SQL_FACTION_GET", ex);
                return;
            }

            faction.revokeInvite(DATA, AuditLogType.INVITE_DENY, "player_name", player.getName());
            Messages.FACTION_INVITE_DENY_SUCCESS.send(player, PlayerHandler.getLocale(player.getUniqueId()), "faction_name", faction.getName());
        });
    }

}
