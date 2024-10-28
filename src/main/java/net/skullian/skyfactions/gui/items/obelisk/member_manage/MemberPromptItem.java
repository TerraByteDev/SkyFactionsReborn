package net.skullian.skyfactions.gui.items.obelisk.member_manage;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;

public class MemberPromptItem extends SkyItem {

    private OfflinePlayer SUBJECT;

    public MemberPromptItem(ItemData data, ItemStack stack, OfflinePlayer player) {
        super(data, stack, null);
        
        this.SUBJECT = player;
    }

    @Override
    public Object[] replacements() {
        return List.of(
            "%player_name%", SUBJECT.getName()
        ).toArray();
    }


}
