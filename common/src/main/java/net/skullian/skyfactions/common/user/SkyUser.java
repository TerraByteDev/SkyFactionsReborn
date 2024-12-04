package net.skullian.skyfactions.common.user;

import lombok.Getter;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.common.database.struct.PlayerData;
import net.skullian.skyfactions.common.faction.JoinRequestData;
import net.skullian.skyfactions.common.island.impl.PlayerIsland;
import net.skullian.skyfactions.common.notification.NotificationData;
import net.skullian.skyfactions.common.util.SkyItemStack;
import net.skullian.skyfactions.common.util.SkyLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
public abstract class SkyUser {

    public abstract boolean isConsole();

    public abstract UUID getUniqueId();

    public abstract String getName();

    public abstract void teleport(SkyLocation location);

    public abstract PlayerData getPlayerData();

    public abstract CompletableFuture<Integer> getGems();

    public abstract CompletableFuture<Integer> getRunes();

    @Nullable public abstract CompletableFuture<PlayerIsland> getIsland();

    public abstract CompletableFuture<List<InviteData>> getIncomingInvites();

    public abstract List<InviteData> getCachedInvites();

    public abstract void onCacheComplete(int runesAddition, int gemsAddition);

    @Nullable public abstract CompletableFuture<JoinRequestData> getActiveJoinRequest();

    public abstract void sendMessage(Component message);

    public abstract void performCommand(String command);

    public abstract SkyLocation getLocation();

    public abstract void closeInventory();

    public abstract boolean hasMetadata(String key);

    public abstract void addMetadata(String key, Object value);

    public abstract void removeMetadata(String key);

    public abstract boolean isOnline();

    public String getWorld() {
        return getLocation().getWorldName();
    }

    public abstract boolean hasPermission(String permission);

    public abstract void playSound(Sound sound, float volume, float pitch);

    public abstract void addItem(SkyItemStack stack);
}
