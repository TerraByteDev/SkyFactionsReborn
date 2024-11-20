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
import org.flywaydb.core.api.migration.Context;
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
    }

    private void createDataSource(
            @NotNull File file, @NotNull String type) {

        Configuration configuration = new DefaultConfiguration().set(new DefaultExecuteListenerProvider(new DatabaseExecutionListener()));
        System.setProperty("org.jooq.no-tips", "true");
        System.setProperty("org.jooq.no-logo", "true");
        System.setProperty("net.skullian.codegen", "false");

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

        ctx.createTableIfNotExists(Islands.ISLANDS)
                .columns(Islands.ISLANDS.fields())
                .primaryKey(Islands.ISLANDS.ID)
                .execute();

        ctx.createTableIfNotExists(Playerdata.PLAYERDATA)
                .columns(Playerdata.PLAYERDATA.fields())
                .primaryKey(Playerdata.PLAYERDATA.UUID)
                .execute();

        ctx.createTableIfNotExists(Factionislands.FACTIONISLANDS)
                .columns(Factionislands.FACTIONISLANDS.fields())
                .primaryKey(Factionislands.FACTIONISLANDS.ID)
                .execute();

        ctx.createTableIfNotExists(Factions.FACTIONS)
                .columns(Factions.FACTIONS.fields())
                .primaryKey(Factions.FACTIONS.NAME)
                .execute();

        ctx.createTableIfNotExists(Factionmembers.FACTIONMEMBERS)
                .columns(Factionmembers.FACTIONMEMBERS.fields())
                .primaryKey(Factionmembers.FACTIONMEMBERS.UUID)
                .execute();

        ctx.createTableIfNotExists(Trustedplayers.TRUSTEDPLAYERS)
                .columns(Trustedplayers.TRUSTEDPLAYERS.fields())
                .primaryKey(Trustedplayers.TRUSTEDPLAYERS.ISLAND_ID)
                .execute();

        // no primary key as there can be multiple instances
        ctx.createTableIfNotExists(Defencelocations.DEFENCELOCATIONS)
                .columns(Defencelocations.DEFENCELOCATIONS.fields())
                .execute();

        // no primary key for same reason as above
        ctx.createTableIfNotExists(Auditlogs.AUDITLOGS)
                .columns(Auditlogs.AUDITLOGS.fields())
                .execute();

        // no primary, same reason
        ctx.createTableIfNotExists(Factionbans.FACTIONBANS)
                .columns(Factionbans.FACTIONBANS.fields())
                .execute();

        // guess what? no primary, same reason
        ctx.createTableIfNotExists(Factioninvites.FACTIONINVITES)
                .columns(Factioninvites.FACTIONINVITES.fields())
                .execute();

        // aaand same as before!
        ctx.createTableIfNotExists(Notifications.NOTIFICATIONS)
                .columns(Notifications.NOTIFICATIONS.fields())
                .execute();

        ctx.createTableIfNotExists(Factionelections.FACTIONELECTIONS)
                .columns(Factionelections.FACTIONELECTIONS.fields())
                .primaryKey(Factionelections.FACTIONELECTIONS.ID)
                .execute();
    }

    public static DSLContext getCtx(Context context) {
        boolean isCodegen = (System.getProperty("net.skullian.codegen").equals("true"));
        Configuration configuration = new DefaultConfiguration()
                .set(isCodegen ? SQLDialect.SQLITE : SkyFactionsReborn.getDatabaseManager().getDialect());
        if (isCodegen) configuration.set(SkyFactionsReborn.getDatabaseManager().getDataSource());
        else configuration.set(context.getConnection());

        return DSL.using(configuration);
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
