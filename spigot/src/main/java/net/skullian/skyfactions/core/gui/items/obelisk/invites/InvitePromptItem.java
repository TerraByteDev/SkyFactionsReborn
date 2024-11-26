package net.skullian.skyfactions.core.gui.items.obelisk.invites;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.impl.SkyItem;

public class InvitePromptItem extends SkyItem {

    public InvitePromptItem(ItemData data, ItemStack stack, String name, Player player) {
        super(data, stack, player, List.of(name).toArray());
    }

    @Override
    public Object[] replacements() {
        String data = (String) getOptionals()[0];

        return List.of(
            "faction_name", data
        ).toArray();
    }
}
