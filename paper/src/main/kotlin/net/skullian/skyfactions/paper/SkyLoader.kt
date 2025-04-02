package net.skullian.skyfactions.paper

import io.papermc.paper.plugin.loader.PluginClasspathBuilder
import io.papermc.paper.plugin.loader.PluginLoader
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver
import net.skullian.skyfactions.common.util.LibraryParser
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.repository.RemoteRepository

/**
 * The dependency loader for the SkyFactions plugin.
 * To avoid large plugin sizes, we use paper's (experimental) dependency loader to load
 * dependencies during runtime.
 */
@Suppress("UnstableApiUsage")
class SkyLoader : PluginLoader {

    private val parser = LibraryParser()

    override fun classloader(classpathBuilder: PluginClasspathBuilder) {

        val libraryData = parser.parseLibraries()
        val resolver = MavenLibraryResolver()

        libraryData.repositories.forEach { repository ->
            resolver.addRepository(
                RemoteRepository.Builder(repository.name, repository.type, repository.url).build()
            )
        }

        libraryData.artifacts.forEach { library ->
            resolver.addDependency(
                Dependency(DefaultArtifact("${library.groupId}:${library.artifactId}:${library.version}"), null)
            )
        }

        classpathBuilder.addLibrary(resolver)
    }
}