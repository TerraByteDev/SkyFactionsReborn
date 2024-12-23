package net.skullian.skyfactions.common.config;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigFileHandler {

    private Map<ConfigTypes, ConfigHandler> configs = new HashMap<>();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void loadFiles() {
        String configPath = SkyApi.getInstance().getFileAPI().getConfigFolderPath();
        new File(configPath, "/schematics").mkdirs();
        new File(configPath, "/songs").mkdirs();

        registerFile(ConfigTypes.SETTINGS, new ConfigHandler("config"));
        registerFile(ConfigTypes.OBELISK, new ConfigHandler("obelisk"));
        registerFile(ConfigTypes.RUNES, new ConfigHandler("runes"));
        registerFile(ConfigTypes.DEFENCES, new ConfigHandler("defences"));

        Settings.setConfig(getFile(ConfigTypes.SETTINGS).getConfig());
        DefencesConfig.setConfig(getFile(ConfigTypes.DEFENCES).getConfig());
        Messages.load(true);
        ObeliskConfig.setConfig(getFile(ConfigTypes.OBELISK).getConfig());
        RunesConfig.setConfig(getFile(ConfigTypes.RUNES).getConfig());
    }

    public ConfigHandler getFile(ConfigTypes type) {
        return configs.get(type);
    }

    public void reloadFiles() {
        configs.values().forEach(ConfigHandler::reload);

        Settings.setConfig(getFile(ConfigTypes.SETTINGS).getConfig());
        Messages.load(false);
        ObeliskConfig.setConfig(getFile(ConfigTypes.OBELISK).getConfig());
        RunesConfig.setConfig(getFile(ConfigTypes.RUNES).getConfig());
        DefencesConfig.setConfig(getFile(ConfigTypes.DEFENCES).getConfig());

        SkyApi.getInstance().getDefenceAPI().refresh();
    }

    public void registerFile(ConfigTypes type, ConfigHandler handler) {
        configs.put(type, handler);
    }
}
