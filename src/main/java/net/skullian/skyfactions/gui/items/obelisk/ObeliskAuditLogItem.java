package net.skullian.skyfactions.gui.items.obelisk;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.gui.obelisk.FactionAuditLogUI;
import net.skullian.skyfactions.util.ErrorHandler;

public class ObeliskAuditLogItem extends SkyItem {

    public ObeliskAuditLogItem(ItemData data, ItemStack stack) {
        super(data, stack, null, null);
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        FactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (faction == null) {
                Messages.ERROR.send(player, "%operation%", "get your Faction", "FACTION_NOT_FOUND");
                return;
            } else if (ex != null) {
                ErrorHandler.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            }

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
            if (faction.getOwner().equals(offlinePlayer) || faction.getAdmins().contains(offlinePlayer)) {
                FactionAuditLogUI.promptPlayer(player);
            } else {
                Messages.OBELISK_GUI_DENY.send(player, "%rank%", Messages.FACTION_ADMIN_TITLE.get());
            }
        });
    }

}
