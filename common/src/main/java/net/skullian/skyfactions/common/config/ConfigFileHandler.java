package net.skullian.skyfactions.common.config;

import net.skullian.skyfactions.common.config.types.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigFileHandler {

    private Map<ConfigTypes, ConfigHandler> configs = new HashMap<>();

    public void loadFiles(JavaPlugin plugin) {
        new File(plugin.getDataFolder(), "/schematics").mkdirs();
        new File(plugin.getDataFolder(), "/songs").mkdirs();

        registerFile(ConfigTypes.SETTINGS, new ConfigHandler("config", plugin));
        registerFile(ConfigTypes.OBELISK, new ConfigHandler("obelisk", plugin));
        registerFile(ConfigTypes.RUNES, new ConfigHandler("runes", plugin));
        registerFile(ConfigTypes.DEFENCES, new ConfigHandler("defences", plugin));

        Settings.setConfig(getFile(ConfigTypes.SETTINGS).getConfig());
        DefencesConfig.setConfig(getFile(ConfigTypes.DEFENCES).getConfig());
        Messages.load(plugin);
        ObeliskConfig.setConfig(getFile(ConfigTypes.OBELISK).getConfig());
        RunesConfig.setConfig(getFile(ConfigTypes.RUNES).getConfig());
    }

    public ConfigHandler getFile(ConfigTypes type) {
        return configs.get(type);
    }

    public void reloadFiles(JavaPlugin plugin) {
        configs.values().forEach(ConfigHandler::reload);

        Settings.setConfig(getFile(ConfigTypes.SETTINGS).getConfig());
        Messages.load(plugin);
        ObeliskConfig.setConfig(getFile(ConfigTypes.OBELISK).getConfig());
        RunesConfig.setConfig(getFile(ConfigTypes.RUNES).getConfig());
        DefencesConfig.setConfig(getFile(ConfigTypes.DEFENCES).getConfig());
    }

    public void registerFile(ConfigTypes type, ConfigHandler handler) {
        configs.put(type, handler);
    }
}
