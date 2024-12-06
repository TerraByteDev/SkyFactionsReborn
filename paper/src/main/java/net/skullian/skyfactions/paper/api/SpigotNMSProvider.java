package net.skullian.skyfactions.paper.api;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.common.util.nms.NMSProvider;
import net.skullian.skyfactions.common.util.worldborder.AWorldBorder;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpigotNMSProvider extends NMSProvider {

    @Override
    public AWorldBorder getBorderFromPlayer(SkyUser player) {
        if (!player.isOnline()) throw new IllegalStateException("Attempted to fetch an offline player's border! Player: " + player.getName());
        Player bukkitPlayer = SpigotAdapter.adapt(player).getPlayer();

        try {
            Class<?> clazz = Class.forName("net.skullian.skyfactions.nms." + getNMS_VERSION() + ".WorldBorder");
            return (AWorldBorder) clazz.getDeclaredConstructor(Player.class).newInstance(bukkitPlayer);
        } catch (Throwable error) {
            SLogger.fatal("Failed to fetch {}'s WorldBorder!", true, player.getName());
            error.printStackTrace();
        }

        return null;
    }

    @Override
    public AWorldBorder getBorderFromWorld(String worldName) {
        if (!SkyApi.getInstance().getRegionAPI().worldExists(worldName)) throw new IllegalArgumentException("Attempted to fetch a non-existent world's border! World: " + worldName);
        World world = Bukkit.getWorld(worldName);

        try {
            Class<?> clazz = Class.forName("net.skullian.skyfactions.nms." + getNMS_VERSION() + ".WorldBorder");
            return (AWorldBorder) clazz.getDeclaredConstructor(World.class).newInstance(world);
        } catch (Throwable error) {
            error.printStackTrace();
        }

        return null;
    }

    @Override
    public String fetchNMSVersion() {
        String internalVer = Bukkit.getVersion();
        Matcher matcher = Pattern.compile("(?i)\\(MC: (\\d)\\.(\\d++)\\.?(\\d++)?(?: (Pre-Release|Release Candidate) )?(\\d)?\\)").matcher(internalVer);

        if (matcher.find()) {
            MatchResult result = matcher.toMatchResult();
            String ver = result.group(2);
            String minorVer = result.group(3);
            String PACKAGE_PATTERN = "v1_%s_R%s";
            setNMS_VERSION(PACKAGE_PATTERN.formatted(ver, minorVer));

            return getNMS_VERSION();
        } else {
            SLogger.setup("Failed to initialise NMS Provider", true);
            new UnsupportedOperationException("This server version is unsupported!").printStackTrace();
        }

        return null;
    }
}
