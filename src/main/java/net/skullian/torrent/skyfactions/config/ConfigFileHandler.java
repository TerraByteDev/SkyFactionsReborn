package net.skullian.torrent.skyfactions.config;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.util.FileUtil;
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

        for (GUIEnums gui : GUIEnums.values()) {
            registerFile(ConfigTypes.GUI, new ConfigHandler(plugin, gui.getConfigPath()));
        }

        configs.values().forEach(ConfigHandler::saveDefaultConfig);
        Messages.setConfig(getFile(ConfigTypes.MESSAGES).getConfig());
        Settings.setConfig(getFile(ConfigTypes.SETTINGS).getConfig());
        ObeliskConfig.setConfig(getFile(ConfigTypes.OBELISK).getConfig());

        MESSAGES_CONFIG = getFile(ConfigTypes.MESSAGES).getConfig();
        DISCORD_CONFIG = getFile(ConfigTypes.DISCORD).getConfig();
    }

    public ConfigHandler getFile(ConfigTypes type) { return configs.get(type); }

    public void reloadFiles() {
        configs.values().forEach(ConfigHandler::reload);
        Messages.setConfig(getFile(ConfigTypes.MESSAGES).getConfig());
        Settings.setConfig(getFile(ConfigTypes.SETTINGS).getConfig());
        ObeliskConfig.setConfig(getFile(ConfigTypes.OBELISK).getConfig());

        for (GUIEnums gui : GUIEnums.values()) {
            registerFile(ConfigTypes.GUI, new ConfigHandler(SkyFactionsReborn.getInstance(), gui.getConfigPath()));
        }

        MESSAGES_CONFIG = getFile(ConfigTypes.MESSAGES).getConfig();
        DISCORD_CONFIG = getFile(ConfigTypes.DISCORD).getConfig();
    }

    public void registerFile(ConfigTypes type, ConfigHandler handler) { configs.put(type, handler); }

    public FileConfiguration getFileConfig(File file) { return YamlConfiguration.loadConfiguration(file); }
}
