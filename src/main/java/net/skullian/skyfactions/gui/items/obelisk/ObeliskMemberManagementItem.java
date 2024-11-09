package net.skullian.skyfactions.gui.items.obelisk;

import net.skullian.skyfactions.event.PlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.gui.screens.obelisk.member.MemberManagementUI;
import net.skullian.skyfactions.util.ErrorUtil;

public class ObeliskMemberManagementItem extends SkyItem {

    private Faction FACTION;

    public ObeliskMemberManagementItem(ItemData data, ItemStack stack, Faction faction, Player player) {
        super(data, stack, player, null);
        this.FACTION = faction;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (FACTION.isOwner(player) || FACTION.isAdmin(player) || FACTION.isModerator(player)) {

            FactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
                if (faction == null) {
                    Messages.ERROR.send(player, PlayerHandler.getLocale(player.getUniqueId()), "operation", "get your Faction", "FACTION_NOT_FOUND");
                    return;
                } else if (ex != null) {
                    ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                    return;
                }

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
                if (faction.getOwner().equals(offlinePlayer) || faction.getAdmins().contains(offlinePlayer)) {
                    MemberManagementUI.promptPlayer(player);
                } else {
                    Messages.OBELISK_GUI_DENY.send(player, PlayerHandler.getLocale(player.getUniqueId()), "rank", Messages.FACTION_ADMIN_TITLE.get(PlayerHandler.getLocale(player.getUniqueId())));
                }
            });
        } else {
            Messages.OBELISK_GUI_DENY.send(player, PlayerHandler.getLocale(player.getUniqueId()), "rank", Messages.FACTION_MODERATOR_TITLE.get(PlayerHandler.getLocale(player.getUniqueId())));
        }
    }

}
