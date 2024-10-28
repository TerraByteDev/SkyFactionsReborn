package net.skullian.skyfactions.gui.items.obelisk.member_manage;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;

public class MemberPromptItem extends SkyItem {

    public MemberPromptItem(ItemData data, ItemStack stack, OfflinePlayer player) {
        super(data, stack, null, List.of(player).toArray());
    }

    @Override
    public Object[] replacements() {
        OfflinePlayer subject = (OfflinePlayer) getOptionals()[0];

        return List.of(
            "%player_name%", subject.getName()
        ).toArray();
    }


}
