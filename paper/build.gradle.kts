repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    api(project(":common"))

    compileOnly(libs.paper.api)
    compileOnly(libs.adventure.platform.bukkit)
}