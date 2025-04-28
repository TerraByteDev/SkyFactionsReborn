package net.skullian.skyfactions.common.database.managers

import com.google.common.net.HostAndPort
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.skullian.skyfactions.common.config.Config
import net.skullian.skyfactions.api.database.DatabaseType
import net.skullian.skyfactions.api.database.manager.SQLManager
import net.skullian.skyfactions.common.util.SLogger
import org.jooq.Configuration
import org.jooq.impl.DSL
import java.util.*

/**
 * The MySQL (and similar) implementation of [SQLManager].
 */
class MySQLManager : SQLManager() {

    override fun enable(dialect: DatabaseType, configuration: Configuration) {
        val config = Config.i().database
        val missingProperties = listOf(
            "database-host" to config.databaseHost,
            "database-name" to config.databaseName,
            "database-username" to config.databaseUsername,
            "database-password" to config.databasePassword
        ).filter { it.second.isBlank() }.map { it.first }

        check(missingProperties.isEmpty()) { "Missing database configuration properties: $missingProperties" }

        // these are (apparently) recommended settings for MySQL databases.
        // https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
        val properties = Properties().apply {
            putAll(mapOf(
                "prepStmtCacheSize" to "250",
                "useLocalSessionState" to "true",
                "useLocalTransactionState" to "true",
                "useServerPrepStmts" to "true",
                "cachePrepStmts" to "true",
                "useSSL" to Config.i().database.useSSL.toString(),
                "rewriteBatchedStatements" to "true",
                "maintainTimeStats" to "false",
                "cacheResultSetMetadata" to "true",
                "cacheServerConfiguration" to "true",
                "elideSetAutoCommits" to "true",
                "prepStmtCacheSqlLimit" to "2048"
            ))
        }

        // we only use HostAndPort as it has a nice validation system within it
        val host = HostAndPort.fromParts(config.databaseHost, config.databasePort)
        val url = "jdbc:${dialect.jdbcString}://${host.host}:${host.port}/${config.databaseName}"

        val hikariConfig = HikariConfig().apply {
            dataSourceProperties = properties
            poolName = "SkyFactions SQL Pool"
            jdbcUrl = url
            maxLifetime = config.maxLifetime
            username = config.databaseUsername
            password = config.databasePassword
            maximumPoolSize = config.maxPoolSize
            minimumIdle = config.maxPoolSize
            keepaliveTime = 0
            connectionTimeout = 5000
            driverClassName = dialect.jdbcDriver
        }

        SLogger.info("Using ${dialect.name} database '${config.databaseName}' on ${config.databaseHost}:${config.databasePort}.")

        dataSource = HikariDataSource(hikariConfig)
        configuration
            .set(dataSource)
            .set(dialect.dialect)

        dslContext = DSL.using(configuration)
    }
}