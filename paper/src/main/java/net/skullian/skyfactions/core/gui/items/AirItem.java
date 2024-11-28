package net.skullian.skyfactions.core.gui.items;

import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.core.api.SpigotGUIAPI;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.impl.SpigotSkyItem;
import net.skullian.skyfactions.core.gui.items.impl.old.SkyItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class AirItem extends SpigotSkyItem {
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
