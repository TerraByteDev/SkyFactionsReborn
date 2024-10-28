package net.skullian.skyfactions.gui.items.obelisk.invites;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.gui.obelisk.invites.OutgoingInvitesUI;

public class OutgoingInvitesTypeItem extends SkyItem {

    public OutgoingInvitesTypeItem(ItemData data, ItemStack stack) {
        super(data, stack, null, null);
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        OutgoingInvitesUI.promptPlayer(player);
    }


}
