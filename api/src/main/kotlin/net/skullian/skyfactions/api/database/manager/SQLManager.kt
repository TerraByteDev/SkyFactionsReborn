package net.skullian.skyfactions.api.database.manager

import com.zaxxer.hikari.HikariDataSource
import net.skullian.skyfactions.api.database.DatabaseType
import org.jooq.Configuration
import org.jooq.DSLContext

/**
 * A simple class extended by each database implementation,
 * (SQLite and MySQL / similar), with various utility functions.
 */
abstract class SQLManager {

    @Transient protected var dslContext: DSLContext? = null
    @Transient protected var dataSource: HikariDataSource? = null

    private var connected = false

    /**
     * Enable the database implementation.
     */
    abstract fun enable(dialect: DatabaseType, configuration: Configuration)

    /**
     * Called when the server closes.
     * This facilitates the safe closing and saving of the database.
     *
     * This method may also be called if the plugin fails to load.
     */
    fun dispose() {
        dataSource?.close()
        dslContext = null
    }

    /**
     * Returns whether the database has finished connecting,
     * and the [HikariDataSource] is still loading.
     */
    fun isConnected(): Boolean {
        return connected && dataSource?.isRunning ?: false
    }

    /**
     * Get the jOOQ [DSLContext], if present.
     *
     * This may throw an [IllegalStateException] if called too early.
     */
    fun getContext(): DSLContext {
        return dslContext ?: error("Database service is not fully initialised!")
    }

}