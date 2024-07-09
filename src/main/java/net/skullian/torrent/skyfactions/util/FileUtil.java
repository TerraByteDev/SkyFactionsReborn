package net.skullian.torrent.skyfactions.util;

import lombok.extern.log4j.Log4j2;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;

import java.io.File;
import java.util.UUID;

@Log4j2(topic = "SkyFactionsReborn")
public class FileUtil {

    public static File getSchematicFile(String name) {
        File retrievedSchematic = new File(SkyFactionsReborn.getInstance().getDataFolder() + "\\schematics", name);
        if (retrievedSchematic.exists() && !retrievedSchematic.isDirectory()) {
            LOGGER.info("Successfully retrieved island schematic [{}].", name);
            return retrievedSchematic;
        }
        LOGGER.error("Failed to retrieve island schematic [{}]. Is the filename correct?", name);
        return null;
    }
}
