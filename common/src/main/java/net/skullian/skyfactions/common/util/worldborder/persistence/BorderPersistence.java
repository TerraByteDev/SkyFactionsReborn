package net.skullian.skyfactions.common.util.worldborder.persistence;

import net.skullian.skyfactions.common.api.WorldBorderAPI;
import net.skullian.skyfactions.common.util.worldborder.BorderAPI;
import net.skullian.skyfactions.common.util.worldborder.BorderPos;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public class BorderPersistence implements BorderAPI.PersistentBorderAPI {

    private final BorderAPI borderAPI;
    private final NamespacedKey dataKey;
    private final BorderDataTags tags;

    public BorderPersistence(WorldBorderAPI api, JavaPlugin plugin) {
        this.borderAPI = api;
        this.dataKey = new NamespacedKey(plugin, "world_border_data");
        this.tags = new BorderDataTags(plugin);
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
