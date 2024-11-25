package net.skullian.skyfactions.common.config;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import lombok.Getter;
import net.skullian.skyfactions.common.util.SLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;


public class ConfigHandler {

    private final String name;
    private final File file;

    @Getter
    private YamlDocument config;

    public ConfigHandler(String name, JavaPlugin plugin) {
        this.name = name + ".yml";
        this.file = new File(plugin.getDataFolder(), this.name);

        try {
            this.config = YamlDocument.create(this.file, plugin.getResource(this.name),
                    GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("CONFIG_VERSION")).build());
        } catch (IOException error) {
            SLogger.fatal("----------------------- CONFIGURATION EXCEPTION -----------------------");
            SLogger.fatal("There was an error loading config {}", name);
            SLogger.fatal("Please check that config for any configuration mistakes.");
            SLogger.fatal("Plugin will now disable.");
            SLogger.fatal("----------------------- CONFIGURATION EXCEPTION -----------------------");
            error.printStackTrace();
            Bukkit.getServer().getPluginManager().disablePlugin(plugin);
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
