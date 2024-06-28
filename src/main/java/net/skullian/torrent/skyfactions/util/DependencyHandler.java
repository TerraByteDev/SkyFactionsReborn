package net.skullian.torrent.skyfactions.util;

import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import lombok.extern.log4j.Log4j2;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.api.IslandAPI;
import net.skullian.torrent.skyfactions.papi.PlaceholderManager;
import org.bukkit.plugin.RegisteredServiceProvider;

@Log4j2(topic = "SkyFactionsReborn")
public class DependencyHandler {

    public static boolean jukebox = false;
    public static boolean papi = false;
    public static boolean nbapi = false;

    public static void init() {
        if (isPluginEnabled("PlaceholderAPI")) {
            LOGGER.info("Syncing with PAPI.");
            new PlaceholderManager(SkyFactionsReborn.getInstance()).register();
            papi = true;
        } else {
            alert("PlaceholderAPI");
        }

        if (isPluginEnabled("JukeBox")) {
            LOGGER.info("Found JukeBox installed on the server.");
            jukebox = true;
        }

        if (isPluginEnabled("NoteBlockAPI")) {
            LOGGER.info("Found NoteBlockAPI installed on the server.");
            nbapi = true;
        }

        RegisteredServiceProvider<WorldBorderApi> worldBorderApiRegisteredServiceProvider = SkyFactionsReborn.getInstance().getServer().getServicesManager().getRegistration(WorldBorderApi.class);
        if (worldBorderApiRegisteredServiceProvider == null) {
            throw new RuntimeException("Failed to find WorldBorderAPI!");
        } else {
            IslandAPI.worldBorderApi = worldBorderApiRegisteredServiceProvider.getProvider();
        }
    }

    private static boolean isPluginEnabled(String name) {
        return SkyFactionsReborn.getInstance().getServer().getPluginManager().isPluginEnabled(name);
    }

    private static void alert(String name) {
        LOGGER.warn("Could not find {} on the server!", name);
        LOGGER.warn("If you have {} installed, this is a bug!", name);
    }
}
