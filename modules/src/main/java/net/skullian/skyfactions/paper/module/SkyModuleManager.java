package net.skullian.skyfactions.paper.module;

import net.skullian.skyfactions.common.util.SLogger;

import java.util.ArrayList;
import java.util.List;

public class SkyModuleManager {

    private final List<String> modules = new ArrayList<>();
    private final List<SkyModule> enabledModules = new ArrayList<>();

    public void registerModule(Class<?> module) {
        modules.add(module.getClass().getName());
    }

    public SkyModuleManager() {

    }

    public void onEnable() {
        for (String module : modules) {
            try {
                Class<?> clazz = Class.forName(module);
                SkyModule skyModule = (SkyModule) clazz.newInstance();
                if (skyModule.shouldEnable()) {
                    skyModule.onEnable();
                }

                enabledModules.add(skyModule);
            } catch (Exception e) {
                SLogger.fatal("----------------------- MODULES EXCEPTION -----------------------");
                SLogger.fatal("There was an error initialising module {}:", module);
                SLogger.fatal(e.getMessage());
                SLogger.fatal("Please forward this error to the developers!");
                SLogger.fatal("----------------------- MODULES EXCEPTION -----------------------");
                e.printStackTrace();
            }
        }
    }

    public void onReload() {
        for (SkyModule module : enabledModules) {
            module.onReload();
        }
    }

    public void onDisable() {
        for (SkyModule module : enabledModules) {
            module.onDisable();
        }
    }
}
