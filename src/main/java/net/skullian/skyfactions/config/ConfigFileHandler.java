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
import net.skullian.skyfactions.config.types.GUIEnums;
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
        new File(plugin.getDataFolder(), "/songs").mkdir();

        registerFile(ConfigTypes.SETTINGS, new ConfigHandler("config"));
        registerFile(ConfigTypes.MESSAGES, new ConfigHandler("messages"));
        registerFile(ConfigTypes.DISCORD, new ConfigHandler("discord"));
        registerFile(ConfigTypes.OBELISK, new ConfigHandler("obelisk"));
        registerFile(ConfigTypes.RUNES, new ConfigHandler("runes"));
        registerFile(ConfigTypes.DEFENCES, new ConfigHandler("defences"));

        loadGUIs();

        Messages.setConfig(getFile(ConfigTypes.MESSAGES).getConfig());
        Settings.setConfig(getFile(ConfigTypes.SETTINGS).getConfig());
        ObeliskConfig.setConfig(getFile(ConfigTypes.OBELISK).getConfig());
        Runes.setConfig(getFile(ConfigTypes.RUNES).getConfig());
        DefencesConfig.setConfig(getFile(ConfigTypes.DEFENCES).getConfig());
        DiscordConfig.setConfig(getFile(ConfigTypes.DISCORD).getConfig());

        if (!Files.exists(Paths.get(plugin.getDataFolder() + "/defences"))) {
            DefencesFactory.registerDefaultDefences();
        }
        DefencesFactory.register();
    }

    public ConfigHandler getFile(ConfigTypes type) {
        return configs.get(type);
    }

    public void reloadFiles() {
        configs.values().forEach(ConfigHandler::reload);
        Messages.setConfig(getFile(ConfigTypes.MESSAGES).getConfig());
        Settings.setConfig(getFile(ConfigTypes.SETTINGS).getConfig());
        ObeliskConfig.setConfig(getFile(ConfigTypes.OBELISK).getConfig());
        Runes.setConfig(getFile(ConfigTypes.RUNES).getConfig());
        DefencesConfig.setConfig(getFile(ConfigTypes.DEFENCES).getConfig());
        DiscordConfig.setConfig(getFile(ConfigTypes.DISCORD).getConfig());

        loadGUIs();
        DefencesFactory.register();
        DefenceHandler.refresh();
    }

    private void loadGUIs() {
        for (GUIEnums enumEntry : GUIEnums.values()) {
            ConfigHandler handler = new ConfigHandler(enumEntry.getConfigPath());
            GUIEnums.configs.put(enumEntry.getConfigPath(), handler.getConfig());
        }
    }

    public void registerFile(ConfigTypes type, ConfigHandler handler) {
        configs.put(type, handler);
    }
}
