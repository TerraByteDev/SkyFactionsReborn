package net.skullian.skyfactions.core.gui.items.obelisk;

import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.core.api.FactionAPI;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.impl.SkyItem;
import net.skullian.skyfactions.core.gui.screens.obelisk.FactionAuditLogUI;
import net.skullian.skyfactions.core.util.ErrorUtil;

public class ObeliskAuditLogItem extends SkyItem {

    public ObeliskAuditLogItem(ItemData data, ItemStack stack, Player player) {
        super(data, stack, player, null);
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        FactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (faction == null) {
                Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "get your Faction", "FACTION_NOT_FOUND");
                return;
            } else if (ex != null) {
                ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            }

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
            if (faction.getOwner().equals(offlinePlayer) || faction.getAdmins().contains(offlinePlayer)) {
                FactionAuditLogUI.promptPlayer(player, faction);
            } else {
                Messages.OBELISK_GUI_DENY.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "rank", Messages.FACTION_ADMIN_TITLE.get(SpigotPlayerAPI.getLocale(player.getUniqueId())));
            }
        });
    }

}
