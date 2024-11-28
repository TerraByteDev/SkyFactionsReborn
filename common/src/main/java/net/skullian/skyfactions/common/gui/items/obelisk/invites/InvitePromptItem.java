package net.skullian.skyfactions.common.gui.items.obelisk.invites;

import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.impl.old.SkyItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

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
