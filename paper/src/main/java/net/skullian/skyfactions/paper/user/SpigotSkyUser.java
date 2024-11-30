package net.skullian.skyfactions.paper.user;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.skullian.skyfactions.common.api.InvitesAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.common.database.struct.PlayerData;
import net.skullian.skyfactions.common.faction.JoinRequestData;
import net.skullian.skyfactions.common.island.impl.PlayerIsland;
import net.skullian.skyfactions.common.notification.NotificationData;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyLocation;
import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SpigotSkyUser extends SkyUser {

    private UUID uuid;
    private Optional<PlayerData> data = Optional.empty();
    private Optional<Integer> gems = Optional.empty();
    private Optional<Integer> runes = Optional.empty();
    private Optional<PlayerIsland> island = Optional.empty();
    private Optional<List<NotificationData>> notifications = Optional.empty();
    private Optional<List<InviteData>> incomingInvites = Optional.empty();
    private Optional<JoinRequestData> activeJoinRequest = Optional.empty();

    public SpigotSkyUser(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public String getName() {
        return SpigotAdapter.adapt(this).getName();
    }

    @Override
    public void playSound(Sound sound, float pitch, float volume) {
        OfflinePlayer player = SpigotAdapter.adapt(this);
        if (player.isOnline()) player.getPlayer().playSound(player.getPlayer().getLocation(), sound, Sound.Source.MASTER, volume, pitch);
    }

    @Override
    public void teleport(SkyLocation location) {
        OfflinePlayer player = SpigotAdapter.adapt(this);
        if (!player.isOnline()) return;

        Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> {
            player.getPlayer().teleport(SpigotAdapter.adapt(location));
        });
    }

    @Override
    public PlayerData getPlayerData() {
        return this.data.get();
    }

    @Override
    public CompletableFuture<Integer> getGems() {
        if (this.gems.isEmpty()) return SkyApi.getInstance().getDatabaseManager().getCurrencyManager().getGems(this.uuid).whenComplete((gems, ex) -> {
            if (ex != null) return;
            this.gems = Optional.of(this.gems.get() + gems);
        });

        if (SkyApi.getInstance().getCacheService().getPlayersToCache().containsKey(this.uuid) && this.gems.isPresent()) return CompletableFuture.completedFuture((this.gems.get() + SkyApi.getInstance().getCacheService().getEntry(this.uuid).getGems()));
            else return CompletableFuture.completedFuture(this.gems.get());
    }

    @Override
    public CompletableFuture<Integer> getRunes() {
        if (this.runes.isEmpty()) return SkyApi.getInstance().getDatabaseManager().getCurrencyManager().getRunes(this.uuid).whenComplete((runes, ex) -> {
            if (ex != null) return;
            this.runes = Optional.of(this.runes.get() + runes);
        });

        if (SkyApi.getInstance().getCacheService().getPlayersToCache().containsKey(this.uuid) && this.runes.isPresent()) return CompletableFuture.completedFuture((this.runes.get() + SkyApi.getInstance().getCacheService().getEntry(this.uuid).getRunes()));
            else return CompletableFuture.completedFuture(this.runes.get());
    }

    @Nullable
    @Override
    public CompletableFuture<PlayerIsland> getIsland() {
        return this.island.map(CompletableFuture::completedFuture).orElseGet(() -> SkyApi.getInstance().getDatabaseManager().getPlayerIslandManager().getPlayerIsland(this.uuid).whenComplete((island, ex) -> {
            if (ex != null) return;

            this.island = Optional.of(island);
        }));

    }

    @Override
    public List<NotificationData> getNotifications() {
        return this.notifications.orElseGet(() -> SkyApi.getInstance().getNotificationAPI().getNotifications(uuid));
    }

    @Override
    public CompletableFuture<List<InviteData>> getIncomingInvites() {
        return InvitesAPI.getPlayerIncomingInvites(this.uuid).whenComplete((invites, ex) -> {
            if (ex != null) return;

            this.incomingInvites = Optional.of(invites);
        });
    }

    @Override
    public List<InviteData> getCachedInvites() {
        return this.incomingInvites.get();
    }

    @Override
    public void onCacheComplete(int runesAddition, int gemsAddition) {
        this.runes = Optional.of(this.runes.get() + runesAddition);
        this.gems = Optional.of(this.gems.get() + gemsAddition);
    }

    @Nullable
    @Override
    public CompletableFuture<JoinRequestData> getActiveJoinRequest() {
        return InvitesAPI.getPlayerJoinRequest(this.uuid).whenComplete((request, ex) -> {
            if (ex != null) return;

            this.activeJoinRequest = Optional.of(request);
        });
    }

    @Override
    public void sendMessage(Component message) {
        OfflinePlayer player = SpigotAdapter.adapt(this);
        if (player.isOnline()) player.getPlayer().sendMessage(message);
    }
}
