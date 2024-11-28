package net.skullian.skyfactions.core.gui.items.obelisk.invites;

import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.core.api.SpigotFactionAPI;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.common.faction.AuditLogType;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.impl.old.SkyItem;
import net.skullian.skyfactions.core.util.ErrorUtil;

public class PlayerIncomingInviteDeny extends SkyItem {

    private InviteData DATA;

    public PlayerIncomingInviteDeny(ItemData data, ItemStack stack, InviteData inviteData, Player player) {
        super(data, stack, player, null);

        this.DATA = inviteData;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.getInventory().close();
        SpigotFactionAPI.getFaction(DATA.getFactionName()).whenComplete((faction, ex) -> {
            if (faction == null) {
                Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "get the Faction", "FACTION_NOT_FOUND");
                return;
            } else if (ex != null) {
                ErrorUtil.handleError(player, "get the Faction", "SQL_FACTION_GET", ex);
                return;
            }

            faction.revokeInvite(DATA, AuditLogType.INVITE_DENY, "player_name", player.getName());
            Messages.FACTION_INVITE_DENY_SUCCESS.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "faction_name", faction.getName());
        });
    }

}
