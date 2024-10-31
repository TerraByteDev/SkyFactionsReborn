package net.skullian.skyfactions.gui.items.obelisk.invites;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;

public class InvitePromptItem extends SkyItem {

    public InvitePromptItem(ItemData data, ItemStack stack, String name) {
        super(data, stack, null, List.of(name).toArray());
    }

    @Override
    public Object[] replacements() {
        String data = (String) getOptionals()[0];

        return List.of(
            "faction_name", data
        ).toArray();
    }
}
