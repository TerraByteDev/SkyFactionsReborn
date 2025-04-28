package net.skullian.skyfactions.api.database

import org.jooq.SQLDialect

/**
 * A simple class for database types.
 */
enum class DatabaseType(val dialect: SQLDialect, val jdbcString: String, val jdbcDriver: String) {

    SQLITE(SQLDialect.SQLITE, "sqlite", "org.sqlite.SQLiteDataSource"),
    MYSQL(SQLDialect.MYSQL, "mysql", "com.mysql.cj.jdbc.Driver"),
    MARIADB(SQLDialect.MARIADB, "mariadb", "org.mariadb.jdbc.Driver"),
    POSTGRESQL(SQLDialect.POSTGRES, "postgresql", "org.postgresql.Driver")

}