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
    implementation(libs.jackson.databind)

    compileOnly(libs.adventure.api)
    compileOnly(libs.adventure.text.minimessage)
    compileOnly(libs.reflections)
}