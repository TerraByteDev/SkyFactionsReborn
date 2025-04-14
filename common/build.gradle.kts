import org.jooq.meta.jaxb.Logging

buildscript {

    repositories {
        mavenCentral()
    }

    // jOOQ and Flyway use SQLite for validating migrations and creating classes, so we
    // need to add the SQLite JDBC dependency to the buildscript for the plugins
    dependencies {
        classpath(libs.sqlite.jdbc)
    }
}

plugins {
    alias(libs.plugins.flyway)
    alias(libs.plugins.jooq.codgen)
}

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

    compileOnly(libs.bundles.adventure)
    compileOnly(libs.reflections)

    compileOnly(libs.bundles.database)

    jooqCodegen(libs.sqlite.jdbc)
}

flyway {
    url = "jdbc:sqlite:file:${rootProject.rootDir}/assets/data.sqlite3"
    user = "sa"
    password = ""
    locations = arrayOf("classpath:net/skullian/skyfactions/common/database/flyway/migrations")
    driver = "org.sqlite.JDBC"
}

jooq {
    configuration {
        logging = Logging.DEBUG

        jdbc {
            driver = "org.sqlite.JDBC" // This is why we added SQLite JDBC to the classpath!
            url = flyway.url
            user = flyway.user
            password = flyway.password
        }

        generator {
            name = "org.jooq.codegen.KotlinGenerator"
            database {
                name = "org.jooq.meta.sqlite.SQLiteDatabase"
                includes = ".*"
                excludes = ""
            }

            target {
                packageName = "net.skullian.skyfactions.common.database.jooq"
                directory = "src/main/kotlin"
                isClean = false
            }
        }
    }
}

tasks {
    flywayMigrate.configure {
        notCompatibleWithConfigurationCache("Flyway tasks access project during execution.")
        System.setProperty("net.skullian.skyfactions.codegen", "true")

        dependsOn("classes")
    }

    jooqCodegen.configure {
        dependsOn("flywayMigrate")
    }

    detekt.configure {
        dependsOn("jooqCodegen")
    }
}