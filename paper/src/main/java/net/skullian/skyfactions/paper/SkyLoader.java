package net.skullian.skyfactions.paper;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import net.skullian.skyfactions.common.util.LibraryParser;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * The dependency loader for the SkyFactions plugin.
 * To avoid large plugin sizes, we use paper's (experimental) dependency loader to load
 * dependencies during runtime.
 */
@SuppressWarnings("UnstableApiUsage")
public class SkyLoader implements PluginLoader {

    private final LibraryParser parser = new LibraryParser();

    @Override
    public void classloader(PluginClasspathBuilder classpathBuilder) {

        try {
            LibraryParser.LibraryData data = parser.parseLibraries();
            MavenLibraryResolver resolver = new MavenLibraryResolver();

            data.repositories().forEach(repository -> {
                resolver.addRepository(
                        new RemoteRepository.Builder(repository.id(), repository.type(), repository.url()).build()
                );
            });

            data.artifacts().forEach(library -> {
                    resolver.addDependency(
                            new Dependency(new DefaultArtifact(String.format("%s:%s:%s", library.groupId(), library.artifactId(), library.version())), null)
                    );
            });

            classpathBuilder.addLibrary(resolver);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }
}
