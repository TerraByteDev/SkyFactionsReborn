package net.skullian.skyfactions.core.gui.items.obelisk.invites;

import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.core.api.SpigotFactionAPI;
import net.skullian.skyfactions.core.api.SpigotNotificationAPI;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.common.faction.AuditLogType;
import net.skullian.skyfactions.common.faction.JoinRequestData;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.impl.old.SkyItem;
import net.skullian.skyfactions.core.util.ErrorUtil;

public class FactionPlayerJoinRequestDenyItem extends SkyItem {

    private JoinRequestData DATA;

    public FactionPlayerJoinRequestDenyItem(ItemData data, ItemStack stack, JoinRequestData joinRequestData, Player player) {
        super(data, stack, player, null);
        
        this.DATA = joinRequestData;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.getInventory().close();

        SpigotFactionAPI.getFaction(DATA.getFactionName()).whenComplete((faction, ex) -> {
            if (faction == null) {
                Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "get your Faction", "FACTION_NOT_FOUND");
                return;
            } else if (ex != null) {
                ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            }

            faction.revokeInvite(faction.toInviteData(DATA, player), AuditLogType.PLAYER_JOIN, "player_name", player.getName());

            SpigotNotificationAPI.factionInviteStore.replace(faction.getName(), (SpigotNotificationAPI.factionInviteStore.get(faction.getName()) - 1));
            Messages.FACTION_JOIN_REQUEST_DENY_SUCCESS.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "faction_name", DATA.getFactionName());
        });
    }


}
