package net.skullian.torrent.skyfactions.island;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.config.Settings;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
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
import java.util.regex.Pattern;

public class FactionAPI {

    public static boolean hasKingdom(Player player) {
        KingdomPlayer kingdomPlayer = KingdomPlayer.getKingdomPlayer(player.getUniqueId());

        return kingdomPlayer.hasKingdom();
    }

    public static boolean hasValidName(Player player, String name) {
        int minimumLength = Settings.FACTION_CREATION_MIN_LENGTH.getInt();
        int maximumLength = Settings.FACTION_CREATION_MAX_LENGTH.getInt();
        int length = name.length();
        if (length >= minimumLength && length <= maximumLength) {
            if (!Settings.FACTION_CREATION_ALLOW_NUMBERS.getBoolean() && TextUtility.containsNumbers(name)) {
                Messages.FACTION_NO_NUMBERS.send(player);
                return false;
            } else if (!Settings.FACTION_CREATION_ALLOW_NON_ENGLISH.getBoolean() && !TextUtility.isEnglish(name)) {
                Messages.FACTION_NON_ENGLISH.send(player);
                return false;
            } else if (!Settings.FACTION_CREATION_ALLOW_SYMBOLS.getBoolean() && TextUtility.hasSymbols(name)) {
                Messages.FACTION_NO_SYMBOLS.send(player);
                return false;
            } else {
                boolean regexMatch = false;
                List<String> blacklistedNames = Settings.FACTION_CREATION_BLACKLISTED_NAMES.getList();

                for (String blacklistedName : blacklistedNames) {
                    if (Pattern.compile(blacklistedName).matcher(name).find()) {
                        regexMatch = true;
                        break;
                    }
                }

                if (regexMatch) {
                    Messages.FACTION_NAME_PROHIBITED.send(player);
                    return false;
                } else {
                    return true;
                }
            }

        } else {
            Messages.FACTION_NAME_LENGTH_LIMIT.send(player, "%min%", minimumLength, "%max%", maximumLength);
            return false;
        }
    }



    private static void createKingdomsRegion(Player player, PlayerIsland island, World world) {

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
