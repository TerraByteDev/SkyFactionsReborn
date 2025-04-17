package net.skullian.skyfactions.common.database.managers

import com.zaxxer.hikari.HikariDataSource
import org.jooq.DSLContext
import org.jooq.SQLDialect

abstract class SQLManager {

    @Transient protected var context: DSLContext? = null
    @Transient protected var dataSource: HikariDataSource? = null

    private var connected = false

    abstract fun enable(dialect: SQLDialect)

    fun dispose() {
        dataSource?.close()
        context = null
    }

    fun isConnected(): Boolean {
        return connected && dataSource?.isRunning ?: false
    }

    fun getContext(): DSLContext {
        return context ?: error("Database service is not fully initialised!")
    }

}