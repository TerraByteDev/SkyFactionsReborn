package net.skullian.torrent.skyfactions.defence;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.defence.struct.DefenceStruct;
import net.skullian.torrent.skyfactions.util.SLogger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class DefencesRegistry {

    private static Map<String, ? extends DefenceStruct> a = new HashMap<>();

    public static void register() {
        try {
            URI defencesFolder = SkyFactionsReborn.class.getResource("/defences").toURI();
            FileSystem fileSystem = FileSystems.newFileSystem(defencesFolder, new HashMap());
            for (Path yamlPath : fileSystem.getRootDirectories()) {
                Stream<Path> stream = Files.list(yamlPath.resolve("/defences"));
                stream.forEach(path -> {
                    String fullPathName = path.getFileName().toString();
                    String defenceName = fullPathName.substring(0, fullPathName.length() - 4);
                    SLogger.info("Registering Defence: \u001B[32m{}", defenceName);


                });
            }
        } catch (URISyntaxException | IOException error) {
            SLogger.fatal("----------------------- CONFIGURATION EXCEPTION -----------------------");
            SLogger.fatal("There was an error loading defences.");
            SLogger.fatal("Please check the below error and proceed accordingly.");
            SLogger.fatal("Plugin will now disable.");
            SLogger.fatal("----------------------- CONFIGURATION EXCEPTION -----------------------");
            error.printStackTrace();
            SkyFactionsReborn.getInstance().getServer().getPluginManager().disablePlugin(SkyFactionsReborn.getInstance());
        }
    }

}
