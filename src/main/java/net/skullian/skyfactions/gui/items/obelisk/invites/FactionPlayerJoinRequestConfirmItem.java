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
import net.skullian.skyfactions.faction.JoinRequestData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.util.ErrorHandler;

public class FactionPlayerJoinRequestConfirmItem extends SkyItem {
    private JoinRequestData DATA;

    public FactionPlayerJoinRequestConfirmItem(ItemData data, ItemStack stack, JoinRequestData joinRequestData) {
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
                    faction.addFactionMember(player.getUniqueId()),
                    SkyFactionsReborn.databaseHandler.revokeInvite(DATA.getFactionName(), player.getUniqueId(), "incoming")
            ).whenComplete((ignored, exc) -> {
                if (exc != null) {
                    ErrorHandler.handleError(player, "accept a join request", "SQL_FACTION_GET", exc);
                    return;
                }

                Messages.PLAYER_FACTION_JOIN_SUCCESS.send(player, player.locale().getLanguage(), "%faction_name%", DATA.getFactionName());
                NotificationAPI.factionInviteStore.replace(faction.getName(), (NotificationAPI.factionInviteStore.get(faction.getName()) - 1));
            });
        });
    }

}
