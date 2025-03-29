package net.skullian.skyfactions.common.util

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class LibraryParser {
    private val json = Json { ignoreUnknownKeys = true }

    fun parseLibraries(): Pair<List<Library>, List<Repository>> {
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

        return libraries to repositories
    }

    private fun parseJson(filePath: Path): LibraryData {
        val jsonString = Files.readString(filePath)
        return json.decodeFromString(jsonString)
    }
}

@Serializable
data class LibraryData(
    val repositories: List<Repository>,
    val artifacts: List<Library>
)

@Serializable
data class Library(
    val groupId: String,
    val artifactId: String,
    val version: String
)

@Serializable
data class Repository(
    val url: String,
    val type: String,
    val name: String
)