package net.skullian.skyfactions.gui.items.obelisk.invites;

import net.skullian.skyfactions.event.PlayerHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.NotificationAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.faction.JoinRequestData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.util.ErrorUtil;

public class FactionPlayerJoinRequestRevoke extends SkyItem {

    private JoinRequestData DATA;

    public FactionPlayerJoinRequestRevoke(ItemData data, ItemStack stack, JoinRequestData joinRequestData, Player player) {
        super(data, stack, player, null);
        
        this.DATA = joinRequestData;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.getInventory().close();

        SkyFactionsReborn.databaseManager.factionInvitesManager.revokeInvite(player.getUniqueId(), DATA.getFactionName(), "incoming").whenComplete((ignored, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "revoke a Faction join request", "SQL_JOIN_REQUEST_REJECT", ex);
                return;
            }

            Messages.FACTION_JOIN_REQUEST_REVOKE_SUCCESS.send(player, PlayerHandler.getLocale(player.getUniqueId()), "faction_name", DATA.getFactionName());
            NotificationAPI.factionInviteStore.replace(DATA.getFactionName(), (NotificationAPI.factionInviteStore.get(DATA.getFactionName()) - 1));
        });
    }
}
