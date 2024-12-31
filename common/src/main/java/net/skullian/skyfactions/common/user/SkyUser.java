package net.skullian.skyfactions.common.user;

import lombok.Getter;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.common.database.struct.PlayerData;
import net.skullian.skyfactions.common.faction.JoinRequestData;
import net.skullian.skyfactions.common.island.impl.PlayerIsland;
import net.skullian.skyfactions.common.util.SkyItemStack;
import net.skullian.skyfactions.common.util.SkyLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public abstract class SkyUser {

    public final UUID uuid;
    public final Optional<PlayerData> data = Optional.empty();
    public OptionalInt gems = OptionalInt.empty();
    public OptionalInt runes = OptionalInt.empty();
    public List<InviteData> incomingInvites;
    public Optional<JoinRequestData> activeJoinRequest = Optional.empty();
    @Getter
    public final boolean console;
    @Getter
    public Object commandSender;

    public SkyUser(UUID uuid, boolean console, Object commandSender) {
        this.uuid = uuid;
        this.console = console;
        this.commandSender = commandSender;
    }

    public UUID getUniqueId() {
        return this.uuid;
    }

    public abstract String getName();

    public abstract void teleport(SkyLocation location);

    public PlayerData getPlayerData() {
        return this.data.orElse(null);
    }

    public CompletableFuture<Integer> getGems() {
        if (this.gems.isEmpty()) return SkyApi.getInstance().getDatabaseManager().getCurrencyManager().getGems(this.uuid).whenComplete((gems, ex) -> {
            if (ex != null) return;
            this.gems = OptionalInt.of((this.gems.orElse(0)) + gems);
        });

        if (SkyApi.getInstance().getCacheService().getPlayersToCache().containsKey(this.uuid) && this.gems.isPresent()) return CompletableFuture.completedFuture((this.gems.getAsInt() + SkyApi.getInstance().getCacheService().getEntry(this.uuid).getGems()));
        else return CompletableFuture.completedFuture(this.gems.getAsInt());
    }

    public void addGems(int amount) {
        SkyApi.getInstance().getCacheService().getEntry(this.uuid).addGems(amount);
    }

    public void removeGems(int amount) {
        SkyApi.getInstance().getCacheService().getEntry(this.uuid).removeGems(amount);
    }

    public CompletableFuture<Integer> getRunes() {
        if (this.runes.isEmpty()) return SkyApi.getInstance().getDatabaseManager().getCurrencyManager().getRunes(this.uuid).whenComplete((runes, ex) -> {
            if (ex != null) return;
            this.runes = OptionalInt.of((this.runes.orElse(0)) + runes);
        });

        if (SkyApi.getInstance().getCacheService().getPlayersToCache().containsKey(this.uuid) && this.runes.isPresent()) return CompletableFuture.completedFuture((this.runes.getAsInt() + SkyApi.getInstance().getCacheService().getEntry(this.uuid).getRunes()));
        else return CompletableFuture.completedFuture(this.runes.getAsInt());
    }

    public void addRunes(int amount) {
        SkyApi.getInstance().getCacheService().getEntry(this.uuid).addRunes(amount);
    }

    public void removeRunes(int amount) {
        SkyApi.getInstance().getCacheService().getEntry(this.uuid).removeRunes(amount);
    }

    @Nullable
    public CompletableFuture<PlayerIsland> getIsland() {
        return SkyApi.getInstance().getIslandAPI().getPlayerIsland(uuid);
    }

    public CompletableFuture<List<InviteData>> getIncomingInvites() {
        return this.incomingInvites != null ? CompletableFuture.completedFuture(this.incomingInvites) : SkyApi.getInstance().getDatabaseManager().getFactionInvitesManager().getInvitesOfPlayer(getUniqueId()).whenComplete((invites, ex) -> {
            if (ex != null) return;

            this.incomingInvites = invites;
        });
    }

    @Nullable
    public List<InviteData> getCachedInvites() {
        return this.incomingInvites;
    }

    public void onCacheComplete(int runesAddition, int gemsAddition) {
        this.runes = OptionalInt.of(this.runes.orElse(runesAddition));
        this.gems = OptionalInt.of(this.gems.orElse(gemsAddition));
    }

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

    public SkyUser setCommandSender(Object commandSender) {
        this.commandSender = commandSender;
        return this;
    }

    public abstract void kick(Component reason);
}
