import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.shadow)
    alias(libs.plugins.detekt)
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    group = "net.skullian.skyfactions"
    version = "1.0.0-ALPHA"

    val apiAnnotationProcessor = configurations.maybeCreate("apiAnnotation")
    plugins.withType<JavaPlugin> {
        extensions.getByType<SourceSetContainer>().named(SourceSet.MAIN_SOURCE_SET_NAME) {
            configurations.api.get().extendsFrom(apiAnnotationProcessor)
            configurations.annotationProcessor.get().extendsFrom(apiAnnotationProcessor)
        }
    }

    repositories {
        mavenCentral()
        maven("https://libraries.minecraft.net")
        maven("https://repo.preva1l.info/releases")
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "com.gradleup.shadow")
    apply(plugin = "io.gitlab.arturbosch.detekt")

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

        archiveFileName.set("${rootProject.name}-${project.name}-v${rootProject.version}.jar")
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
        .dependsOn("shadowJar")


    detekt {
        toolVersion = "1.23.8"
        config.setFrom(rootDir.resolve("config/detekt/detekt.yml"))
        buildUponDefaultConfig = true
    }
}

dependencies {
    implementation("org.xerial:sqlite-jdbc:3.42.0.0")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        configurations = listOf(project.configurations.runtimeClasspath.get())
        dependencies {
            include(project(":common"))
            include(project(":api"))
        }
    }
}