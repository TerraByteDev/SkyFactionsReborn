package net.skullian.skyfactions.common.gui.items.obelisk.invites;

import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

import java.util.List;

public class InvitePromptItem extends SkyItem {

    public InvitePromptItem(ItemData data, SkyItemStack stack, String name, SkyUser player) {
        super(data, stack, player, List.of(name).toArray());
    }

    @Override
    public Object[] replacements() {
        String data = (String) getOPTIONALS()[0];

        return List.of(
            "faction_name", data
        ).toArray();
    }
}
