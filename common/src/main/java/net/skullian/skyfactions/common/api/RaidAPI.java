package net.skullian.skyfactions.common.api;

import lombok.Getter;
import net.skullian.skyfactions.common.database.struct.IslandRaidData;
import net.skullian.skyfactions.common.user.SkyUser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
public abstract class RaidAPI {

    // attacker, victim
    public Map<UUID, UUID> processingRaid = new HashMap<>();
    public Map<UUID, UUID> currentRaids = new HashMap<>();

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
