plugins {
    alias(libs.plugins.pluginyml.paper)
}

val compileOnlyLibrary = configurations.maybeCreate("compileOnlyLibrary")
plugins.withType<JavaPlugin> {
    extensions.getByType<SourceSetContainer>().named(SourceSet.MAIN_SOURCE_SET_NAME) {
        configurations.compileOnly.get().extendsFrom(compileOnlyLibrary)
        configurations.library.get().extendsFrom(compileOnlyLibrary)
    }
}

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    api(project(":common"))

    compileOnly(libs.paper.api)
    compileOnlyLibrary(libs.adventure.platform.bukkit)

    implementation(libs.trashcan.paper)
    annotationProcessor(libs.trashcan.paper)

    library(libs.bundles.adventure)
    library(libs.bundles.database)
    library(libs.adventure.platform.bukkit)
}

paper {
    name = "SkyFactionsReborn"
    main = "net.skullian.skyfactions.paper.SkyFactionsReborn"
    loader = "net.skullian.skyfactions.library.trashcan.plugin.libloader.BaseLibraryLoader"
    version = rootProject.version.toString()
    apiVersion = "1.21"
    authors = listOf("Skullians", "nouish", "JerichoTorrent")
    contributors = listOf("imMohika", "Preva1l", "ProGamingDK", "DannyX", "Kioku", "Haibun", "Kishku", "ZephyrZymbol")
    website = "https://github.com/TerraByteDev/SkyFactionsReborn"
    generateLibrariesJson = true
}

tasks.shadowJar {
    relocate("info.preva1l", "net.skullian.skyfactions.library") // there is flavor and trashcan, I don't want it to be library.trashcan.trashcan
}