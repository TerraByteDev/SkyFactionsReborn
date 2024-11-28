package net.skullian.skyfactions.common.gui.items.obelisk.member_manage;

import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.impl.old.SkyItem;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MemberPromptItem extends SkyItem {

    public MemberPromptItem(ItemData data, ItemStack stack, OfflinePlayer player, Player viewer) {
        super(data, stack, viewer, List.of(player).toArray());
    }

    @Override
    public Object[] replacements() {
        OfflinePlayer subject = (OfflinePlayer) getOptionals()[0];

        return List.of(
            "player_name", subject.getName()
        ).toArray();
    }


}
