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
        new File(plugin.getDataFolder(), "/schematics/raid_saves").mkdirs();
        new File(plugin.getDataFolder(), "/songs").mkdir();

        registerFile(ConfigTypes.SETTINGS, new ConfigHandler(plugin, "config"));
        registerFile(ConfigTypes.MESSAGES, new ConfigHandler(plugin, "messages"));
        registerFile(ConfigTypes.DISCORD, new ConfigHandler(plugin, "discord"));
        registerFile(ConfigTypes.OBELISK, new ConfigHandler(plugin, "obelisk"));
        registerFile(ConfigTypes.RUNES, new ConfigHandler(plugin, "runes"));

        for (GUIEnums enumEntry : GUIEnums.values()) {
            ConfigHandler handler = new ConfigHandler(plugin, enumEntry.getConfigPath());
            handler.saveDefaultConfig();
            GUIEnums.configs.put(enumEntry.getConfigPath(), handler.getConfig());
        }

        configs.values().forEach(ConfigHandler::saveDefaultConfig);
        Messages.setConfig(getFile(ConfigTypes.MESSAGES).getConfig());
        Settings.setConfig(getFile(ConfigTypes.SETTINGS).getConfig());
        ObeliskConfig.setConfig(getFile(ConfigTypes.OBELISK).getConfig());
        Runes.setConfig(getFile(ConfigTypes.RUNES).getConfig());

        MESSAGES_CONFIG = getFile(ConfigTypes.MESSAGES).getConfig();
        DISCORD_CONFIG = getFile(ConfigTypes.DISCORD).getConfig();
    }

    public ConfigHandler getFile(ConfigTypes type) { return configs.get(type); }

    public void reloadFiles() {
        configs.values().forEach(ConfigHandler::reload);
        Messages.setConfig(getFile(ConfigTypes.MESSAGES).getConfig());
        Settings.setConfig(getFile(ConfigTypes.SETTINGS).getConfig());
        ObeliskConfig.setConfig(getFile(ConfigTypes.OBELISK).getConfig());
        Runes.setConfig(getFile(ConfigTypes.RUNES).getConfig());

        for (GUIEnums enumEntry : GUIEnums.values()) {
            ConfigHandler handler = new ConfigHandler(SkyFactionsReborn.getInstance(), enumEntry.getConfigPath());
            handler.saveDefaultConfig();
            GUIEnums.configs.put(enumEntry.getConfigPath(), handler.getConfig());
        }

        MESSAGES_CONFIG = getFile(ConfigTypes.MESSAGES).getConfig();
        DISCORD_CONFIG = getFile(ConfigTypes.DISCORD).getConfig();
    }

    public void registerFile(ConfigTypes type, ConfigHandler handler) { configs.put(type, handler); }

    public FileConfiguration getFileConfig(File file) { return YamlConfiguration.loadConfiguration(file); }
}
