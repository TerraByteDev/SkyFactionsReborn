package net.skullian.skyfactions.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

/**
 * Simple utility class to parse json files for
 * use in the platform-independent dependency loaders.
 */
public class LibraryParser {


    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Parse libraries from all json files located in the `/assets/libraries` folder.
     *
     * @return A [List] of libraries and repositories, condensed to [LibraryData]
     */
    public LibraryData parseLibraries() throws URISyntaxException, IOException {
        List<Library> libraries = new ArrayList<>();
        List<Repository> repositories = new ArrayList<>();
        URI folderURI = getClass().getResource("/assets/libraries").toURI();

        try (FileSystem fileSystem = FileSystems.newFileSystem(folderURI, new HashMap<>())) {
            Stream<Path> stream = Files.walk(fileSystem.getPath("/assets/libraries"));

            stream.filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".json")).forEach(filePath -> {
                LibraryData data = parseJson(filePath);
                libraries.addAll(data.artifacts);
                repositories.addAll(data.repositories);
            });
        }

        return new LibraryData(repositories, libraries);
    }

    private LibraryData parseJson(Path filePath) {
        try {
            String str = Files.readString(filePath);
            return mapper.readValue(str, LibraryData.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * A single repository.
     *
     * @param url The URL of the repository. E.g. `<a href="https://repo.maven.apache.org/maven2">https://repo.maven.apache.org/maven2</a>`
     * @param type The type of repository. Most of these will be `default`
     * @param id Purely an aesthetic property. E.g. `central`.
     */
    public record Repository(
            String id,
            String type,
            String url
    ) {}

    /**
     * A single dependency.
     *
     * @param groupId The dependency group id, e.g. `net.kyori`.
     * @param artifactId The ID of the dependency, e.g. `adventure-api`,
     * @param version The version of the dependency, e.g. `4.19.0`. In gradle, we use placeholders like ${adventureVersion} which are replaced on buiild.
     */
    public record Library(
            String groupId,
            String artifactId,
            String version
    ) {}

    /**
     * A simple data class containing artifacts to download,
     * as well as the repositories that should be used.
     */
    public record LibraryData(
        List<Repository> repositories,
        List<Library> artifacts
    ) {}
}
