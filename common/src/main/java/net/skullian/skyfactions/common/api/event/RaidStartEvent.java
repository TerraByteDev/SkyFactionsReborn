package net.skullian.skyfactions.common.api.event;

import lombok.Getter;
import net.skullian.skyfactions.common.island.SkyIsland;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RaidStartEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @Getter private final Player attacker;
    @Getter private final Player defender;
    @Getter private final SkyIsland defenderIsland;
    private boolean isCancelled;

    public RaidStartEvent(Player attacker, Player defender, SkyIsland defenderIsland) {
        this.attacker = attacker;
        this.defender = defender;
        this.defenderIsland = defenderIsland;

        Bukkit.getServer().getPluginManager().callEvent(this);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }
}
