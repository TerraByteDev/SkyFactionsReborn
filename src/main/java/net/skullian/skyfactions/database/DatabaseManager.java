package net.skullian.skyfactions.database;

import com.google.common.net.HostAndPort;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.database.impl.*;
import net.skullian.skyfactions.database.impl.faction.FactionAuditLogDatabaseManager;
import net.skullian.skyfactions.database.impl.faction.FactionInvitesDatabaseManager;
import net.skullian.skyfactions.database.impl.faction.FactionIslandDatabaseManager;
import net.skullian.skyfactions.database.impl.faction.FactionsDatabaseManager;
import net.skullian.skyfactions.database.tables.*;
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

public class DatabaseManager {

    private transient DSLContext ctx;
    private transient HikariDataSource dataSource;
    public boolean closed;

    public CurrencyDatabaseManager currencyManager;
    public DefencesDatabaseManager defencesManager;
    public NotificationDatabaseManager notificationManager;
    public PlayerIslandsDatabaseManager playerIslandManager;
    public PlayerDatabaseManager playerManager;

    public FactionAuditLogDatabaseManager factionAuditLogManager;
    public FactionInvitesDatabaseManager factionInvitesManager;
    public FactionIslandDatabaseManager factionIslandManager;
    public FactionsDatabaseManager factionsManager;

    public void initialise(String type) throws SQLException {
        createDataSource(new File(SkyFactionsReborn.getInstance().getDataFolder(), "/data/data.sqlite3"), type);

        this.currencyManager = new CurrencyDatabaseManager(this.ctx);
        this.defencesManager = new DefencesDatabaseManager(this.ctx);
        this.notificationManager = new NotificationDatabaseManager(this.ctx);
        this.playerIslandManager = new PlayerIslandsDatabaseManager(this.ctx);
        this.playerManager = new PlayerDatabaseManager(this.ctx);

        this.factionAuditLogManager = new FactionAuditLogDatabaseManager(this.ctx);
        this.factionInvitesManager = new FactionInvitesDatabaseManager(this.ctx);
        this.factionIslandManager = new FactionIslandDatabaseManager(this.ctx);
        this.factionsManager = new FactionsDatabaseManager(this.ctx);
    }

    private void createDataSource(
            @NotNull File file, @NotNull String type) {

        Configuration configuration = new DefaultConfiguration().set(new DefaultExecuteListenerProvider(new DatabaseExecutionListener()));

        System.setProperty("org.jooq.no-tips", "true");
        System.setProperty("org.jooq.no-logo", "true");


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

            this.dataSource = dataSource;
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

            this.dataSource = dataSource;
            this.ctx = DSL.using(dataSource, SQLDialect.MYSQL);
        } else {
            throw new IllegalStateException("Unknown database type: " + type);
        }

        setupTables();
    }

    private void setupTables() {
        SLogger.info("Creating SQL Tables.");

        ctx.createTable(Islands.ISLANDS)
                .primaryKey(Islands.ISLANDS.ID)
                .execute();

        ctx.createTable(Playerdata.PLAYERDATA)
                .primaryKey(Playerdata.PLAYERDATA.UUID)
                .execute();

        ctx.createTable(Factionislands.FACTIONISLANDS)
                .primaryKey(Factionislands.FACTIONISLANDS.ID)
                .execute();

        ctx.createTable(Factions.FACTIONS)
                .primaryKey(Factions.FACTIONS.NAME)
                .execute();

        ctx.createTable(Factionmembers.FACTIONMEMBERS)
                .primaryKey(Factionmembers.FACTIONMEMBERS.UUID)
                .execute();

        ctx.createTable(Trustedplayers.TRUSTEDPLAYERS)
                .primaryKey(Trustedplayers.TRUSTEDPLAYERS.ISLAND_ID)
                .execute();

        // no primary key as there can be multiple instances
        ctx.createTable(Defencelocations.DEFENCELOCATIONS)
                .execute();

        // no primary key for same reason as above
        ctx.createTable(Auditlogs.AUDITLOGS)
                .execute();

        // no primary, same reason
        ctx.createTable(Factionbans.FACTIONBANS)
                .execute();

        // guess what? no primary, same reason
        ctx.createTable(Factioninvites.FACTIONINVITES)
                .execute();

        // aaand same as before!
        ctx.createTable(Notifications.NOTIFICATIONS)
                .execute();
    }

    public void closeConnection() {
        this.dataSource.close();
        ctx = null;
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
