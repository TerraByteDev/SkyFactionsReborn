group = "net.skullian.skyfactions.paper"
version = "${rootProject.version}"

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":api"))

    compileOnly(libs.paper.api)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}