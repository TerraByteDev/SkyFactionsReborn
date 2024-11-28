package net.skullian.skyfactions.common.gui.items;

import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.user.SkyUser;
import org.bukkit.Material;

import java.util.List;

public class AirItem extends SkyItem {
    private static final ItemData airItemData = new ItemData(
            "air",
            '\0',
            "",
            Material.AIR.name(),
            "",
            "NONE",
            0,
            List.of()
    );

    public AirItem(SkyUser player) {
        super(airItemData, GUIAPI.createItem(airItemData, player.getUniqueId()), player, null);
    }
}
