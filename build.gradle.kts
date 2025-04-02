import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.ajoberstar.grgit.Grgit
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.shadow)
    alias(libs.plugins.detekt)
}

val branchProvider = providers.exec {
    commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
}.standardOutput.asText

allprojects {
    group = "net.skullian.skyfactions"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
        configureRepo(branchProvider.get() != "master")
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "com.gradleup.shadow")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    tasks.processResources {
        val tokenMap = variables()
        filesMatching(listOf("**/*.json", "**/*.yml")) {
            expand(tokenMap)
        }
    }

    kotlin {
        jvmToolchain(jdkVersion = 21)
    }

    sourceSets {
        main {
            java {
                setSrcDirs(listOf("src/main/java", "src/main/kotlin"))
            }
        }
    }

    tasks.withType<ShadowJar> {
        archiveClassifier.set("")
        exclude(
            "**/*.kotlin_metadata",
            "**/*.kotlin_builtins",
            "META-INF/",
            "kotlin/**",
            "org/**"
        )

        archiveFileName.set("${rootProject.name}-${project.name}-${rootProject.version}.jar")
        destinationDirectory.set(rootProject.rootDir.resolve("./libs"))
    }

    tasks.withType<JavaCompile> {
        options.compilerArgs.add("-parameters")
        options.fork()
        options.encoding = "UTF-8"
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            javaParameters = true
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    tasks.getByName("build")
        .dependsOn(
            "shadowJar"
        )

    detekt {
        toolVersion = "1.23.8"
        config.setFrom(rootDir.resolve("config/detekt/detekt.yml"))
        buildUponDefaultConfig = true
    }

    publishing {
        repositories.configureRepo(branchProvider.get() != "master")

        publications {
            register(
                name = "mavenJava",
                type = MavenPublication::class,
                configurationAction = shadow::component
            )
        }
    }
}

tasks.shadowJar {
    archiveClassifier.set("")
    configurations = listOf(project.configurations.runtimeClasspath.get())
    dependencies {
        include(project(":common"))
        include(project(":api"))
    }
}

/**
 * Variables that are replaced within resource files,
 * during the processResources task.
 */
fun variables(): Map<String, String> = mapOf(
    "version" to rootProject.version.toString(),
    "kotlinxVersion" to libs.versions.kotlinx.version.get(),
    "adventureVersion" to libs.versions.adventure.version.get(),
    "adventurePlatformVersion" to libs.versions.adventure.platform.version.get(),
    "reflectionsVersion" to libs.versions.reflections.version.get(),
    "jakartaVersion" to libs.versions.jakarta.version.get(),
)

/**
 * Configure the TerraByteDev repository.
 */
fun RepositoryHandler.configureRepo(development: Boolean = false) {
    val user: String? = properties["repo_username"]?.toString() ?: System.getenv("repo_username")
    val pw: String? = properties["repo_password"]?.toString() ?: System.getenv("repo_password")

    if (user != null && pw != null) {
        println("Using authenticated credentials for TerraByteDev repository.")
        maven("https://repo.terrabytedev.com/${if (development) "snapshots" else "releases"}/") {
            name = "TerrabyteDev"
            credentials {
                username = user
                password = pw
            }
        }

        return
    }

    println("Using TerraByteDev repository without credentials.")

    maven("https://repo.terrabytedev.com/${if (development) "snapshots" else "releases"}/") {
        name = "TerraByteDev"
    }
}
