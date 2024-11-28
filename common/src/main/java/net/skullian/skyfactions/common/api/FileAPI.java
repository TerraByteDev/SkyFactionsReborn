package net.skullian.skyfactions.common.api;

import net.skullian.skyfactions.common.util.SLogger;

import java.io.File;

public abstract class FileAPI {

    public abstract String getConfigFolderPath();

    public File getDatabasePath() {
        return new File(getConfigFolderPath(), "/data/data.sqlite3");
    }

    public File getSongsPath() {
        return new File(getConfigFolderPath(), "/songs/");
    }

    public File getSchematicFile(String name) {
        File retrievedSchematic = new File(getConfigFolderPath() + "/schematics", name);
        if (retrievedSchematic.exists() && !retrievedSchematic.isDirectory()) {
            SLogger.info("Successfully retrieved island schematic [{}].", name);
            return retrievedSchematic;
        }

        SLogger.fatal("Failed to retrieve island schematic [{}]. Is the filename correct?", name);
        return null;
    }
}
