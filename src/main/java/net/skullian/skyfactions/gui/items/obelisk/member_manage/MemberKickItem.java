package net.skullian.skyfactions.gui.items.obelisk.member_manage;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.faction.AuditLogType;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.util.ErrorHandler;

public class MemberKickItem extends SkyItem {

    private OfflinePlayer SUBJECT;

    public MemberKickItem(ItemData data, ItemStack stack, OfflinePlayer player) {
        super(data, stack, null);
        
        this.SUBJECT = player;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        FactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, exc) -> {
            if (exc != null) {
                ErrorHandler.handleError(player, "kick a player", "SQL_FACTION_GET", exc);
                return;
            }

            if (faction != null) {
                if (faction.getAllMembers().contains(SUBJECT)) {
                    // todo discord support & or announce in chat?
                    faction.createAuditLog(SUBJECT.getUniqueId(), AuditLogType.PLAYER_KICK, "%kicked%", SUBJECT.getName(), "%player%", player.getName());
                    faction.kickPlayer(SUBJECT, player);

                    Messages.FACTION_MANAGE_KICK_SUCCESS.send(player, "%player%", SUBJECT.getName());
                } else {
                    Messages.ERROR.send(player, "%operation%", "kick a player", "%debug%", "FACTION_MEMBER_UNKNOWN");
                    event.getInventory().close();
                }
            } else {
                Messages.ERROR.send(player, "%operation%", "kick a player", "%debug%", "FACTION_NOT_EXIST");
                event.getInventory().close();
            }
        });


    }
}
