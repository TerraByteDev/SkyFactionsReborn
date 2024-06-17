package net.skullian.torrent.skyfactions.config;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

@Log4j2(topic = "SkyFactionsReborn")
public class ConfigHandler {

    private final JavaPlugin plugin;
    private final String name;
    private final File file;

    @Getter
    private FileConfiguration config;

    public ConfigHandler(JavaPlugin plugin, String name) {
        this.plugin = plugin;
        this.name = name + ".yml";
        this.file = new File(plugin.getDataFolder(), this.name);
        this.config = new YamlConfiguration();

        new File(plugin.getDataFolder(), "/schematics/raid_saves").mkdirs();
        new File(plugin.getDataFolder(), "/songs").mkdir();
    }

    public void saveDefaultConfig() {
        if (!file.exists()) {
            plugin.saveResource(name, false);
        }

        try {
            config.load(file);
        } catch (InvalidConfigurationException | IOException error) {
            LOGGER.fatal("----------------------- CONFIGURATION EXCEPTION -----------------------");
            LOGGER.fatal("There was an error loading config " + name);
            LOGGER.fatal("Please check that config for any configuration mistakes.");
            LOGGER.fatal("Plugin will now disable.");
            LOGGER.fatal("----------------------- CONFIGURATION EXCEPTION -----------------------");
            error.printStackTrace();
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    public void save() {
        if (config == null || file == null) return;
        try {
            getConfig().save(file);
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    public void reload() { config = YamlConfiguration.loadConfiguration(file); }

}
