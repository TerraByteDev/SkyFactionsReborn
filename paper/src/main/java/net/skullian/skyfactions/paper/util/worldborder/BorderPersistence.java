package net.skullian.skyfactions.paper.util.worldborder;

import net.skullian.skyfactions.common.api.WorldBorderAPI;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.common.util.worldborder.BorderAPI;
import net.skullian.skyfactions.common.util.worldborder.BorderPos;
import net.skullian.skyfactions.common.util.worldborder.persistence.WBData;
import net.skullian.skyfactions.paper.PaperSharedConstants;
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
        this.dataKey = PaperSharedConstants.WORLD_BORDER_DATA_KEY;
        this.tags = new BorderDataTags(plugin);
    }

    @Override
    public WBData getWBData(SkyUser player) {
        Player bukkitPlayer = SpigotAdapter.adapt(player).getPlayer();
        if (bukkitPlayer != null) {
            PersistentDataContainer container = bukkitPlayer.getPersistentDataContainer();
            if (container.has(dataKey, tags)) {
                return container.get(dataKey, tags);
            }
        } else {
            SLogger.warn("Attempted to get an offline player's border data. UUID: {}", player.getUniqueId());
        }

        return null;
    }

    @Override
    public void resetBorder(SkyUser player) {
        Player bukkitPlayer = SpigotAdapter.adapt(player).getPlayer();

        if (bukkitPlayer != null) {
            borderAPI.resetBorder(player);
            PersistentDataContainer container = bukkitPlayer.getPersistentDataContainer();
            if (container.has(dataKey, tags)) {
                container.remove(dataKey);
            }
        } else {
            SLogger.warn("Attempted to reset an offline player's world border. UUID: {}", player.getUniqueId());
        }
    }

    @Override
    public void setWorldBorder(SkyUser player, double radius, BorderPos location) {
        Player bukkitPlayer = SpigotAdapter.adapt(player).getPlayer();

        if (bukkitPlayer != null) {
            borderAPI.setWorldBorder(player, radius, location);
            update(bukkitPlayer, data -> data.setSize(radius));
        } else {
            SLogger.warn("Attempted to modify an offline player's world border. UUID: {}", player.getUniqueId());
        }
    }

    private void update(Player player, Consumer<WBData> consumer) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        WBData data = new WBData();
        if (container.has(dataKey, tags)) {
            data = container.get(dataKey, tags);
        }

        if (data != null) {
            consumer.accept(data);
            container.set(dataKey, tags, data);
        } else {
            SLogger.warn("Attempted to update an offline player's world border data. UUID: {}", player.getUniqueId());
        }
    }
}
