package net.skullian.skyfactions.common.api;

import net.skullian.skyfactions.common.database.struct.IslandRaidData;
import net.skullian.skyfactions.common.user.SkyUser;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class RaidAPI {

    public abstract CompletableFuture<String> getCooldownDuration(SkyUser player);

    public abstract void startRaid(SkyUser player);

    public abstract void handlePlayers(SkyUser attacker, UUID uuid);

    public abstract boolean hasEnoughGems(SkyUser player);

    public abstract IslandRaidData getRandomRaidable(SkyUser player);

    public abstract void alertPlayer(SkyUser player, SkyUser attacker);

    public abstract void teleportToPreparationArea(SkyUser player);

    public abstract CompletableFuture<Void> showCountdown(UUID def, SkyUser att);

    public abstract void handleRaidExecutionError(SkyUser player, boolean isDefendant);

}
