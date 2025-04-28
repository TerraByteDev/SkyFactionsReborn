package net.skullian.skyfactions.common.database.managers

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.skullian.skyfactions.api.SkyApi
import net.skullian.skyfactions.common.config.Config
import net.skullian.skyfactions.api.database.DatabaseType
import net.skullian.skyfactions.api.database.manager.SQLManager
import net.skullian.skyfactions.common.util.SLogger
import org.jooq.Configuration
import org.jooq.impl.DSL
import java.io.File
import java.util.*

/**
 * The SQLite implementation of [SQLManager].
 */
class SQLiteManager : SQLManager() {

    override fun enable(dialect: DatabaseType, configuration: Configuration) {
        val config = Config.i().database

        val sqliteDatabase: File = SkyApi.getInstance().getPlatform().getConfigDirectory().resolve("data/db.sqlite3")
        sqliteDatabase.mkdirs()

        val properties = Properties().apply {
            putAll(mapOf(
                "encoding" to "UTF-8",
                "enforceForeignKeys" to "true",
                "synchronous" to "NORMAL",
                "journalMode" to "WAL",
            ))
        }

        SLogger.info("Using SQLite database at ${sqliteDatabase.absolutePath}.")

        val url = "jdbc:${dialect.jdbcString}:${sqliteDatabase.absolutePath}"
        val hikariConfig = HikariConfig().apply {
            dataSourceClassName = dialect.jdbcDriver
            connectionTestQuery = "SELECT 1"
            jdbcUrl = url
            maxLifetime = config.maxLifetime
            maximumPoolSize = config.maxPoolSize
            dataSourceProperties = properties
            poolName = "SkyFactions SQLite Pool"
        }

        dataSource = HikariDataSource(hikariConfig)
        configuration
            .set(dataSource)
            .set(dialect.dialect)

        dslContext = DSL.using(configuration)
    }

}