package net.skullian.skyfactions.gui.items.obelisk.invites;

import java.util.concurrent.CompletableFuture;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.NotificationAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.faction.AuditLogType;
import net.skullian.skyfactions.faction.JoinRequestData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.util.ErrorHandler;

public class FactionPlayerJoinRequestDenyItem extends SkyItem {

    private JoinRequestData DATA;

    public FactionPlayerJoinRequestDenyItem(ItemData data, ItemStack stack, JoinRequestData joinRequestData) {
        super(data, stack, null, null);
        
        this.DATA = joinRequestData;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.getInventory().close();

        FactionAPI.getFaction(DATA.getFactionName()).whenComplete((faction, ex) -> {
            if (faction == null) {
                Messages.ERROR.send(player, player.locale().getLanguage(), "%operation%", "get your Faction", "FACTION_NOT_FOUND");
                return;
            } else if (ex != null) {
                ErrorHandler.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            }

            CompletableFuture.allOf(
                    faction.createAuditLog(player.getUniqueId(), AuditLogType.PLAYER_JOIN_REQUEST_REVOKE, "%player_name%", player.getName()),
                    SkyFactionsReborn.databaseHandler.revokeInvite(DATA.getFactionName(), player.getUniqueId(), "incoming")
            ).whenComplete((ignored, exc) -> {
                if (exc != null) {
                    ErrorHandler.handleError(player, "deny a join request", "SQL_JOIN_REQUEST_REVOKE", exc);
                    return;
                }

                NotificationAPI.factionInviteStore.replace(faction.getName(), (NotificationAPI.factionInviteStore.get(faction.getName()) - 1));
                Messages.FACTION_JOIN_REQUEST_DENY_SUCCESS.send(player, player.locale().getLanguage(), "%faction_name%", DATA.getFactionName());
            });
        });
    }


}
