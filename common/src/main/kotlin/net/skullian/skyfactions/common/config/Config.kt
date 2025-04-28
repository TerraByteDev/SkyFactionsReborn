package net.skullian.skyfactions.common.config

import de.exlll.configlib.Comment
import de.exlll.configlib.Configuration
import de.exlll.configlib.NameFormatters
import de.exlll.configlib.YamlConfigurationProperties
import de.exlll.configlib.YamlConfigurations
import net.skullian.skyfactions.api.SkyApi
import net.skullian.skyfactions.api.database.DatabaseType
import net.skullian.skyfactions.common.util.SLogger
import java.nio.file.Path

/**
 * The main config for SkyFactions.
 */
@Configuration
class Config {


    /**
     * Database configuration.
     * Facilitates SQLite and MySQL (or similar) database configurations.
     */
    @Configuration
    class Database {
        @Comment(
            "Select a database type here.",
            "Supported types:",
            "- SQLITE",
            "- MYSQL",
            "- MARIADB",
            "- POSTGRESQL"
        )
        var databaseType = DatabaseType.SQLITE
        var databaseHost = "localhost"
        var databasePort = 3306
        var databaseName = "skyfactions"
        var databaseUsername = "root"
        var databasePassword = "replaceThisPassword"
        var useSSL = false

        @Comment(
            "\nMax lifetime for the database connection", // add a space with \n
            "Format: Milliseconds"
        ) var maxLifetime: Long = 1_800_000
        @Comment(
            "Max amount of pools that can be connected at one time.",
            "Applies for all database types."
        ) var maxPoolSize: Int = 10
        @Comment(
            "Additional properties for the connection source can be placed here.",
            "Format: \"propertyName\", \"propertyValue\""
        ) var properties: List<String> = emptyList()
    }

    /**
     * Configurations that apply to multiple (if not all) aspects of the plugin.
     */
    @Configuration
    class Global {
        @Comment(
            "The majority of SkyFaction's operations run asynchronously.",
            "This prevents the majority of the code running on the main thread,",
            "however THIS DOES NOT FIX LAG! This can & will still cause lag if",
            "your CPU is not very powerful. Do not set the below value too high.",
            "\nThe below variable is the amount of threads that the plugin can use."
        ) var globalExecutorThreadSize = 2

        @Comment(
            "SkyFactions supports logging exceptions to Sentry.",
            "You can configure the Sentry integration here."
        ) var sentry = SentryDsn()

        /**
         * Configure sentry.io monitoring.
         */
        @Configuration
        class SentryDsn {
            @Comment("Leaving dsn-url empty will disable the Sentry integration.")
            var dsnUrl = ""
            @Comment("What 'environment' is this plugin running on? E.g. production / testing.")
            var environment = "production"
            @Comment("What is the 'name' of this server?")
            var serverName = "skyfactions"
        }
    }

    @Comment("All database configurations require a restart to take effect.")
    var database = Database()
    @Comment("") // space
    var global = Global()

    companion object {
        private var instance: Config? = null

        private const val fileName = "config.yml"
        private val header = """
               _____      __  __  _                 
              / ___/___  / /_/ /_(_)___  ____ ______
              \__ \/ _ \/ __/ __/ / __ \/ __ `/ ___/
             ___/ /  __/ /_/ /_/ / / / / /_/ (__  ) 
            /____/\___/\__/\__/_/_/ /_/\__, /____/  
                          /____/        
            
            Ensure you validate your configurations
            through a validator such as YAMLLint.
        """.trimIndent()

        private val properties = YamlConfigurationProperties.newBuilder()
            .charset(Charsets.UTF_8)
            .header(header)
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE)
            .build()

        /**
         * Get an instance of [Config].
         */
        @JvmStatic
        fun i(): Config {
            if (instance == null) {
                instance = YamlConfigurations.update(getConfigLocation(), Config::class.java, properties)
            }

            return instance!!
        }

        /**
         * Reload config changes after file modifications.
         */
        @JvmStatic
        fun reload() {
            instance = YamlConfigurations.load(getConfigLocation(), Config::class.java, properties)
            SLogger.info("Successfully reloaded main config")
        }

        private fun getConfigLocation(): Path {
            return SkyApi.getInstance().getPlatform().getConfigDirectory().resolve(fileName).toPath()
        }
    }

}