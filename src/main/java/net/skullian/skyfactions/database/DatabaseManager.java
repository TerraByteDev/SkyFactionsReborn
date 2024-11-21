package net.skullian.skyfactions.database;

import com.google.common.net.HostAndPort;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.database.impl.*;
import net.skullian.skyfactions.database.impl.faction.*;
import net.skullian.skyfactions.database.tables.*;
import net.skullian.skyfactions.util.SLogger;
import org.bukkit.Bukkit;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.migration.Context;
import org.flywaydb.core.api.output.MigrateResult;
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

public class DatabaseManager {

    private transient DSLContext ctx;
    @Getter private transient HikariDataSource dataSource;
    @Getter private transient SQLDialect dialect;
    @Getter private transient String url;
    public boolean closed;

    @Getter private CurrencyDatabaseManager currencyManager;
    @Getter private DefencesDatabaseManager defencesManager;
    @Getter private NotificationDatabaseManager notificationManager;
    @Getter private PlayerIslandsDatabaseManager playerIslandManager;
    @Getter private PlayerDatabaseManager playerManager;

    @Getter private FactionAuditLogDatabaseManager factionAuditLogManager;
    @Getter private FactionInvitesDatabaseManager factionInvitesManager;
    @Getter private FactionIslandDatabaseManager factionIslandManager;
    @Getter private FactionsDatabaseManager factionsManager;
    @Getter private FactionElectionManager electionManager;

    public void initialise(String type) throws SQLException {
        createDataSource(new File(SkyFactionsReborn.getInstance().getDataFolder(), "/data/data.sqlite3"), type);
    }

    private void createDataSource(
            @NotNull File file, @NotNull String type) {

        Configuration configuration = new DefaultConfiguration().set(new DefaultExecuteListenerProvider(new DatabaseExecutionListener()));
        System.setProperty("org.jooq.no-tips", "true");
        System.setProperty("org.jooq.no-logo", "true");
        System.setProperty("net.skullian.codegen", "false");

        if (type.equals("sqlite")) {

            HikariConfig sqliteConfig = new HikariConfig();

            this.url = JDBC.PREFIX + file.getAbsolutePath();

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

            this.dataSource = dataSource;
            this.dialect = SQLDialect.SQLITE;
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

            this.url = String.format("jdbc:mysql://%s:%d/%s",
                    host.getHost(), host.getPortOrDefault(3306), databaseName);

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

            this.dataSource = dataSource;
            this.dialect = SQLDialect.MYSQL;
            this.ctx = DSL.using(dataSource, SQLDialect.MYSQL);
        } else {
            handleError(new IllegalStateException("Unknown database type: " + type));
            SLogger.fatal("SkyFactions will now disable.");
            SkyFactionsReborn.getInstance().disable();
        }

        setup();
    }

    private void setup() {
        SLogger.info("Beginning database migrations.");

        Flyway flyway = Flyway.configure(this.getClass().getClassLoader())
                .locations("classpath:net/skullian/skyfactions/database/migrations").failOnMissingLocations(true).cleanDisabled(true)
                .dataSource(this.url, "wefuckinghateflyway", "")
                .load();

        MigrateResult result = flyway.migrate();

        if (result.success) {
            SLogger.info("Database migrations complete: ({} Migrations completed in {}ms)", result.getSuccessfulMigrations().size(), result.getTotalMigrationTime());

            this.currencyManager = new CurrencyDatabaseManager(this.ctx);
            this.defencesManager = new DefencesDatabaseManager(this.ctx);
            this.notificationManager = new NotificationDatabaseManager(this.ctx);
            this.playerIslandManager = new PlayerIslandsDatabaseManager(this.ctx);
            this.playerManager = new PlayerDatabaseManager(this.ctx);

            this.factionAuditLogManager = new FactionAuditLogDatabaseManager(this.ctx);
            this.factionInvitesManager = new FactionInvitesDatabaseManager(this.ctx);
            this.factionIslandManager = new FactionIslandDatabaseManager(this.ctx);
            this.factionsManager = new FactionsDatabaseManager(this.ctx);
            this.electionManager = new FactionElectionManager(this.ctx);
        } else {
            result.getFailedMigrations().forEach(migration -> SLogger.fatal("Migration failed: {}", migration.description));
            handleError(new Exception("Failed to complete migrations - " + result.getFailedMigrations().size() + " Migrations failed: " + result.getException()));
            SLogger.fatal("SkyFactions will now disable.");
            SkyFactionsReborn.getInstance().disable();
        }
    }

    public void closeConnection() {
        this.dataSource.close();
        ctx = null;
    }

    // ------------------ MISC ------------------ //

    public static void handleError(Exception error) {
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
