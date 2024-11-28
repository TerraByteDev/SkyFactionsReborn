package net.skullian.skyfactions.core.gui.items.defence;

import net.skullian.skyfactions.common.defence.struct.DefenceData;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.impl.old.SkyItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DefenceDisplayItem extends SkyItem {

    public DefenceDisplayItem(ItemData data, ItemStack stack, Player player, DefenceStruct struct, DefenceData defenceData) {
        super(data, stack, player, List.of(struct, defenceData).toArray());
    }

    @Override
    public Object[] replacements() {
        DefenceStruct struct = (DefenceStruct) getOptionals()[0];
        DefenceData data = (DefenceData) getOptionals()[1];
        return List.of(
                "defence_name", struct.getNAME(),
                "ammo", data.getAMMO(),
                "level", data.getLEVEL(),
                "durability", data.getDURABILITY()
        ).toArray();
    }

}
