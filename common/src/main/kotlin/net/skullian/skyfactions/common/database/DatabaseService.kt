package net.skullian.skyfactions.common.database

import net.skullian.skyfactions.api.library.flavor.service.Service
import net.skullian.skyfactions.api.library.flavor.service.Configure
import net.skullian.skyfactions.common.config.Config
import net.skullian.skyfactions.common.database.managers.MySQLManager
import net.skullian.skyfactions.common.database.managers.SQLManager
import net.skullian.skyfactions.common.database.managers.SQLiteManager
import net.skullian.skyfactions.common.util.SLogger
import org.jooq.SQLDialect

/**
 * A simple service class that handles database initialisation,
 * as well as database migrations through jOOQ and Flyway.
 */
@Service(
    name = "Database Service",
    priority = 10
)
class DatabaseService {

    private var manager: SQLManager? = null

    @Configure
    fun onEnable() {
        System.setProperty("org.jooq.no-logo", "true")
        System.setProperty("org.jooq.no-tips", "true")

        val dialect: SQLDialect = SQLDialect.valueOf(Config.i().database.databaseType.name)
        manager = when (dialect) {
            SQLDialect.SQLITE -> SQLiteManager()
            else -> MySQLManager()
        }

        SLogger.info("Initialising database with type ${dialect.name}")
        manager!!.enable(dialect)
    }

    fun isEnabled(): Boolean {
        return manager?.isConnected() ?: error("Database service is not fully initialised!")
    }
}