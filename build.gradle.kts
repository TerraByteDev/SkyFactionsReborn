plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.shadow)
    alias(libs.plugins.detekt)
}

group = "net.skullian.skyfactions"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

subprojects {
    apply(plugin = "java")
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

    detekt {
        toolVersion = "1.23.8"
        config.setFrom(rootDir.resolve("config/detekt/detekt.yml"))
        buildUponDefaultConfig = true
    }
}

tasks.shadowJar {
    archiveClassifier.set("")
    configurations = project.configurations.compileClasspath.map { listOf(it) }
}

/**
 * Variables that are replaced within resource files,
 * during the processResources task.
 */
fun variables(): Map<String, String> = mapOf(
    "version" to rootProject.version.toString(),
    "kotlinxVersion" to libs.versions.kotlinx.version.get(),
    "adventureVersion" to libs.versions.adventure.version.get(),
)