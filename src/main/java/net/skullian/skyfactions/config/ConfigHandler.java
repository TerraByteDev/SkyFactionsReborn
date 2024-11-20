package net.skullian.skyfactions.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import lombok.Getter;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.util.SLogger;


public class ConfigHandler {

    private final String name;
    private final File file;

    @Getter
    private YamlDocument config;

    public ConfigHandler(String name) {
        this.name = name + ".yml";
        this.file = new File(SkyFactionsReborn.getInstance().getDataFolder(), this.name);

        try {
            this.config = YamlDocument.create(this.file, SkyFactionsReborn.getInstance().getResource(this.name),
                    GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("CONFIG_VERSION")).build());
        } catch (IOException error) {
            SLogger.fatal("----------------------- CONFIGURATION EXCEPTION -----------------------");
            SLogger.fatal("There was an error loading config {}", name);
            SLogger.fatal("Please check that config for any configuration mistakes.");
            SLogger.fatal("Plugin will now disable.");
            SLogger.fatal("----------------------- CONFIGURATION EXCEPTION -----------------------");
            error.printStackTrace();
            SkyFactionsReborn.getInstance().disable();
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
