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

public class PlayerIncomingInviteAccept extends SkyItem {

    private InviteData DATA;

    public PlayerIncomingInviteAccept(ItemData data, ItemStack stack, InviteData inviteData) {
        super(data, stack, null, null);
        
        this.DATA = inviteData;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.getInventory().close();

        FactionAPI.isInFaction(player).whenComplete((isInFaction, ex) -> {
            if (ex != null) {
                ErrorHandler.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            } else if (isInFaction) {
                Messages.ALREADY_IN_FACTION.send(player, player.locale().getLanguage());
                return;
            }

            FactionAPI.getFaction(DATA.getFactionName()).whenComplete((faction, throwable) -> {
                if (throwable != null) {
                    ErrorHandler.handleError(player, "get the Faction", "SQL_FACTION_GET", throwable);
                    return;
                }

                CompletableFuture.allOf(
                        SkyFactionsReborn.databaseHandler.revokeInvite(DATA.getFactionName(), player.getUniqueId(), "outgoing"),
                        faction.addFactionMember(player.getUniqueId()),
                        faction.createAuditLog(player.getUniqueId(), AuditLogType.INVITE_ACCEPT, "%player_name%", player.getName())
                ).whenComplete((ignored, exce) -> {
                    if (exce != null) {
                        ErrorHandler.handleError(player, "accept an invite", "SQL_INVITE_ACEPT", exce);
                        return;
                    }

                    Messages.PLAYER_FACTION_JOIN_SUCCESS.send(player, player.locale().getLanguage(), "%faction_name%", player.getName());
                });
            });
        });
    }
}
