package net.skullian.torrent.skyfactions.defence;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockBreakAnimation;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DefenceDestructionManager implements PacketListener {

    // I only separated these because the break system is far more complicated.

    private static final HashMap<UUID, Long> nextPhase = new HashMap<>();
    private static final HashMap<Location, Integer> blockStages = new HashMap<>();

    public long getNextPhase(Player player) {
        return nextPhase.get(player);
    }

    public boolean updatePhaseCooldown(Player player) {
        List<UUID> toRemove = new ArrayList<>();
        nextPhase.forEach((uuid, phase) -> {
            if (phase <= System.currentTimeMillis()) {
                toRemove.add(uuid);
            }
        });
        toRemove.forEach(nextPhase::remove);
        if (nextPhase.containsKey(player.getUniqueId())) return false;
        nextPhase(player);
        return true;
    }

    public void nextPhase(Player player) {
        nextPhase.put(player.getUniqueId(), System.currentTimeMillis() + 400);
    }

    public boolean updateAndNextPhase(Player player) {
        if (updatePhaseCooldown(player)) {
            nextPhase(player);
            return true;
        }
        return false;
    }

    public void sendBlockDamage(Player player, Location location) {
        int locationId = location.getBlockX() + location.getBlockY() + location.getBlockZ();
        WrapperPlayServerBlockBreakAnimation packet = new WrapperPlayServerBlockBreakAnimation(
                locationId, new Vector3i(location.getBlockX(), location.getBlockY(), location.getBlockZ()), (byte) getBlockStage(location)
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }

    public int getBlockStage(Location loc) {
        return blockStages.getOrDefault(loc, 0);
    }

    public void setBlockStage(Location loc, int stage) {
        blockStages.remove(loc);
        blockStages.put(loc, stage);
    }

    public void removeBlockStage(Location loc) {
        blockStages.remove(loc);
    }

}
