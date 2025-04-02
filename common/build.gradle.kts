repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    api(project(":api")) {
        exclude(group = "org.jetbrains")
        exclude(group = "kotlin")
    }

    compileOnly(libs.kotlinx.serialization)
    compileOnly(libs.adventure.api)
    compileOnly(libs.adventure.text.minimessage)
    compileOnly(libs.reflections)
}