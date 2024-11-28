package net.skullian.skyfactions.common.gui.items.obelisk.invites;

import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.common.faction.AuditLogType;
import net.skullian.skyfactions.common.util.text.TextUtility;
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

import java.util.List;

public class OutgoingInvitePaginationItem extends SkyItem {

    private InviteData DATA;

    public OutgoingInvitePaginationItem(ItemData data, ItemStack stack, InviteData inviteData, Player player) {
        super(data, stack, player, List.of(inviteData).toArray());
        
        this.DATA = inviteData;
    }

    @Override
    public Object[] replacements() {
        InviteData data = (InviteData) getOptionals()[0];

        return List.of(
            "inviter", data.getInviter().getName(),
            "player_name", data.getPlayer().getName(),
            "timestamp", TextUtility.formatExtendedElapsedTime(data.getTimestamp())
        ).toArray();
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (clickType.isRightClick()) {
            event.getInventory().close();

            SpigotFactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
                if (faction == null) {
                    Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "get your Faction", "FACTION_NOT_FOUND");
                    return;
                } else if (ex != null) {
                    ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                    return;
                }
                faction.revokeInvite(DATA, AuditLogType.INVITE_REVOKE, "player", player.getName(), "invited", DATA.getPlayer().getName());
                Messages.FACTION_INVITE_REVOKE_SUCCESS.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "player_name", DATA.getPlayer().getName());
            });
        }
    }

}
