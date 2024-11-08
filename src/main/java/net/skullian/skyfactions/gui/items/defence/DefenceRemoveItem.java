package net.skullian.skyfactions.gui.items.defence;

import net.skullian.skyfactions.api.DefenceAPI;
import net.skullian.skyfactions.defence.Defence;
import net.skullian.skyfactions.defence.struct.DefenceData;
import net.skullian.skyfactions.defence.struct.DefenceStruct;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DefenceRemoveItem extends SkyItem {

    private final boolean FACTION;

    public DefenceRemoveItem(ItemData data, ItemStack stack, Player player, DefenceStruct struct, DefenceData defenceData, boolean faction) {
        super(data, stack, player, List.of(struct, defenceData).toArray());

        this.FACTION = faction;
    }

    @Override
    public void onClick(ClickType clickType, Player player, InventoryClickEvent event) {
        DefenceStruct struct = (DefenceStruct) getOptionals()[0];
        DefenceData defenceData = (DefenceData) getOptionals()[1];

        Defence defence = DefenceAPI.getDefenceFromData(defenceData);
        defence.disable();

        defence.remove();
//        DefencesFactory.addDefence();
    }
}
