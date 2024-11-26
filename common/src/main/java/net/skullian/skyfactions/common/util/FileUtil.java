package net.skullian.skyfactions.common.util;

import net.skullian.skyfactions.core.SkyFactionsReborn;

import java.io.File;


public class FileUtil {

    public static File getSchematicFile(String name) {
        File retrievedSchematic = new File(SkyFactionsReborn.getInstance().getDataFolder() + "/schematics", name);
        if (retrievedSchematic.exists() && !retrievedSchematic.isDirectory()) {
            SLogger.info("Successfully retrieved island schematic [{}].", name);
            return retrievedSchematic;
        }
        SLogger.fatal("Failed to retrieve island schematic [{}]. Is the filename correct?", name);
        return null;
    }
}
