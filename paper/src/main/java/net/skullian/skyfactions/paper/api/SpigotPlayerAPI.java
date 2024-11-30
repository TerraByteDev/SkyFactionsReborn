package net.skullian.skyfactions.paper.api;

import me.clip.placeholderapi.PlaceholderAPI;
import net.skullian.skyfactions.common.api.PlayerAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.database.struct.PlayerData;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.paper.util.DependencyHandler;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import net.skullian.skyfactions.paper.hooks.ItemJoinHook;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class SpigotPlayerAPI extends PlayerAPI {

    // Player Data //

    public static final Map<UUID, PlayerData> playerData = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<Boolean> isPlayerRegistered(UUID uuid) {
        if (playerData.containsKey(uuid)) return CompletableFuture.completedFuture(true);

        return getPlayerData(uuid).handle((data, ex) -> data != null);
    }

    @Override
    public void cacheData(UUID playerUUID) {
        SkyUser user = SkyApi.getInstance().getUserManager().getUser(playerUUID);
        user.getGems();
        user.getRunes();
        user.getIncomingInvites();
        user.getActiveJoinRequest();
    }

    @Override
    public boolean isPlayerCached(UUID uuid) {
        return playerData.containsKey(uuid);
    }

    @Override
    public CompletableFuture<PlayerData> getPlayerData(UUID uuid) {
        if (playerData.containsKey(uuid)) return CompletableFuture.completedFuture(playerData.get(uuid));

        return SkyApi.getInstance().getDatabaseManager().getPlayerManager().getPlayerData(uuid).handle((data, ex) -> {
            if (data != null) {
                playerData.put(uuid, data);
            }
            return data;
        });
    }

    @NotNull
    @Override
    public PlayerData getCachedPlayerData(UUID uuid) {
        return playerData.get(uuid);
    }

    @Override
    public String getLocale(UUID uuid) {
        return playerData.getOrDefault(uuid, getDefaultPlayerData()).getLOCALE();
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
}
