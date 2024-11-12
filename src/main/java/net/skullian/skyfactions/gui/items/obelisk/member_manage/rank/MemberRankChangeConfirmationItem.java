package net.skullian.skyfactions.gui.items.obelisk.member_manage.rank;

import lombok.Setter;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MemberRankChangeConfirmationItem extends SkyItem {

    @Setter
    private String SELECTED = "N/A";

    public MemberRankChangeConfirmationItem(ItemData data, ItemStack stack, Player player, Faction faction, OfflinePlayer subject, String type) {
        super(data, stack, player, List.of(faction, subject, type).toArray());
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {

    }


}
