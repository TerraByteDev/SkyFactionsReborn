package net.skullian.skyfactions.common.config;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import lombok.Getter;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.util.SLogger;

import java.io.File;
import java.io.IOException;

public class ConfigHandler {

    private final String name;
    private final File file;

    @Getter
    private YamlDocument config;

    public ConfigHandler(String name) {
        this.name = name + ".yml";
        this.file = new File(SkyApi.getInstance().getFileAPI().getConfigFolderPath(), this.name);

        try {
            this.config = YamlDocument.create(this.file, ConfigHandler.class.getClassLoader().getResourceAsStream(this.name),
                    GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("CONFIG_VERSION")).build());
        } catch (IOException error) {
            SLogger.setup("----------------- CONFIGURATION EXCEPTION -----------------", true);
            SLogger.setup("There was an error loading config {}", true, name);
            SLogger.setup("Please check that config for any configuration mistakes.", true);
            SLogger.setup("Plugin will now disable.", false);
            SLogger.setup("----------------- CONFIGURATION EXCEPTION -----------------", true);
            error.printStackTrace();
            SkyApi.disablePlugin();
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

    public void reload() {
        try {
            config.reload();
        } catch (IOException error) {
            SLogger.fatal("----------------------- CONFIGURATION EXCEPTION -----------------------");
            SLogger.fatal("There was an error reloading config {}", name);
            SLogger.fatal("Please see the below error.");
            SLogger.fatal("----------------------- CONFIGURATION EXCEPTION -----------------------");
            error.printStackTrace();
        }
    }

}
