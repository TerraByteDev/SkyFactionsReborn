package net.skullian.skyfactions.common.util

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Simple utility class to parse json files for
 * use in the platform-independent dependency loaders.
 */
class LibraryParser {
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Parse libraries from all json files located in the `/assets/libraries` folder.
     *
     * @return A [List] of libraries and repositories, condensed to [LibraryData]
     */
    fun parseLibraries(): LibraryData {
        val libraries = mutableListOf<Library>()
        val repositories = mutableListOf<Repository>()
        val folderUri = this::class.java.getResource("/assets/libraries")?.toURI()
            ?: throw IllegalArgumentException("Failed to find resource: /assets/libraries")
        val path = Paths.get(folderUri)

        Files.walk(path).use { paths ->
            paths.filter { Files.isRegularFile(it) && it.toString().endsWith(".json") }.forEach { filePath ->
                val data = parseJson(filePath)
                libraries.addAll(data.artifacts)
                repositories.addAll(data.repositories)
            }
        }

        return LibraryData(repositories, libraries)
    }

    private fun parseJson(filePath: Path): LibraryData {
        val jsonString = Files.readString(filePath)
        return json.decodeFromString(jsonString)
    }
}

/**
 * A simple data class containing artifacts to download,
 * as well as the repositories that should be used.
 */
@Serializable
data class LibraryData(
    val repositories: List<Repository>,
    val artifacts: List<Library>
)

/**
 * A single dependency.
 *
 * @property groupId The dependency group id, e.g. `net.kyori`.
 * @property artifactId The ID of the dependency, e.g. `adventure-api`,
 * @property version The version of the dependency, e.g. `4.19.0`. In gradle, we use placeholders like ${adventureVersion} which are replaced on buiild.
 */
@Serializable
data class Library(
    val groupId: String,
    val artifactId: String,
    val version: String
)

/**
 * A single repository.
 *
 * @property url The URL of the repository. E.g. `https://repo.maven.apache.org/maven2`
 * @property type The type of repository. Most of these will be `default`
 * @property name Purely an aesthetic property. E.g. `central`.
 */
@Serializable
data class Repository(
    val url: String,
    val type: String,
    val name: String
)