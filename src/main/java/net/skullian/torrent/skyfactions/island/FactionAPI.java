package net.skullian.torrent.skyfactions.island;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.constants.land.location.SimpleChunkLocation;
import org.kingdoms.constants.player.KingdomPlayer;
import org.kingdoms.events.lands.ClaimLandEvent;

import java.util.ArrayList;
import java.util.List;

public class FactionAPI {

    private static void createKingdomsRegion(Player player, SkyIsland island, World world) {

        KingdomPlayer kingdomPlayer = KingdomPlayer.getKingdomPlayer(player.getUniqueId());

        if (kingdomPlayer.hasKingdom()) {
            Kingdom kingdom = kingdomPlayer.getKingdom();

            Location corner1 = island.getPosition1(world);
            Location corner2 = island.getPosition2(world);

            int minChunkX = Math.min(corner1.getBlockX(), corner2.getBlockX());
            int maxChunkX = Math.max(corner1.getBlockX(), corner2.getBlockX());
            int minChunkZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
            int maxChunkZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

            List<SimpleChunkLocation> chunkLocations = new ArrayList<>();

            for (int x = minChunkX; x <= maxChunkX; x++) {
                for (int z = minChunkZ; z <= maxChunkZ; z++) {
                    Chunk chunk = world.getChunkAt(x, z);
                    if (chunk!= null) {
                        SimpleChunkLocation simpleChunkLocation = SimpleChunkLocation.of(chunk);
                        chunkLocations.add(simpleChunkLocation);
                    }
                }
            }

            for (SimpleChunkLocation chunk : chunkLocations) {
                kingdom.claim(chunk, kingdomPlayer, ClaimLandEvent.Reason.CLAIMED);
            }
        }
    }
}
