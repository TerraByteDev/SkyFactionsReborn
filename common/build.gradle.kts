group = "net.skullian.skyfactions.common"
version = "${rootProject.version}"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(project(":api"))

    compileOnly(libs.kotlinx.serialization)
    compileOnly(libs.adventure.api)
    compileOnly(libs.adventure.text.minimessage)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}