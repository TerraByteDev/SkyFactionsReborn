package net.skullian.skyfactions.common.api;

import net.skullian.skyfactions.common.database.struct.IslandRaidData;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class RaidAPI {

    public abstract CompletableFuture<String> getCooldownDuration(Player player);

    public abstract void startRaid(Player player);

    public abstract void handlePlayers(Player attacker, UUID uuid);

    public abstract boolean hasEnoughGems(Player player);

    public abstract IslandRaidData getRandomRaidable(Player player);

    public abstract boolean isPlayerOnline(UUID uuid);

    public abstract void alertPlayer(Player player, Player attacker);

    public abstract void teleportToPreparationArea(Player player);

    public abstract CompletableFuture<Void> showCountdown(UUID def, Player att);

    public abstract void handleRaidExecutionError(Player player, boolean isDefendant);

}
