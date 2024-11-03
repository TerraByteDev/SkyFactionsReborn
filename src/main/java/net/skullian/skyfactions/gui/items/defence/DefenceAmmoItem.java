package net.skullian.skyfactions.gui.items.defence;

import net.skullian.skyfactions.defence.struct.DefenceData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DefenceAmmoItem extends SkyItem {

    public DefenceAmmoItem(ItemData data, ItemStack stack, Player player, DefenceData defenceData) {
        super(data, stack, player, List.of(defenceData).toArray());
    }

    @Override
    public Object[] replacements() {
        DefenceData data = (DefenceData) getOptionals()[0];

        return List.of(
                "ammo", data.getAMMO()
        ).toArray();
    }

    @Override
    public void onClick(ClickType clickType, Player player, InventoryClickEvent event) {
        // todo
    }
}
