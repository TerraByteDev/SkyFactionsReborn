package net.skullian.skyfactions.util.worldborder.persistence;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.WorldBorderAPI;
import net.skullian.skyfactions.util.worldborder.BorderAPI;
import net.skullian.skyfactions.util.worldborder.BorderPos;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.function.Consumer;

public class BorderPersistence implements BorderAPI.PersistentBorderAPI {

    private final BorderAPI borderAPI;
    private final NamespacedKey dataKey;
    private final BorderDataTags tags;

    public BorderPersistence(WorldBorderAPI api) {
        SkyFactionsReborn p = SkyFactionsReborn.getInstance();
        this.borderAPI = api;
        this.dataKey = new NamespacedKey(p, "world_border_data");
        this.tags = new BorderDataTags();
    }

    @Override
    public WBData getWBData(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        if (container.has(dataKey, tags)) {
            return container.get(dataKey, tags);
        }

        return null;
    }

    @Override
    public void resetBorder(Player player) {
        borderAPI.resetBorder(player);
        PersistentDataContainer container = player.getPersistentDataContainer();
        if (container.has(dataKey, tags)) {
            container.remove(dataKey);
        }
    }

    @Override
    public void setWorldBorder(Player player, double radius, BorderPos location) {
        borderAPI.setWorldBorder(player, radius, location);
        update(player, data -> data.setSize(radius));
    }

    private void update(Player player, Consumer<WBData> consumer) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        WBData data = new WBData();
        if (container.has(dataKey, tags)) {
            data = container.get(dataKey, tags);
        }

        consumer.accept(data);
        container.set(dataKey, tags, data);
    }
}
