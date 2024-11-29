package net.skullian.skyfactions.common.gui.items.defence;

import net.skullian.skyfactions.common.defence.struct.DefenceData;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

import java.util.List;

public class DefenceDisplayItem extends SkyItem {

    public DefenceDisplayItem(ItemData data, SkyItemStack stack, SkyUser player, DefenceStruct struct, DefenceData defenceData) {
        super(data, stack, player, List.of(struct, defenceData).toArray());
    }

    @Override
    public Object[] replacements() {
        DefenceStruct struct = (DefenceStruct) getOPTIONALS()[0];
        DefenceData data = (DefenceData) getOPTIONALS()[1];
        return List.of(
                "defence_name", struct.getNAME(),
                "ammo", data.getAMMO(),
                "level", data.getLEVEL(),
                "durability", data.getDURABILITY()
        ).toArray();
    }

}
