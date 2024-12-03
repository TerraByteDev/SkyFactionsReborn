package net.skullian.skyfactions.paper.api;

import me.clip.placeholderapi.PlaceholderAPI;
import net.skullian.skyfactions.common.api.PlayerAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.database.struct.PlayerData;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;
import net.skullian.skyfactions.paper.util.DependencyHandler;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import net.skullian.skyfactions.paper.hooks.ItemJoinHook;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class SpigotPlayerAPI extends PlayerAPI {

    @Override
    public SkyItemStack getPlayerSkull(SkyItemStack builder, UUID playerUUID) {
        if (builder.getMaterial().equals("PLAYER_HEAD")) {
            builder.setOwningPlayerUUID(playerUUID.toString());
        }
        
        return builder;
    }

    @Override
    public String processText(SkyUser user, String text) {
        if (user != null && DependencyHandler.isEnabled("PlaceholderAPI")) return PlaceholderAPI.setPlaceholders(SpigotAdapter.adapt(user), text);
            else return text;
    }

    @Override
    public void clearInventory(SkyUser user) {
        OfflinePlayer offlinePlayer = SpigotAdapter.adapt(user);
        if (!offlinePlayer.isOnline()) return;

        Player player = offlinePlayer.getPlayer();

        ItemStack[] items = player.getInventory().getContents();
        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];

            if (item == null || item.getType().isAir()) {
                continue;
            }

            if (ItemJoinHook.isEnabled() && ItemJoinHook.isCustom(item)) {
                continue;
            }

            player.getInventory().clear(i);
        }
    }

    @Override
    public void clearEnderChest(SkyUser user) {
        OfflinePlayer offlinePlayer = SpigotAdapter.adapt(user);
        if (!offlinePlayer.isOnline()) return;

        Player player = offlinePlayer.getPlayer();
        player.getEnderChest().clear();
    }

    @Override
    public List<SkyUser> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream().map(player -> SkyApi.getInstance().getUserManager().getUser(player.getUniqueId())).toList();
    }

    @Override
    public boolean hasInventorySpace(SkyUser user) {
        return SpigotAdapter.adapt(user).getPlayer().getInventory().firstEmpty() != -1;
    }
}
