package net.skullian.skyfactions.gui.items.obelisk.invites;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.database.struct.InviteData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.gui.screens.obelisk.invites.PlayerManageIncomingInviteUI;

public class PlayerFactionInvitePaginationItem extends SkyItem {

    private InviteData DATA;

    public PlayerFactionInvitePaginationItem(ItemData data, ItemStack stack, Player player, InviteData inviteData) {
        super(data, stack, player, List.of(inviteData).toArray());

        this.DATA = inviteData;
    }

    @Override
    public Object[] replacements() {
        InviteData data = (InviteData) getOptionals()[0];

        return List.of(
            "faction_name", data.getFactionName(),
            "player_name", data.getInviter().getName()
        ).toArray();
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        PlayerManageIncomingInviteUI.promptPlayer(player, DATA);
    }

}
