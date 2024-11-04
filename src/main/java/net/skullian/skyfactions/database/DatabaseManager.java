package net.skullian.skyfactions.database;

import com.google.common.net.HostAndPort;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.database.impl.IslandDatabase;
import net.skullian.skyfactions.util.SLogger;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.sqlite.JDBC;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.jooq.impl.SQLDataType.*;

public class DatabaseManager {

    private transient DSLContext ctx;
    public boolean closed;

    public IslandDatabase islandDatabase;

    public void initialise(String type) throws SQLException {
        createDataSource(new File(SkyFactionsReborn.getInstance().getDataFolder(), "/data/data.sqlite3"), type);

        this.islandDatabase = new IslandDatabase(this.ctx);
    }

    private void createDataSource(
            @NotNull File file, @NotNull String type) {

        Configuration configuration = new DefaultConfiguration().set(new DefaultExecuteListenerProvider(new DatabaseExecutionListener()));

        if (type.equals("sqlite")) {

            HikariConfig sqliteConfig = new HikariConfig();
            sqliteConfig.setDataSourceClassName("org.sqlite.SQLiteDataSource");
            sqliteConfig.addDataSourceProperty("url", JDBC.PREFIX + file.getAbsolutePath());
            sqliteConfig.addDataSourceProperty("encoding", "UTF-8");
            sqliteConfig.addDataSourceProperty("enforceForeignKeys", "true");
            sqliteConfig.addDataSourceProperty("synchronous", "NORMAL");
            sqliteConfig.addDataSourceProperty("journalMode", "WAL");
            sqliteConfig.setPoolName("SQLite");
            sqliteConfig.setMaximumPoolSize(1);

            HikariDataSource dataSource = new HikariDataSource(sqliteConfig);

            SLogger.info("Using SQLite Database.");

            configuration
                    .set(dataSource)
                    .set(SQLDialect.SQLITE);
            this.ctx = DSL.using(configuration);
        } else if (type.equals("sql")) {

            String rawHost = Settings.DATABASE_HOST.getString();
            String databaseName = Settings.DATABASE_NAME.getString();
            String username = Settings.DATABASE_USERNAME.getString();
            String password = Settings.DATABASE_PASSWORD.getString();

            List<String> missingProperties = new ArrayList<>();

            if (rawHost == null || rawHost.isBlank()) {
                missingProperties.add("DATABASE_HOST");
            }

            if (databaseName == null || databaseName.isBlank()) {
                missingProperties.add("DATABASE_NAME");
            }

            if (username == null || username.isBlank()) {
                missingProperties.add("DATABASE_USERNAME");
            }

            if (password == null || password.isBlank()) {
                missingProperties.add("DATABASE_PASSWORD");
            }

            if (!missingProperties.isEmpty()) {
                throw new IllegalStateException("Missing MySQL Configuration Properties: " + missingProperties);
            }

            HostAndPort host = HostAndPort.fromHost(rawHost);
            HikariConfig mysqlConfig = new HikariConfig();
            mysqlConfig.setPoolName("SkyFactions");
            mysqlConfig.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s",
                    host.getHost(), host.getPortOrDefault(3306), databaseName));
            mysqlConfig.setMaxLifetime(TimeUnit.MINUTES.toMillis(Settings.DATABASE_MAX_LIFETIME.getInt()));
            mysqlConfig.setUsername(username);
            mysqlConfig.setPassword(password);
            mysqlConfig.setMaximumPoolSize(2);

            SLogger.info("Using MySQL database '{}' on: {}:{}.",
                    databaseName, host.getHost(), host.getPortOrDefault(3306));
            HikariDataSource dataSource = new HikariDataSource(mysqlConfig);

            configuration
                    .set(dataSource)
                    .set(SQLDialect.MYSQL);
            this.ctx = DSL.using(dataSource, SQLDialect.MYSQL);
        } else {
            throw new IllegalStateException("Unknown database type: " + type);
        }
    }

    private void setupTables() {
        SLogger.info("Creating SQL Tables.");

         ctx.createTable("islands")
                .column("id", INTEGER)
                .column("uuid", VARCHAR)
                .column("level", INTEGER)
                .column("gems", INTEGER)
                .column("runes", INTEGER)
                .column("defenceCount", INTEGER)
                .column("last_raided", BIGINT)
                .column("last_raider", VARCHAR)
                .primaryKey("id")
                .execute();

        ctx.createTable("playerData")
                .column("uuid", VARCHAR)
                .column("faction", VARCHAR)
                .column("discord_id", VARCHAR)
                .column("last_raid", BIGINT)
                .column("locale", VARCHAR)
                .primaryKey("uuid")
                .execute();

        ctx.createTable("factionIslands")
                .column("id", INTEGER)
                .column("factionName", VARCHAR)
                .column("runes", INTEGER)
                .column("defenceCount", INTEGER)
                .column("gems", INTEGER)
                .column("last_raided", BIGINT)
                .column("last_raider", VARCHAR)
                .primaryKey("id")
                .execute();

        ctx.createTable("factions")
                .column("name", VARCHAR)
                .column("motd", VARCHAR)
                .column("level", INTEGER)
                .column("last_raid", BIGINT)
                .column("locale", VARCHAR)
                .primaryKey("name")
                .execute();

        ctx.createTable("factionMembers")
                .column("factionName", VARCHAR)
                .column("uuid", VARCHAR)
                .column("rank", VARCHAR)
                .primaryKey("uuid")
                .execute();

        ctx.createTable("trustedPlayers")
                .column("island_id", INTEGER)
                .column("uuid", VARCHAR)
                .primaryKey("island_id")
                .execute();

        ctx.createTable("defenceLocations")
                .column("uuid", VARCHAR)
                .column("type", VARCHAR)
                .column("factionName", VARCHAR)
                .column("x", INTEGER)
                .column("y", INTEGER)
                .column("z", INTEGER)
                .execute();

        ctx.createTable("auditLogs")
                .column("factionName", VARCHAR)
                .column("type", VARCHAR)
                .column("uuid", VARCHAR)
                .column("replacements", VARCHAR)
                .column("timestamp", BIGINT)
                .execute();

        ctx.createTable("factionBans")
                .column("factionName", VARCHAR)
                .column("uuid", VARCHAR)
                .execute();

        ctx.createTable("factionInvites")
                .column("factionName", VARCHAR)
                .column("uuid", VARCHAR)
                .column("inviter", VARCHAR)
                .column("type", VARCHAR)
                .column("accepted", INTEGER)
                .column("timestamp", BIGINT)
                .execute();

        ctx.createTable("notifications")
                .column("uuid", VARCHAR)
                .column("type", VARCHAR)
                .column("description", VARCHAR)
                .column("timestamp", BIGINT)
                .execute();
    }

    // ------------------ MISC ------------------ //

    public static void handleError(SQLException error) {
        Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> {
            SLogger.fatal("----------------------- DATABASE EXCEPTION -----------------------");
            SLogger.fatal("There was an error while performing database actions:");
            SLogger.fatal(error.getMessage());
            SLogger.fatal("Please see https://docs.terrabytedev.com/skyfactions/errors-and-debugging for more information.");
            SLogger.fatal("Please contact the devs.");
            SLogger.fatal("----------------------- DATABASE EXCEPTION -----------------------");
        });
    }
}
