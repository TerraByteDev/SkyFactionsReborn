package net.skullian.skyfactions.api.database

import net.skullian.skyfactions.api.database.manager.SQLManager

/**
 * A database service interface facilitating persistent storage access (r/w).
 */
interface DatabaseService {

    /**
     * Called when all services are loaded.
     * This facilitates the connection of the database implementations.
     */
    fun onEnable()

    /**
     * Called when the server is shutting down.
     */
    fun onDisable()

    /**
     * A simple function that returns whether the database is loaded or not.
     */
    fun isEnabled(): Boolean {
        return getManager()?.isConnected() ?: error("Database service is not fully initialised!")
    }

    /**
     * Get the (initialised, if present) [SQLManager].
     */
    fun getManager(): SQLManager?
}