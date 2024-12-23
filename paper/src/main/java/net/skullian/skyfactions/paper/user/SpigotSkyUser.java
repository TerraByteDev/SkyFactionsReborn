package net.skullian.skyfactions.paper.user;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.faction.JoinRequestData;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;
import net.skullian.skyfactions.common.util.SkyLocation;
import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent.Reason;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
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
        return !isConsole() ? SpigotAdapter.adapt(this).getName() : "CONSOLE";
    }

    @Override
    public void teleport(SkyLocation location) {
        Player player = SpigotAdapter.adapt(this).getPlayer();

        if (player != null) Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> player.teleport(SpigotAdapter.adapt(location), PlayerTeleportEvent.TeleportCause.PLUGIN));
    }

    @Nullable
    @Override
    public CompletableFuture<JoinRequestData> getActiveJoinRequest() {
        return this.activeJoinRequest.map(CompletableFuture::completedFuture).orElseGet(() -> SkyApi.getInstance().getDatabaseManager().getFactionInvitesManager().getPlayerJoinRequest(getUniqueId()).whenComplete((request, ex) -> {
            if (ex != null || request == null) return;

            this.activeJoinRequest = Optional.of(request);
        }));

    }

    @Override
    public void sendMessage(Component message) {
        if (isConsole()) {
            ((CommandSender) commandSender).sendMessage(message);
            return;
        }
        Player player = SpigotAdapter.adapt(this).getPlayer();
        if (player != null) player.sendMessage(message);
    }

    @Override
    public void performCommand(String command) {
        Player player = SpigotAdapter.adapt(this).getPlayer();
        if (player != null) player.performCommand(command);
    }

    @Override
    public SkyLocation getLocation() {
        Player player = SpigotAdapter.adapt(this).getPlayer();
        return player != null ? SpigotAdapter.adapt(player.getLocation()) : null;
    }

    @Override
    public void closeInventory() {
        Player player = SpigotAdapter.adapt(this).getPlayer();
        if (player != null) player.closeInventory(Reason.PLUGIN);
    }

    @Override
    public boolean hasMetadata(String key) {
        Player player = SpigotAdapter.adapt(this).getPlayer();
        return player != null && player.hasMetadata(key);
    }

    @Override
    public void addMetadata(String key, Object value) {
        Player player = SpigotAdapter.adapt(this).getPlayer();
        if (player != null) player.setMetadata(key, new FixedMetadataValue(SkyFactionsReborn.getInstance(), value));
    }

    @Override
    public void removeMetadata(String key) {
        Player player = SpigotAdapter.adapt(this).getPlayer();
        if (player != null) player.removeMetadata(key, SkyFactionsReborn.getInstance());
    }

    @Override
    public boolean isOnline() {
        return SpigotAdapter.adapt(this).isOnline();
    }

    @Override
    public boolean hasPermission(String permission) {
        Player player = SpigotAdapter.adapt(this).getPlayer();
        return player != null && player.hasPermission(permission);
    }

    @Override
    public void playSound(Sound sound, float volume, float pitch) {
        Player player = SpigotAdapter.adapt(this).getPlayer();
        if (player != null) player.playSound(sound, Sound.Emitter.self());
    }

    @Override
    public void addItem(SkyItemStack stack) {
        Player player = SpigotAdapter.adapt(this).getPlayer();
        if (player != null) player.getInventory().addItem(SpigotAdapter.adapt(stack, this, false));
    }

    @Override
    public void kick(Component reason) {
        Player player = SpigotAdapter.adapt(this).getPlayer();
        if (player != null) player.kick(reason, PlayerKickEvent.Cause.PLUGIN);
    }
}
