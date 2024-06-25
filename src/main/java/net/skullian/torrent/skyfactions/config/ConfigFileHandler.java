package net.skullian.torrent.skyfactions.config;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigFileHandler {

    private Map<ConfigTypes, ConfigHandler> configs;
    public ConfigFileHandler() { configs = new HashMap<>(); }

    public FileConfiguration MESSAGES_CONFIG;
    public FileConfiguration DISCORD_CONFIG;

    public void loadFiles(SkyFactionsReborn plugin) {
        registerFile(ConfigTypes.SETTINGS, new ConfigHandler(plugin, "config"));
        registerFile(ConfigTypes.MESSAGES, new ConfigHandler(plugin, "messages"));
        registerFile(ConfigTypes.DISCORD, new ConfigHandler(plugin, "discord"));

        configs.values().forEach(ConfigHandler::saveDefaultConfig);
        Messages.setConfig(getFile(ConfigTypes.MESSAGES).getConfig());
        Settings.setConfig(getFile(ConfigTypes.SETTINGS).getConfig());

        MESSAGES_CONFIG = getFile(ConfigTypes.MESSAGES).getConfig();
        DISCORD_CONFIG = getFile(ConfigTypes.DISCORD).getConfig();
    }

    public ConfigHandler getFile(ConfigTypes type) { return configs.get(type); }

    public void reloadFiles() {
        configs.values().forEach(ConfigHandler::reload);
        Messages.setConfig(getFile(ConfigTypes.MESSAGES).getConfig());
        Settings.setConfig(getFile(ConfigTypes.SETTINGS).getConfig());

        MESSAGES_CONFIG = getFile(ConfigTypes.MESSAGES).getConfig();
        DISCORD_CONFIG = getFile(ConfigTypes.DISCORD).getConfig();
    }

    public void registerFile(ConfigTypes type, ConfigHandler handler) { configs.put(type, handler); }

    public FileConfiguration getFileConfig(File file) { return YamlConfiguration.loadConfiguration(file); }
}
