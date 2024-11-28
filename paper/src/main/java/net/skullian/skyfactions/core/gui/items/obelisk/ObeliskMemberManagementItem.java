package net.skullian.skyfactions.core.gui.items.obelisk;

import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.config.types.Settings;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.impl.old.SkyItem;
import net.skullian.skyfactions.core.gui.screens.obelisk.member.MemberManagementUI;
import net.skullian.skyfactions.common.util.text.TextUtility;
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
            Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "get your Faction", "FACTION_NOT_FOUND");
        } else if (!TextUtility.merge(Settings.FACTION_KICK_PERMISSIONS.getList(), Settings.FACTION_BAN_PERMISSIONS.getList(), Settings.FACTION_MANAGE_RANK_PERMISSIONS.getList()).contains(FACTION.getRankType(player.getUniqueId()).getRankValue())) {
            Messages.OBELISK_GUI_DENY.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "rank", Messages.FACTION_ADMIN_TITLE.get(SpigotPlayerAPI.getLocale(player.getUniqueId())));
        } else {
            MemberManagementUI.promptPlayer(player, FACTION);
        }
    }

}
