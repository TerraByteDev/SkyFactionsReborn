package net.skullian.skyfactions.config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.config.types.ConfigTypes;
import net.skullian.skyfactions.config.types.DefencesConfig;
import net.skullian.skyfactions.config.types.DiscordConfig;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.ObeliskConfig;
import net.skullian.skyfactions.config.types.Runes;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.defence.DefencesFactory;
import net.skullian.skyfactions.event.DefenceHandler;

public class ConfigFileHandler {

    private Map<ConfigTypes, ConfigHandler> configs;

    public ConfigFileHandler() {
        configs = new HashMap<>();
    }

    public void loadFiles(SkyFactionsReborn plugin) {
        new File(plugin.getDataFolder(), "/schematics").mkdirs();
        new File(plugin.getDataFolder(), "/songs").mkdirs();

        registerFile(ConfigTypes.SETTINGS, new ConfigHandler("config"));
        registerFile(ConfigTypes.MESSAGES, new ConfigHandler("messages"));
        registerFile(ConfigTypes.DISCORD, new ConfigHandler("discord"));
        registerFile(ConfigTypes.OBELISK, new ConfigHandler("obelisk"));
        registerFile(ConfigTypes.RUNES, new ConfigHandler("runes"));
        registerFile(ConfigTypes.DEFENCES, new ConfigHandler("defences"));

        Settings.setConfig(getFile(ConfigTypes.SETTINGS).getConfig());
        DefencesConfig.setConfig(getFile(ConfigTypes.DEFENCES).getConfig());
        DefencesFactory.registerDefaultDefences();
        Messages.load();
        ObeliskConfig.setConfig(getFile(ConfigTypes.OBELISK).getConfig());
        Runes.setConfig(getFile(ConfigTypes.RUNES).getConfig());
        DiscordConfig.setConfig(getFile(ConfigTypes.DISCORD).getConfig());
    }

    public ConfigHandler getFile(ConfigTypes type) {
        return configs.get(type);
    }

    public void reloadFiles() {
        configs.values().forEach(ConfigHandler::reload);

        Settings.setConfig(getFile(ConfigTypes.SETTINGS).getConfig());
        Messages.load();
        ObeliskConfig.setConfig(getFile(ConfigTypes.OBELISK).getConfig());
        Runes.setConfig(getFile(ConfigTypes.RUNES).getConfig());
        DefencesConfig.setConfig(getFile(ConfigTypes.DEFENCES).getConfig());
        DiscordConfig.setConfig(getFile(ConfigTypes.DISCORD).getConfig());

        DefenceHandler.refresh();
    }

    public void registerFile(ConfigTypes type, ConfigHandler handler) {
        configs.put(type, handler);
    }
}
