package net.skullian.skyfactions.gui.items.obelisk;

import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.api.PlayerAPI;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.gui.screens.obelisk.member.MemberManagementUI;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ObeliskMemberManagementItem extends SkyItem {

    private Faction FACTION;

    public ObeliskMemberManagementItem(ItemData data, ItemStack stack, Faction faction, Player player) {
        super(data, stack, player, null);
        this.FACTION = faction;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (FACTION == null) {
            Messages.ERROR.send(player, PlayerAPI.getLocale(player.getUniqueId()), "operation", "get your Faction", "FACTION_NOT_FOUND");
        } else if (!TextUtility.merge(Settings.FACTION_KICK_PERMISSIONS.getList(), Settings.FACTION_BAN_PERMISSIONS.getList(), Settings.FACTION_MANAGE_RANK_PERMISSIONS.getList()).contains(FACTION.getRankType(player.getUniqueId()).getRankValue())) {
            Messages.OBELISK_GUI_DENY.send(player, PlayerAPI.getLocale(player.getUniqueId()), "rank", Messages.FACTION_ADMIN_TITLE.get(PlayerAPI.getLocale(player.getUniqueId())));
        } else {
            MemberManagementUI.promptPlayer(player, FACTION);
        }
    }

}
