package net.skullian.skyfactions.common.gui.items.obelisk.member_manage;

import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

import java.util.List;

public class MemberPromptItem extends SkyItem {

    public MemberPromptItem(ItemData data, SkyItemStack stack, SkyUser player, SkyUser viewer) {
        super(data, stack, viewer, List.of(player).toArray());
    }

    @Override
    public Object[] replacements() {
        SkyUser subject = (SkyUser) getOPTIONALS()[0];

        return List.of(
            "player_name", subject.getName()
        ).toArray();
    }


}
