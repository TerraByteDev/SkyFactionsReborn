package net.skullian.skyfactions.paper.util.worldborder;

import net.skullian.skyfactions.common.api.WorldBorderAPI;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.worldborder.BorderAPI;
import net.skullian.skyfactions.common.util.worldborder.BorderPos;
import net.skullian.skyfactions.common.util.worldborder.persistence.WBData;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
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
    public WBData getWBData(SkyUser player) {
        if (!player.isOnline()) return null;
        Player bukkitPlayer = SpigotAdapter.adapt(player).getPlayer();

        PersistentDataContainer container = bukkitPlayer.getPersistentDataContainer();
        if (container.has(dataKey, tags)) {
            return container.get(dataKey, tags);
        }

        return null;
    }

    @Override
    public void resetBorder(SkyUser player) {
        if (!player.isOnline()) return;
        Player bukkitPlayer = SpigotAdapter.adapt(player).getPlayer();

        borderAPI.resetBorder(player);
        PersistentDataContainer container = bukkitPlayer.getPersistentDataContainer();
        if (container.has(dataKey, tags)) {
            container.remove(dataKey);
        }
    }

    @Override
    public void setWorldBorder(SkyUser player, double radius, BorderPos location) {
        if (!player.isOnline()) return;

        borderAPI.setWorldBorder(player, radius, location);
        update(SpigotAdapter.adapt(player).getPlayer(), data -> data.setSize(radius));
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
