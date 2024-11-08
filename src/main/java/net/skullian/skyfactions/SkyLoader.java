package net.skullian.skyfactions;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

public class SkyLoader implements PluginLoader {
    @Override
    public void classloader(@NotNull PluginClasspathBuilder pluginClasspathBuilder) {
        MavenLibraryResolver invUIResolver = new MavenLibraryResolver();

        invUIResolver.addRepository(new RemoteRepository.Builder("xenondevs", "default", "https://repo.xenondevs.xyz/releases/").build());
        invUIResolver.addDependency(new Dependency(new DefaultArtifact("xyz.xenondevs.invui:invui-core:1.40"), null));
        invUIResolver.addDependency(new Dependency(new DefaultArtifact("xyz.xenondevs.invui:inventory-access-r21:1.40"), null));
        invUIResolver.addDependency(new Dependency(new DefaultArtifact("xyz.xenondevs.invui:inventory-access-r20:1.40"), null));

        MavenLibraryResolver centralResolver = new MavenLibraryResolver();
        centralResolver.addRepository(new RemoteRepository.Builder("maven-central", "default", "https://repo1.maven.org/maven2/").build());

        centralResolver.addDependency(new Dependency(new DefaultArtifact("com.zaxxer:HikariCP:6.0.0"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("org.xerial:sqlite-jdbc:3.47.0.0"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("net.dv8tion:JDA:5.2.0"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("com.fasterxml.jackson.core:jackson-databind:2.18.1"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("com.jeff-media:custom-block-data:2.2.2"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("org.jetbrains.kotlin:kotlin-stdlib-common:1.9.10"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.21"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("org.jetbrains.kotlin:kotlin-stdlib:1.8.21"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.21"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("org.apache.commons:commons-collections4:4.4"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("net.sf.trove4j:core:3.1.0"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("com.fasterxml.jackson.core:jackson-core:2.17.0"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("com.fasterxml.jackson.core:jackson-annotations:2.17.2"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("org.slf4j:slf4j-api:2.0.9"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("org.jetbrains:annotations:23.0.0"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("com.google.code.findbugs:jsr305:3.0.2"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("com.google.crypto.tink:tink:1.14.1"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("com.google.protobuf:protobuf-java:3.25.3"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("com.google.errorprone:error_prone_annotations:2.18.0"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("com.google.code.gson:gson:2.10.1"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("dev.dejvokep:boosted-yaml:1.3.7"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("org.incendo:cloud-paper:2.0.0-beta.10"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("org.incendo:cloud-annotations:2.0.0"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("net.objecthunter:exp4j:0.4.8"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("net.kyori:adventure-text-minimessage:4.17.0"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("org.jooq:jooq:3.19.15"), null));
        centralResolver.addDependency(new Dependency(new DefaultArtifact("org.jooq:jooq-meta:3.19.15"), null));

        pluginClasspathBuilder.addLibrary(centralResolver);
        pluginClasspathBuilder.addLibrary(invUIResolver);
    }
}
