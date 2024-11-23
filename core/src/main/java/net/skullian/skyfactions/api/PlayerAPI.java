package net.skullian.skyfactions.api;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.database.struct.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerAPI {

    // Player Data //

    public static final Map<UUID, PlayerData> playerData = new ConcurrentHashMap<>();

    public static CompletableFuture<Boolean> isPlayerRegistered(UUID uuid) {
        if (playerData.containsKey(uuid)) return CompletableFuture.completedFuture(true);

        return getPlayerData(uuid).handle((data, ex) -> data != null);
    }

    // for PlaceholderAPI
    public static void cacheData(Player player) {
        if (!GemsAPI.playerGems.containsKey(player.getUniqueId())) GemsAPI.cachePlayer(player.getUniqueId());
        if (!RunesAPI.playerRunes.containsKey(player.getUniqueId())) RunesAPI.cachePlayer(player.getUniqueId());
        if (!FactionAPI.factionCache.containsKey(player.getUniqueId())) FactionAPI.getFaction(player.getUniqueId());
        if (!InvitesAPI.playerIncomingInvites.containsKey(player.getUniqueId()) || !InvitesAPI.playerJoinRequests.containsKey(player.getUniqueId())) InvitesAPI.cache(player.getUniqueId());
    }

    public static CompletableFuture<PlayerData> getPlayerData(UUID uuid) {
        if (playerData.containsKey(uuid)) return CompletableFuture.completedFuture(playerData.get(uuid));

        return SkyFactionsReborn.getDatabaseManager().getPlayerManager().getPlayerData(uuid).handle((data, ex) -> {
            if (data != null) {
                playerData.put(uuid, data);
            }
            return data;
        });
    }

    public static String getLocale(UUID uuid) {
        return playerData.getOrDefault(uuid, getDefaultPlayerData()).getLOCALE();
    }

    private static PlayerData getDefaultPlayerData() {
        return new PlayerData(null, null, 0, Messages.getDefaulLocale());
    }

    // Skulls //

    private static Method metaSetProfileMethod;
    private static Field metaProfileField;

    public static ItemStack convertToSkull(ItemStack item, String skullValue) {
        if (item.getType() == Material.PLAYER_HEAD) {
            item.editMeta(SkullMeta.class, skullMeta -> {
                final UUID uuid = UUID.randomUUID();
                final PlayerProfile playerProfile = Bukkit.createProfile(uuid, uuid.toString().substring(0, 16));
                playerProfile.setProperty(new ProfileProperty("textures", skullValue));

                skullMeta.setPlayerProfile(playerProfile);
            });
        }

        return item;
    }

    public static ItemStack getPlayerSkull(ItemStack item, UUID playerUUID) {
        if (item.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(playerUUID));
            item.setItemMeta(meta);
        }

        return item;
    }
}
