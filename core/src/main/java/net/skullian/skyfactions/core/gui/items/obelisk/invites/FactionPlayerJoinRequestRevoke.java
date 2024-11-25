package net.skullian.skyfactions.core.gui.items.obelisk.invites;

import net.skullian.skyfactions.core.api.FactionAPI;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.common.faction.AuditLogType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.core.api.SpigotNotificationAPI;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.common.faction.JoinRequestData;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.impl.SkyItem;
import net.skullian.skyfactions.core.util.ErrorUtil;

public class FactionPlayerJoinRequestRevoke extends SkyItem {

    private JoinRequestData DATA;

    public FactionPlayerJoinRequestRevoke(ItemData data, ItemStack stack, JoinRequestData joinRequestData, Player player) {
        super(data, stack, player, null);
        
        this.DATA = joinRequestData;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.getInventory().close();

        FactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, throwable) -> {
            if (throwable != null) {
                ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", throwable);
                return;
            }

            faction.revokeInvite(faction.toInviteData(DATA, player), AuditLogType.PLAYER_JOIN_REQUEST_REVOKE, "player_name", player.getName());

            Messages.FACTION_JOIN_REQUEST_REVOKE_SUCCESS.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "faction_name", DATA.getFactionName());
            SpigotNotificationAPI.factionInviteStore.replace(DATA.getFactionName(), (SpigotNotificationAPI.factionInviteStore.get(DATA.getFactionName()) - 1));
        });
    }
}
