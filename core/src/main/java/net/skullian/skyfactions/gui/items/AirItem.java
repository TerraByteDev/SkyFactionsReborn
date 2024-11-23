package net.skullian.skyfactions.gui.items;

import net.skullian.skyfactions.api.GUIAPI;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

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

    public AirItem(Player player) {
        super(airItemData, GUIAPI.createItem(airItemData, player.getUniqueId()), player, null);
    }
}
