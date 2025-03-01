package net.skullian.skyfactions.paper;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class SkyLoader implements PluginLoader {

    @Override
    public void classloader(@NotNull PluginClasspathBuilder pluginClasspathBuilder) {
        MavenLibraryResolver invUIResolver = new MavenLibraryResolver();

        invUIResolver.addRepository(new RemoteRepository.Builder("xenondevs", "default", "https://repo.xenondevs.xyz/releases/").build());

        invUIResolver.addDependency(new Dependency(new DefaultArtifact("xyz.xenondevs.invui:invui-core:1.43"), null));
        invUIResolver.addDependency(new Dependency(new DefaultArtifact("xyz.xenondevs.invui:inventory-access-r22:1.43"), null));
        invUIResolver.addDependency(new Dependency(new DefaultArtifact("xyz.xenondevs.invui:inventory-access-r21:1.43"), null));
        invUIResolver.addDependency(new Dependency(new DefaultArtifact("xyz.xenondevs.invui:inventory-access-r20:1.43"), null));


        MavenLibraryResolver centralResolver = new MavenLibraryResolver();
        centralResolver.addRepository(new RemoteRepository.Builder("maven-central", "default", "https://repo1.maven.org/maven2/").build());

        centralResolver.addDependency(new Dependency(new DefaultArtifact("com.zaxxer:HikariCP:6.2.1"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("org.xerial:sqlite-jdbc:3.47.2.0"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("net.dv8tion:JDA:5.2.2"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("com.fasterxml.jackson.core:jackson-databind:2.18.1"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("com.jeff-media:custom-block-data:2.2.3"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("org.apache.commons:commons-lang3:3.17.0"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("com.fasterxml.jackson.core:jackson-core:2.18.2"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("com.fasterxml.jackson.core:jackson-annotations:2.18.2"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("org.slf4j:slf4j-api:2.0.9"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("org.jetbrains:annotations:23.0.0"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("dev.dejvokep:boosted-yaml:1.3.7"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("org.incendo:cloud-paper:2.0.0-beta.10"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("org.incendo:cloud-annotations:2.0.0"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("net.objecthunter:exp4j:0.4.8"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("net.kyori:adventure-text-minimessage:4.17.0"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("net.kyori:adventure-text-logger-slf4j:4.17.0"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("net.kyori:adventure-text-serializer-legacy:4.17.0"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("net.kyori:adventure-platform-bukkit:4.3.4"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("org.jooq:jooq:3.19.16"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("org.flywaydb:flyway-core:11.1.1"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("org.flywaydb:flyway-mysql:11.1.1"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("org.flywaydb:flyway-database-postgresql:11.1.1"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("com.google.guava:guava:33.3.1-jre"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("org.postgresql:postgresql:42.7.4"), null));


        MavenLibraryResolver multiLibResolver = new MavenLibraryResolver();
        multiLibResolver.addRepository(new RemoteRepository.Builder("multipaper", "default", "https://repo.clojars.org/").build());

        multiLibResolver.addDependency(new Dependency(new DefaultArtifact("com.github.puregero:multilib:1.2.4"), null));


        pluginClasspathBuilder.addLibrary(invUIResolver);
        pluginClasspathBuilder.addLibrary(centralResolver);
        pluginClasspathBuilder.addLibrary(multiLibResolver);
    }
}
