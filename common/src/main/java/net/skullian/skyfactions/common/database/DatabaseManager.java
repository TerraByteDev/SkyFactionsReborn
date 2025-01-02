package net.skullian.skyfactions.common.database;

import com.google.common.net.HostAndPort;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.database.impl.*;
import net.skullian.skyfactions.common.database.impl.faction.*;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.util.ErrorUtil;
import net.skullian.skyfactions.common.util.SLogger;
import org.flywaydb.core.Flyway;
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
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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

    private Executor databaseExecutor;

    public void initialise(String type) {
        this.databaseExecutor = Executors.newVirtualThreadPerTaskExecutor();
        createDataSource(SkyApi.getInstance().getFileAPI().getDatabasePath(), type);
    }

    private void createDataSource(
            @NotNull File file, @NotNull String type) {

        Configuration configuration = new DefaultConfiguration()
                .set(new DefaultExecuteListenerProvider(new DatabaseExecutionListener()));
        System.setProperty("org.jooq.no-tips", "true");
        System.setProperty("org.jooq.no-logo", "true");
        System.setProperty("net.skullian.codegen", "false");

        int maxPoolSize = Settings.DATABASE_MAX_POOL_SIZE.getInt();
        long maxLifetime = Settings.DATABASE_MAX_LIFETIME.getLong();

        if (type.equals("sqlite")) {

            HikariConfig sqliteConfig = new HikariConfig();

            this.url = JDBC.PREFIX + file.getAbsolutePath();

            Properties properties = new Properties();
            properties.putAll(Map.of(
                    "encoding", "UTF-8",
                    "enforceForeignKeys", "true",
                    "synchronous", "NORMAL",
                    "journalMode", "WAL"
            ));

            sqliteConfig.setDataSourceClassName("org.sqlite.SQLiteDataSource");
            sqliteConfig.setConnectionTestQuery("SELECT 1");
            sqliteConfig.addDataSourceProperty("url", JDBC.PREFIX + file.getAbsolutePath());
            sqliteConfig.setMaxLifetime(maxLifetime);
            sqliteConfig.setDataSourceProperties(properties);
            sqliteConfig.setPoolName("SkyFactions SQLite Pool");
            sqliteConfig.setMaximumPoolSize(maxPoolSize);

            HikariDataSource dataSource = new HikariDataSource(sqliteConfig);
            SLogger.info("Using SQLite Database.");

            configuration
                    .set(dataSource)
                    .set(SQLDialect.SQLITE);

            this.dataSource = dataSource;
            this.dialect = SQLDialect.SQLITE;
            this.ctx = DSL.using(configuration);
        } else if (List.of("mysql", "mariadb", "postgres").contains(type)) {

            String rawHost = Settings.DATABASE_HOST.getString();
            String databaseName = Settings.DATABASE_NAME.getString();
            String username = Settings.DATABASE_USERNAME.getString();
            String password = Settings.DATABASE_PASSWORD.getString();
            List<String> missingProperties = new ArrayList<>();

            if (rawHost == null || rawHost.isBlank()) {
                missingProperties.add("database-host");
            }

            if (databaseName == null || databaseName.isBlank()) {
                missingProperties.add("database-name");
            }

            if (username == null || username.isBlank()) {
                missingProperties.add("DATABASE_USERNAME");
            }

            if (password == null || password.isBlank()) {
                missingProperties.add("DATABASE_PASSWORD");
            }

            if (!missingProperties.isEmpty()) {
                throw new IllegalStateException("Missing Database Configuration Properties: " + missingProperties);
            }

            Properties properties = new Properties();
            properties.putAll(Map.of(
                    "prepStmtCacheSize", "250",
                    "useLocalSessionState", "true",
                    "useLocalTransactionState", "true",
                    "useServerPrepStmts", "true",
                    "cachePrepStmts", "true"
            ));
            properties.putAll(Map.of(
                    "rewriteBatchedStatements", "true",
                    "maintainTimeStats", "false",
                    "cacheResultSetMetadata", "true",
                    "cacheServerConfiguration", "true",
                    "elideSetAutoCommits", "true",
                    "prepStmtCacheSqlLimit", "2048"
            ));


            HostAndPort host = HostAndPort.fromString(rawHost);
            this.url = String.format("jdbc:%s://%s:%d/%s?useSSL=%s",
                    type.equalsIgnoreCase("mysql") ? "mysql" : "postgresql",
                    host.getHost(),
                    host.getPortOrDefault(3306),
                    databaseName,
                    Settings.DATABASE_USE_SSL.getBoolean()
            );

            HikariConfig mysqlConfig = new HikariConfig();
            mysqlConfig.setDataSourceProperties(properties);
            mysqlConfig.setPoolName("SkyFactions MySQL Pool");
            mysqlConfig.setJdbcUrl(url);
            mysqlConfig.setMaxLifetime(maxLifetime);
            mysqlConfig.setUsername(username);
            mysqlConfig.setPassword(password);
            mysqlConfig.setMaximumPoolSize(maxPoolSize);
            mysqlConfig.setMinimumIdle(maxPoolSize);
            mysqlConfig.setKeepaliveTime(0);
            mysqlConfig.setConnectionTimeout(5000);

            if (type.equalsIgnoreCase("postgres")) mysqlConfig.setDriverClassName("org.postgresql.Driver");

            SLogger.info("Using {} database '{}' on: {}:{}.",
                    type.equalsIgnoreCase("mysql") ? "MySQL" : "PostgreSQL", databaseName, host.getHost(), host.getPortOrDefault(3306));
            HikariDataSource dataSource = new HikariDataSource(mysqlConfig);

            configuration
                    .set(dataSource)
                    .set(SQLDialect.valueOf(type.toUpperCase(Locale.ROOT)));

            this.dataSource = dataSource;
            this.dialect = SQLDialect.valueOf(type.toUpperCase(Locale.ROOT));
            this.ctx = DSL.using(dataSource, SQLDialect.valueOf(type.toUpperCase(Locale.ROOT)));
        } else {
            ErrorUtil.handleDatabaseSetupError(new IllegalStateException("Unknown database type: " + type));
        }

        setup();
    }

    private void setup() {
        SLogger.info("Beginning database migrations.");

        Flyway flyway = Flyway.configure(this.getClass().getClassLoader())
                .locations("classpath:net/skullian/skyfactions/common/database/migrations").failOnMissingLocations(true).cleanDisabled(true)
                .dataSource(this.dataSource)
                .load();
        MigrateResult result = flyway.migrate();

        if (result.success) {
            SLogger.info("Database migrations complete: ({} Migrations completed in {}ms)", result.getSuccessfulMigrations().size(), result.getTotalMigrationTime());

            this.currencyManager = new CurrencyDatabaseManager(this.ctx, this.databaseExecutor);
            this.defencesManager = new DefencesDatabaseManager(this.ctx, this.databaseExecutor);
            this.notificationManager = new NotificationDatabaseManager(this.ctx, this.databaseExecutor);
            this.playerIslandManager = new PlayerIslandsDatabaseManager(this.ctx, this.databaseExecutor);
            this.playerManager = new PlayerDatabaseManager(this.ctx, this.databaseExecutor);

            this.factionAuditLogManager = new FactionAuditLogDatabaseManager(this.ctx, this.databaseExecutor);
            this.factionInvitesManager = new FactionInvitesDatabaseManager(this.ctx, this.databaseExecutor);
            this.factionIslandManager = new FactionIslandDatabaseManager(this.ctx, this.databaseExecutor);
            this.factionsManager = new FactionsDatabaseManager(this.ctx, this.databaseExecutor);
            this.electionManager = new FactionElectionManager(this.ctx, this.databaseExecutor);
        } else {
            ErrorUtil.handleDatabaseSetupError(new Exception("Failed to complete migrations - " + result.getFailedMigrations().size() + " Migrations failed: " + result.getException()));
            result.getFailedMigrations().forEach(migration -> SLogger.info("Migration failed: {}", migration.description));
        }
    }

    public void closeConnection() {
        this.dataSource.close();
        ctx = null;
    }
}
