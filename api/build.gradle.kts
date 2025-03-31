group = "net.skullian.skyfactions.common"
version = "${rootProject.version}"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly(libs.kotlinx.serialization)
    compileOnly(libs.adventure.api)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}