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

public class PlayerIncomingInviteAccept extends SkyItem {

    private InviteData DATA;

    public PlayerIncomingInviteAccept(ItemData data, ItemStack stack, InviteData inviteData, Player player) {
        super(data, stack, player, null);
        
        this.DATA = inviteData;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.getInventory().close();

        FactionAPI.isInFaction(player).whenComplete((isInFaction, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            } else if (isInFaction) {
                Messages.ALREADY_IN_FACTION.send(player, PlayerHandler.getLocale(player.getUniqueId()));
                return;
            }

            FactionAPI.getFaction(DATA.getFactionName()).whenComplete((faction, throwable) -> {
                if (throwable != null) {
                    ErrorUtil.handleError(player, "get the Faction", "SQL_FACTION_GET", throwable);
                    return;
                }

                CompletableFuture.allOf(
                        SkyFactionsReborn.databaseManager.factionInvitesManager.revokeInvite(player.getUniqueId(), DATA.getFactionName(), "outgoing"),
                        faction.addFactionMember(player.getUniqueId()),
                        faction.createAuditLog(player.getUniqueId(), AuditLogType.INVITE_ACCEPT, "player_name", player.getName())
                ).whenComplete((ignored, exce) -> {
                    if (exce != null) {
                        ErrorUtil.handleError(player, "accept an invite", "SQL_INVITE_ACEPT", exce);
                        return;
                    }

                    Messages.PLAYER_FACTION_JOIN_SUCCESS.send(player, PlayerHandler.getLocale(player.getUniqueId()), "faction_name", player.getName());
                });
            });
        });
    }
}
