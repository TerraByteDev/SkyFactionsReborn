package net.skullian.skyfactions.gui.items.obelisk.invites;

import java.util.concurrent.CompletableFuture;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.db.InviteData;
import net.skullian.skyfactions.faction.AuditLogType;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.util.ErrorHandler;

public class PlayerIncomingInviteDeny extends SkyItem {

    private InviteData DATA;

    public PlayerIncomingInviteDeny(ItemData data, ItemStack stack, InviteData inviteData) {
        super(data, stack, null, null);

        this.DATA = inviteData;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.getInventory().close();
        FactionAPI.getFaction(DATA.getFactionName()).whenComplete((faction, ex) -> {
            if (faction == null) {
                Messages.ERROR.send(player, player.locale().getLanguage(), "%operation%", "get the Faction", "FACTION_NOT_FOUND");
                return;
            } else if (ex != null) {
                ErrorHandler.handleError(player, "get the Faction", "SQL_FACTION_GET", ex);
                return;
            }

            CompletableFuture.allOf(
                    faction.createAuditLog(player.getUniqueId(), AuditLogType.INVITE_DENY, "%player_name%", player.getName()),
                    SkyFactionsReborn.databaseHandler.revokeInvite(DATA.getFactionName(), player.getUniqueId(), "outgoing")
            ).whenComplete((ignored, throwable) -> {
                if (throwable != null) {
                    ErrorHandler.handleError(player, "deny an invite", "SQL_INVITE_DENY", throwable);
                    return;
                }

                Messages.FACTION_INVITE_DENY_SUCCESS.send(player, player.locale().getLanguage(), "%faction_name%", faction.getName());
            });
        });
    }

}
