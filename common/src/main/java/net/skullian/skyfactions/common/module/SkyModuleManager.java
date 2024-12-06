package net.skullian.skyfactions.common.module;

import lombok.Getter;
import net.skullian.skyfactions.common.util.SLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkyModuleManager {

    private static final List<String> modules = new ArrayList<>();
    @Getter
    private static final Map<String, SkyModule> enabledModules = new HashMap<>();

    public static void registerModule(Class<?> module) {
        modules.add(module.getClass().getName());
    }

    public static void onEnable() {
        for (String module : modules) {
            try {
                Class<?> clazz = Class.forName(module);
                SkyModule skyModule = (SkyModule) clazz.newInstance();
                if (skyModule.shouldEnable()) {
                    skyModule.onEnable();
                }

                enabledModules.put(skyModule.getClass().getName(), skyModule);
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
    
    public static void onReload() {
        for (Map.Entry<String, SkyModule> module : enabledModules.entrySet()) {
            module.getValue().onReload();
        }
    }

    public static void onDisable() {
        for (Map.Entry<String, SkyModule> module : enabledModules.entrySet()) {
            module.getValue().onDisable();
        }
    }
}
