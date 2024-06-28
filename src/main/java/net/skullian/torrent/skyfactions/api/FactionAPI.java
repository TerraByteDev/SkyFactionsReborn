package net.skullian.torrent.skyfactions.api;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.config.Settings;
import net.skullian.torrent.skyfactions.island.FactionIsland;
import net.skullian.torrent.skyfactions.island.PlayerIsland;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FactionAPI {

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

    private static void createRegion(Player player, FactionIsland island, World world) {
        Location corner1 = island.getPosition1(null);
        Location corner2 = island.getPosition2(null);

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(world));
        BlockVector3 min = BlockVector3.at(corner1.getBlockX(), -64, corner1.getBlockZ());
        BlockVector3 max = BlockVector3.at(corner2.getBlockX(), 319, corner2.getBlockZ());
        ProtectedRegion region = new ProtectedCuboidRegion(player.getUniqueId().toString(), min, max);

        DefaultDomain owners = region.getOwners();
        owners.addPlayer(player.getUniqueId());

        region.setOwners(owners);
        regionManager.addRegion(region);
    }
}
