package net.skullian.skyfactions.paper.user;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.skullian.skyfactions.common.api.InvitesAPI;
import net.skullian.skyfactions.common.faction.JoinRequestData;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;
import net.skullian.skyfactions.common.util.SkyLocation;
import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryCloseEvent.Reason;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SpigotSkyUser extends SkyUser {

    public SpigotSkyUser(UUID uuid, boolean console, Object commandSender) {
        super(uuid, console, commandSender);
    }

    @Override
    public String getName() {
        return SpigotAdapter.adapt(this).getName();
    }

    @Override
    public void teleport(SkyLocation location) {
        OfflinePlayer player = SpigotAdapter.adapt(this);
        if (!player.isOnline()) return;

        Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> player.getPlayer().teleport(SpigotAdapter.adapt(location)));
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

    @Override
    public void performCommand(String command) {
        OfflinePlayer player = SpigotAdapter.adapt(this);
        if (player.isOnline()) player.getPlayer().performCommand(command);
    }

    @Override
    public SkyLocation getLocation() {
        OfflinePlayer player = SpigotAdapter.adapt(this);
        return player.isOnline() ? SpigotAdapter.adapt(player.getPlayer().getLocation()) : null;
    }

    @Override
    public void closeInventory() {
        OfflinePlayer player = SpigotAdapter.adapt(this);
        if (player.isOnline()) player.getPlayer().closeInventory(Reason.PLUGIN);
    }

    @Override
    public boolean hasMetadata(String key) {
        OfflinePlayer player = SpigotAdapter.adapt(this);
        return player.isOnline() && player.getPlayer().hasMetadata(key);
    }

    @Override
    public void addMetadata(String key, Object value) {
        OfflinePlayer player = SpigotAdapter.adapt(this);
        if (player.isOnline()) player.getPlayer().setMetadata(key, new FixedMetadataValue(SkyFactionsReborn.getInstance(), value));
    }

    @Override
    public void removeMetadata(String key) {
        OfflinePlayer player = SpigotAdapter.adapt(this);
        if (player.isOnline()) player.getPlayer().removeMetadata(key, SkyFactionsReborn.getInstance());
    }

    @Override
    public boolean isOnline() {
        return SpigotAdapter.adapt(this).isOnline();
    }

    @Override
    public boolean hasPermission(String permission) {
        OfflinePlayer player = SpigotAdapter.adapt(this);
        return player.isOnline() && player.getPlayer().hasPermission(permission);
    }

    @Override
    public void playSound(Sound sound, float volume, float pitch) {
        OfflinePlayer player = SpigotAdapter.adapt(this);
        if (player.isOnline()) player.getPlayer().playSound(sound, Sound.Emitter.self());
    }

    @Override
    public void addItem(SkyItemStack stack) {
        OfflinePlayer player = SpigotAdapter.adapt(this);
        if (player.isOnline()) player.getPlayer().getInventory().addItem(SpigotAdapter.adapt(stack, this, false));
    }

    @Override
    public void kick(Component reason) {
        OfflinePlayer player = SpigotAdapter.adapt(this);
        if (player.isOnline()) player.getPlayer().kick(reason, PlayerKickEvent.Cause.PLUGIN);
    }
}
