package net.skullian.skyfactions.gui.items.defence;

import net.skullian.skyfactions.defence.struct.DefenceData;
import net.skullian.skyfactions.defence.struct.DefenceStruct;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DefenceUpgradeItem extends SkyItem {

    public DefenceUpgradeItem(ItemData data, ItemStack stack, Player player, DefenceStruct struct, DefenceData defenceData) {
        super(data, stack, player, List.of(struct, defenceData).toArray());
    }

    @Override
    public Object[] replacements() {
        DefenceStruct struct = (DefenceStruct) getOptionals()[0];

        return List.of("upgrade_lore", struct.getUPGRADE_LORE()).toArray();
    }

    @Override
    public void onClick(ClickType clickType, Player player, InventoryClickEvent event) {
        // todo
    }
}