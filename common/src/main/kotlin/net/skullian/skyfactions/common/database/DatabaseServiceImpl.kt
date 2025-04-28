package net.skullian.skyfactions.common.database

import info.preva1l.trashcan.flavor.annotations.Close
import info.preva1l.trashcan.flavor.annotations.Configure
import info.preva1l.trashcan.flavor.annotations.Service
import net.skullian.skyfactions.api.database.DatabaseService
import net.skullian.skyfactions.api.database.DatabaseType
import net.skullian.skyfactions.common.config.Config
import net.skullian.skyfactions.common.database.jooq.JooqExecutionListener
import net.skullian.skyfactions.common.database.managers.MySQLManager
import net.skullian.skyfactions.api.database.manager.SQLManager
import net.skullian.skyfactions.common.database.managers.SQLiteManager
import org.jooq.Configuration
import org.jooq.SQLDialect
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.DefaultExecuteListenerProvider

/**
 * A simple service class that handles database initialisation,
 * as well as database migrations through jOOQ and Flyway.
 */
@Service
class DatabaseServiceImpl : DatabaseService {

    private var manager: SQLManager? = null

    @Configure
    override fun onEnable() {
        System.setProperty("org.jooq.no-logo", "true")
        System.setProperty("org.jooq.no-tips", "true")

        val dialect: DatabaseType = Config.i().database.databaseType
        manager = when (dialect.dialect) {
            SQLDialect.SQLITE -> SQLiteManager()
            else -> MySQLManager()
        }

        val configuration: Configuration = DefaultConfiguration()
            .set(DefaultExecuteListenerProvider(JooqExecutionListener()))

        manager!!.enable(dialect, configuration)
    }

    @Close
    override fun onDisable() {
        manager?.dispose()
    }

    override fun getManager(): SQLManager? {
        return manager
    }
}