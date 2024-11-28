package net.skullian.skyfactions.common.gui.items.obelisk.invites;

import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.common.faction.AuditLogType;
import net.skullian.skyfactions.core.api.SpigotFactionAPI;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.impl.old.SkyItem;
import net.skullian.skyfactions.core.util.ErrorUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerIncomingInviteAccept extends SkyItem {

    private InviteData DATA;

    public PlayerIncomingInviteAccept(ItemData data, ItemStack stack, InviteData inviteData, Player player) {
        super(data, stack, player, null);
        
        this.DATA = inviteData;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.getInventory().close();

        SpigotFactionAPI.isInFaction(player).whenComplete((isInFaction, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            } else if (isInFaction) {
                Messages.ALREADY_IN_FACTION.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
                return;
            }

            SpigotFactionAPI.getFaction(DATA.getFactionName()).whenComplete((faction, throwable) -> {
                if (throwable != null) {
                    ErrorUtil.handleError(player, "get the Faction", "SQL_FACTION_GET", throwable);
                    return;
                }

                faction.revokeInvite(DATA, AuditLogType.INVITE_ACCEPT, "player_name", player.getName());
                faction.addFactionMember(player);

                Messages.PLAYER_FACTION_JOIN_SUCCESS.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "faction_name", faction.getName());
            });
        });
    }
}
