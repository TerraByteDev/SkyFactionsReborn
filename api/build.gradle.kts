plugins {
    alias(libs.plugins.dokka)
}

val branchProvider: Provider<String> = providers.exec {
    commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
}.standardOutput.asText

dependencies {
    compileOnly(libs.kotlinx.serialization)
    compileOnly(libs.adventure.api)

    compileOnly(libs.reflections)
}

tasks.publish {
    dependsOn("dokkaHtmlJar")
}

tasks.register<Jar>("dokkaHtmlJar") {
    dependsOn(tasks.dokkaHtml)
    from(tasks.dokkaHtml.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
    archiveFileName = "${project.name}-${project.version}-javadoc.jar"
}

publishing {
    repositories.configureRepo(branchProvider.get() != "master")

    publications {
        register<MavenPublication>("mavenJava") {
            from(components["shadow"])

            artifact(tasks.named("dokkaHtmlJar"))
        }
    }
}

/**
 * Configure the TerraByteDev repository.
 */
fun RepositoryHandler.configureRepo(development: Boolean = false) {
    val user: String? = properties["repo_username"]?.toString() ?: System.getenv("repo_username")
    val pw: String? = properties["repo_password"]?.toString() ?: System.getenv("repo_password")

    if (user != null && pw != null) {
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
