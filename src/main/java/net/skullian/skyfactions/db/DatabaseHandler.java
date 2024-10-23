package net.skullian.skyfactions.db;

import com.google.common.net.HostAndPort;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.faction.JoinRequestData;
import net.skullian.skyfactions.island.FactionIsland;
import net.skullian.skyfactions.island.PlayerIsland;
import net.skullian.skyfactions.notification.NotificationData;
import net.skullian.skyfactions.util.SLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.sqlite.JDBC;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class DatabaseHandler {

    private transient HikariDataSource dataSource;
    public int cachedPlayerIslandID;
    public int cachedFactionIslandID;

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void initialise(String type) throws SQLException {
        SLogger.info("Setting up Database.");

        createDataSource(new File(SkyFactionsReborn.getInstance().getDataFolder(), "/data/data.sqlite3"), type);
    }

    private void createDataSource(
            @NotNull File file, @NotNull String type) {
        try {
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

                dataSource = new HikariDataSource(sqliteConfig);

                SLogger.info("Using SQLite Database.");
                setupSQLiteTables();
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
                dataSource = new HikariDataSource(mysqlConfig);
                setupMySQLTables();
            } else {
                throw new IllegalStateException("Unknown database type: " + type);
            }
        } catch (SQLException error) {
            handleError(error);
            Bukkit.getPluginManager().disablePlugin(SkyFactionsReborn.getInstance());
        }
    }

    private void setupSQLiteTables() throws SQLException {
        SLogger.info("Registering SQL Tables.");

        try (Connection connection = dataSource.getConnection();
             PreparedStatement islandsTable = connection.prepareStatement("""
                     CREATE TABLE IF NOT EXISTS islands (
                     [id] INTEGER PRIMARY KEY,
                     [uuid] TEXT NOT NULL,
                     [level] INTEGER NOT NULL,
                     [gems] INTEGER NOT NULL,
                     [runes] INTEGER NOT NULL,
                     [defenceCount] INTEGER NOT NULL,
                     [last_raided] INTEGER NOT NULL,
                     [last_raider] TEXT NOT NULL
                     ) STRICT;
                     """);

             PreparedStatement playerDataTable = connection.prepareStatement("""
                     CREATE TABLE IF NOT EXISTS playerData (
                     [uuid] TEXT PRIMARY KEY UNIQUE NOT NULL,
                     [faction] TEXT NOT NULL,
                     [discord_id] TEXT NOT NULL,
                     [last_raid] INTEGER NOT NULL
                     ) STRICT;
                     """);

             PreparedStatement factionIslandTable = connection.prepareStatement("""
                      CREATE TABLE IF NOT EXISTS factionIslands (
                      [id] INTEGER PRIMARY KEY,
                      [factionName] TEXT NOT NULL,
                      [runes] INTEGER NOT NULL,
                      [defenceCount] INTEGER NOT NULL,
                      [gems] INTEGER NOT NULL,
                      [last_raided] INTEGER NOT NULL,
                      [last_raider] TEXT NOT NULL
                      ) STRICT;
                     """);

             PreparedStatement factionTable = connection.prepareStatement("""
                     CREATE TABLE IF NOT EXISTS factions(
                     [name] TEXT PRIMARY KEY UNIQUE NOT NULL,
                     [motd] TEXT NOT NULL,
                     [level] INTEGER NOT NULL,
                     [last_raid] INTEGER NOT NULL
                     ) STRICT;
                     """);

             PreparedStatement factionMemberTable = connection.prepareStatement("""
                     CREATE TABLE IF NOT EXISTS factionMembers (
                     [factionName] TEXT NOT NULL,
                     [uuid] TEXT PRIMARY KEY NOT NULL,
                     [rank] TEXT NOT NULL
                     ) STRICT;
                     """);

             PreparedStatement trustedPlayerTable = connection.prepareStatement("""
                     CREATE TABLE IF NOT EXISTS trustedPlayers (
                     [island_id] INTEGER PRIMARY KEY NOT NULL,
                     [uuid] TEXT NOT NULL
                     ) STRICT;
                     """);

             PreparedStatement defenceLocationsTable = connection.prepareStatement("""
                     CREATE TABLE IF NOT EXISTS defenceLocations (
                     [uuid] TEXT NOT NULL,
                     [type] TEXT NOT NULL,
                     [factionName] TEXT NOT NULL,
                     [x] INTEGER NOT NULL,
                     [y] INTEGER NOT NULL,
                     [z] INTEGER NOT NULL
                     ) STRICT;
                     """);

             PreparedStatement auditLogTable = connection.prepareStatement("""
                     CREATE TABLE IF NOT EXISTS auditLogs (
                     [factionName] TEXT NOT NULL,
                     [type] TEXT NOT NULL,
                     [uuid] TEXT NOT NULL,
                     [description] TEXT NOT NULL,
                     [timestamp] INTEGER NOT NULL
                     ) STRICT;
                     """);

             PreparedStatement factionBannedMembers = connection.prepareStatement("""
                     CREATE TABLE IF NOT EXISTS factionBans (
                     [factionName] TEXT NOT NULL,
                     [uuid] TEXT NOT NULL
                     ) STRICT;
                     """);

             PreparedStatement factionInvitesTable = connection.prepareStatement("""
                     CREATE TABLE IF NOT EXISTS factionInvites (
                     [factionName] TEXT NOT NULL,
                     [uuid] TEXT NOT NULL,
                     [inviter] TEXT NOT NULL,
                     [type] TEXT NOT NULL,
                     [accepted] INTEGER NOT NULL,
                     [timestamp] INTEGER NOT NULL
                     ) STRICT;
                     """);

             PreparedStatement notificationTable = connection.prepareStatement("""
                     CREATE TABLE IF NOT EXISTS notifications (
                     [uuid] TEXT NOT NULL,
                     [type] TEXT NOT NULL,
                     [description] TEXT NOT NULL,
                     [timestamp] INTEGER NOT NULL
                     ) STRICT;
                     """)) {

            islandsTable.executeUpdate();
            islandsTable.close();

            playerDataTable.executeUpdate();
            playerDataTable.close();

            factionIslandTable.executeUpdate();
            factionIslandTable.close();

            factionTable.executeUpdate();
            factionTable.close();

            factionMemberTable.executeUpdate();
            factionMemberTable.close();

            trustedPlayerTable.executeUpdate();
            trustedPlayerTable.close();

            defenceLocationsTable.executeUpdate();
            defenceLocationsTable.close();

            auditLogTable.executeUpdate();
            auditLogTable.close();

            factionBannedMembers.executeUpdate();
            factionBannedMembers.close();

            factionInvitesTable.executeUpdate();
            factionInvitesTable.close();

            notificationTable.executeUpdate();
            notificationTable.close();

            connection.close();
        }
    }

    private void setupMySQLTables() throws SQLException {
        SLogger.info("Registering SQL Tables.");

        try (Connection connection = dataSource.getConnection();
             PreparedStatement islandsTable = connection.prepareStatement("""
                     CREATE TABLE IF NOT EXISTS islands (
                     `id` BIGINT PRIMARY KEY,
                     `uuid` VARCHAR(255) NOT NULL,
                     `level` BIGINT NOT NULL,
                     `gems` BIGINT NOT NULL,
                     `runes` BIGINT NOT NULL,
                     `defenceCount` BIGINT NOT NULL,
                     `last_raided` BIGINT NOT NULL,
                     `last_raider` VARCHAR(255) NOT NULL
                     );
                     """);

             PreparedStatement playerDataTable = connection.prepareStatement("""
                     CREATE TABLE IF NOT EXISTS playerData (
                     `uuid` VARCHAR(255) PRIMARY KEY NOT NULL,
                     `faction` VARCHAR(255) NOT NULL,
                     `discord_id` VARCHAR(255) NOT NULL,
                     `last_raid` BIGINT NOT NULL
                     );
                     """);

             PreparedStatement factionIslandTable = connection.prepareStatement("""
                      CREATE TABLE IF NOT EXISTS factionIslands (
                      `id` BIGINT PRIMARY KEY,
                      `factionName` VARCHAR(255) NOT NULL,
                      `runes` BIGINT NOT NULL,
                      `defenceCount` BIGINT NOT NULL,
                      `gems` BIGINT NOT NULL,
                      `last_raided` BIGINT NOT NULL,
                      `last_raider` VARCHAR(255) NOT NULL
                      );
                     """);

             PreparedStatement factionTable = connection.prepareStatement("""
                     CREATE TABLE IF NOT EXISTS factions(
                     `name` VARCHAR(255) PRIMARY KEY UNIQUE NOT NULL,
                     `motd` VARCHAR(255) NOT NULL,
                     `level` BIGINT NOT NULL,
                     `last_raid` BIGINT NOT NULL
                     );
                     """);

             PreparedStatement factionMemberTable = connection.prepareStatement("""
                     CREATE TABLE IF NOT EXISTS factionMembers (
                     `factionName` VARCHAR(255) NOT NULL,
                     `uuid` VARCHAR(255) PRIMARY KEY NOT NULL,
                     `rank` VARCHAR(255) NOT NULL
                     );
                     """);

             PreparedStatement trustedPlayerTable = connection.prepareStatement("""
                     CREATE TABLE IF NOT EXISTS trustedPlayers (
                     `island_id` BIGINT PRIMARY KEY NOT NULL,
                     `uuid` VARCHAR(255) NOT NULL
                     );
                     """);

             PreparedStatement defenceLocationsTable = connection.prepareStatement("""
                     CREATE TABLE IF NOT EXISTS defenceLocations (
                     `uuid` BIGINT NOT NULL,
                     `type` VARCHAR(255) NOT NULL,
                     `factionName` VARCHAR(255) NOT NULL,
                     `x` BIGINT NOT NULL,
                     `y` BIGINT NOT NULL,
                     `z` BIGINT NOT NULL
                     );
                     """);

             PreparedStatement auditLogTable = connection.prepareStatement("""
                     CREATE TABLE IF NOT EXISTS auditLogs (
                     `factionName` VARCHAR(255) NOT NULL,
                     `type` VARCHAR(255) NOT NULL,
                     `uuid` VARCHAR(255) NOT NULL,
                     `description` VARCHAR(255) NOT NULL,
                     `timestamp` BIGINT NOT NULL
                     );
                     """);

             PreparedStatement factionBannedMembers = connection.prepareStatement("""
                     CREATE TABLE IF NOT EXISTS factionBans (
                     `factionName` VARCHAR(255) NOT NULL,
                     `uuid` VARCHAR(255) NOT NULL
                     );
                     """);

             PreparedStatement factionInvitesTable = connection.prepareStatement("""
                     CREATE TABLE IF NOT EXISTS factionInvites (
                     `factionName` VARCHAR(255) NOT NULL,
                     `uuid` VARCHAR(255) NOT NULL,
                     `inviter` VARCHAR(255) NOT NULL,
                     `type` VARCHAR(255) NOT NULL,
                     `accepted` BIGINT NOT NULL,
                     `timestamp` BIGINT NOT NULL
                     );
                     """);

             PreparedStatement notificationTable = connection.prepareStatement("""
                     CREATE TABLE IF NOT EXISTS notifications (
                     `uuid` VARCHAR(255) NOT NULL,
                     `type` VARCHAR(255) NOT NULL,
                     `description` VARCHAR(255) NOT NULL,
                     `timestamp` BIGINT NOT NULL
                     );
                     """)) {

            islandsTable.executeUpdate();
            islandsTable.close();

            playerDataTable.executeUpdate();
            playerDataTable.close();

            factionIslandTable.executeUpdate();
            factionIslandTable.close();

            factionTable.executeUpdate();
            factionTable.close();

            factionMemberTable.executeUpdate();
            factionMemberTable.close();

            trustedPlayerTable.executeUpdate();
            trustedPlayerTable.close();

            defenceLocationsTable.executeUpdate();
            defenceLocationsTable.close();

            auditLogTable.executeUpdate();
            auditLogTable.close();

            factionBannedMembers.executeUpdate();
            factionBannedMembers.close();

            factionInvitesTable.executeUpdate();
            factionInvitesTable.close();

            notificationTable.executeUpdate();
            notificationTable.close();

            connection.close();
        }
    }

    public void closeConnection() throws SQLException {
        SLogger.info("Disabling Database.");
        dataSource.close();
        SLogger.info("Database closed.");
    }

    // ------------------ ISLAND ------------------ //

    public CompletableFuture<Boolean> hasIsland(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM islands WHERE uuid = ?")) {

                statement.setString(1, playerUUID.toString());
                ResultSet set = statement.executeQuery();
                if (set.next()) {
                    return true;
                }

                statement.close();
                connection.close();

                return false;
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> createIsland(Player player, PlayerIsland island) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO islands (id, uuid, level, gems, runes, defenceCount, last_raided, last_raider) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

                statement.setInt(1, island.getId());
                statement.setString(2, player.getUniqueId().toString());
                statement.setInt(3, 0);
                statement.setInt(4, 0);
                statement.setInt(5, 0);
                statement.setInt(6, 0);
                statement.setLong(7, System.currentTimeMillis() + Settings.RAIDING_PLAYER_IMMUNITY.getInt());
                statement.setString(8, "N/A");

                statement.executeUpdate();
                statement.close();

                SLogger.info("Finished Creating Island");

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<PlayerIsland> getPlayerIsland(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM islands where uuid = ?")) {

                statement.setString(1, playerUUID.toString());
                ResultSet set = statement.executeQuery();

                if (set.next()) {
                    int id = set.getInt("id");
                    return new PlayerIsland(id);
                }

                statement.close();
                connection.close();

                return null;
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> setIslandCachedNextID() {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM islands WHERE id = (SELECT MAX(id) FROM islands);")) {

                ResultSet set = statement.executeQuery();
                if (set.next()) {
                    int id = set.getInt("id");
                    this.cachedPlayerIslandID = (id + 1);
                } else {
                    this.cachedPlayerIslandID = 1;
                }

                statement.close();
                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> setFactionCachedNextID() {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM factionIslands WHERE id = (SELECT MAX(id) FROM factionIslands);")) {

                ResultSet set = statement.executeQuery();
                if (set.next()) {
                    int id = set.getInt("id");
                    this.cachedFactionIslandID = (id + 1);
                } else {
                    this.cachedFactionIslandID = 1;
                }

                statement.close();
                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Integer> getIslandLevel(PlayerIsland island) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM islands WHERE id = ?")) {

                statement.setInt(1, island.getId());
                ResultSet set = statement.executeQuery();

                if (set.next()) {
                    int level = set.getInt("level");

                    return level;
                }

                statement.close();
                connection.close();

                return 0;
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> upgradeIslandLevel(PlayerIsland island) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE islands SET level = level + 1 WHERE id = ?")) {

                statement.setInt(1, island.getId());

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> setIslandCooldown(PlayerIsland island, long time) {
        return CompletableFuture.runAsync(() -> {
            try {
                try (Connection connection = dataSource.getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE islands set last_raided = ? WHERE id = ?")) {

                    statement.setLong(1, time);
                    statement.setInt(2, island.getId());

                    statement.executeUpdate();
                    statement.close();

                    connection.close();
                }
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> removeIsland(Player player) {
        SLogger.info("Removing island from SQL");
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM islands WHERE uuid = ?")) {

                statement.setString(1, player.getUniqueId().toString());

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    // ------------------ PLAYER DATA ------------------ //

    public CompletableFuture<Boolean> playerIsRegistered(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM playerData WHERE uuid = ?")) {

                statement.setString(1, player.getUniqueId().toString());

                ResultSet set = statement.executeQuery();
                if (set.next()) {
                    return true;
                }

                statement.close();
                connection.close();

                return false;
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> registerPlayer(Player player) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO playerData (uuid, faction, discord_id, last_raid) VALUES (?, ?, ?, ?);")) {

                statement.setString(1, player.getUniqueId().toString());
                statement.setString(2, "none");
                statement.setString(3, "none");
                statement.setInt(4, 0);

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> registerDiscordLink(UUID uuid, String discordID) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE playerData set discord_id = ? WHERE uuid = ?")) {

                statement.setString(1, discordID);
                statement.setString(2, uuid.toString());

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<String> getDiscordLink(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM playerData WHERE uuid = ?")) {

                statement.setString(1, player.getUniqueId().toString());
                ResultSet set = statement.executeQuery();

                if (set.next()) {
                    String id = set.getString("discord_id");
                    if (id.equals("none")) {
                        return null;
                    }
                    return id;
                }

                statement.close();
                connection.close();
                return null;
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Long> getLastRaid(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM playerData WHERE uuid = ?")) {

                statement.setString(1, player.getUniqueId().toString());

                ResultSet set = statement.executeQuery();
                if (set.next()) {
                    long lastRaid = set.getLong("last_raid");

                    return lastRaid;
                }

                statement.close();
                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
            return 0L;
        });
    }

    public CompletableFuture<Void> updateLastRaid(Player player, long time) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE playerData set last_raid = ? WHERE uuid = ?")) {

                statement.setLong(1, time);
                statement.setString(2, player.getUniqueId().toString());

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<List<IslandRaidData>> getRaidablePlayers(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM islands WHERE last_raided <=?")) {

                statement.setLong(1, System.currentTimeMillis() - Settings.RAIDED_COOLDOWN.getInt());

                List<IslandRaidData> islands = new ArrayList<>();
                ResultSet set = statement.executeQuery();

                while (set.next()) {
                    int id = set.getInt("id");
                    String uuid = set.getString("uuid");
                    //if (player.getUniqueId().toString().equals(uuid)) continue;
                    int last_raided = set.getInt("last_raided");

                    islands.add(new IslandRaidData(id, uuid, last_raided));
                }

                statement.close();
                connection.close();

                return islands;
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    // ------------------ CURRENCY ------------------ //

    public CompletableFuture<Integer> getGems(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM islands WHERE uuid = ?")) {

                statement.setString(1, playerUUID.toString());

                ResultSet set = statement.executeQuery();
                if (set.next()) {
                    int gems = set.getInt("gems");
                    return gems;
                }

                statement.close();
                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
            return 0;
        });
    }

    public CompletableFuture<Void> subtractGems(UUID playerUUID, int amount) {
        return CompletableFuture.runAsync(() -> {
            int current = getGems(playerUUID).join();
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE islands set gems = ? WHERE uuid = ?")) {

                statement.setInt(1, (current - amount));
                statement.setString(2, playerUUID.toString());

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> addGems(UUID playerUUID, int amount) {
        return CompletableFuture.runAsync(() -> {
            int currentCount = getGems(playerUUID).join();
            int newCount = currentCount + amount;

            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE islands set gems = ? WHERE uuid = ?")) {

                statement.setInt(1, newCount);
                statement.setString(2, playerUUID.toString());

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    // ------------------ TRUSTING ------------------ //

    public CompletableFuture<Boolean> isPlayerTrusted(UUID playerUUID, int id) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM trustedPlayers WHERE island_id = ? AND uuid = ?")) {

                statement.setInt(1, id);
                statement.setString(2, playerUUID.toString());

                ResultSet set = statement.executeQuery();
                if (set.next()) {
                    return true;
                }

                statement.close();
                connection.close();

                return false;
            } catch (SQLException error) {
                handleError(error);
            }

            return false;
        });
    }

    public CompletableFuture<Void> trustPlayer(UUID playerUUID, int id) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO trustedPlayers (island_id, uuid) VALUES (?, ?)")) {

                statement.setInt(1, id);
                statement.setString(2, playerUUID.toString());

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
            }
        });
    }

    public CompletableFuture<Void> removeTrust(UUID playerUUID, int id) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM trustedPlayers WHERE island_id = ? AND uuid = ?")) {

                statement.setInt(1, id);
                statement.setString(2, playerUUID.toString());

                statement.executeUpdate();

                statement.close();
                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> removeAllTrustedPlayers(int islandID) {
        SLogger.info("Removing trusted players");
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM trustedPlayers WHERE island_id = ?")) {

                statement.setInt(1, islandID);

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<List<OfflinePlayer>> getTrustedPlayers(int islandID) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM trustedPlayers WHERE island_id = ?")) {

                statement.setInt(1, islandID);
                ResultSet set = statement.executeQuery();

                List<OfflinePlayer> players = new ArrayList<>();
                while (set.next()) {
                    UUID uuid = UUID.fromString(set.getString("uuid"));
                    OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

                    if (player.hasPlayedBefore()) {
                        players.add(player);
                    }
                }

                statement.close();
                connection.close();

                return players;
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    // ------------------ FACTIONS ------------------ //

    public CompletableFuture<Void> registerFaction(Player owner, String name) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement factionRegistration = connection.prepareStatement("INSERT INTO factions (name, motd, level, last_raid) VALUES (?, ?, ?, ?)");
                 PreparedStatement factionOwnerRegistration = connection.prepareStatement("INSERT INTO factionMembers (factionName, uuid, rank) VALUES (?, ?, ?)")) {

                factionRegistration.setString(1, name);
                factionRegistration.setString(2, "&aNone");
                factionRegistration.setInt(3, 1);
                factionRegistration.setLong(4, 0);

                factionOwnerRegistration.setString(1, name);
                factionOwnerRegistration.setString(2, owner.getUniqueId().toString());

                factionOwnerRegistration.setString(3, "owner");
                factionOwnerRegistration.executeUpdate();
                factionOwnerRegistration.close();

                factionRegistration.executeUpdate();
                factionRegistration.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> addFactionMember(UUID playerUUID, String factionName) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO factionMembers (factionName, uuid, rank) VALUES (?, ?, ?)")) {

                statement.setString(1, factionName);
                statement.setString(2, playerUUID.toString());
                statement.setString(3, "member");

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Boolean> isInFaction(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement memberCheck = connection.prepareStatement("SELECT * FROM factionMembers WHERE uuid = ?")) {

                memberCheck.setString(1, player.getUniqueId().toString());
                ResultSet memberSet = memberCheck.executeQuery();
                if (memberSet.next()) {
                    return true;
                }

                memberCheck.close();
                connection.close();

                return false;
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> createFactionIsland(String name, FactionIsland island) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO factionIslands (id, factionName, runes, defenceCount, gems, last_raided, last_raider) VALUES (?, ?, ?, ?, ?, ?, ?)")) {

                statement.setInt(1, island.getId());
                statement.setString(2, name);
                statement.setInt(3, 0);
                statement.setInt(4, 0);
                statement.setInt(5, 0);
                statement.setLong(6, island.getLast_raided());
                statement.setString(7, "N/A");

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<FactionIsland> getFactionIsland(String name) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM factionIslands WHERE factionName = ?")) {

                statement.setString(1, name);
                ResultSet set = statement.executeQuery();

                if (set.next()) {
                    int id = set.getInt("id");
                    int last_raided = set.getInt("last_raided");

                    return new FactionIsland(id, last_raided);
                }

                statement.close();
                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }

            return null;
        });
    }

    public CompletableFuture<Faction> getFaction(String name) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM factions WHERE name = ?")) {

                statement.setString(1, name);
                ResultSet set = statement.executeQuery();

                if (set.next()) {
                    int last_raid = set.getInt("last_raid");
                    int level = set.getInt("level");
                    statement.close();
                    connection.close();

                    FactionIsland island = getFactionIsland(name).get();

                    return new Faction(
                            island,
                            name,
                            last_raid,
                            level,
                            getFactionOwner(name).join(),
                            getMembersByRank(name, "admin").join(),
                            getMembersByRank(name, "moderator").join(),
                            getMembersByRank(name, "fighter").join(),
                            getMembersByRank(name, "member").join(),
                            getMOTD(name).join(),
                            getRunes(name).join(),
                            getGems(name).join()
                    );
                }

                statement.close();
                connection.close();
            } catch (SQLException | ExecutionException | InterruptedException error) {
                if (error instanceof SQLException) {
                    handleError((SQLException) error);
                } else {
                    error.printStackTrace();
                    throw new RuntimeException(error);
                }
            }

            return null;
        });
    }

    public CompletableFuture<Faction> getFaction(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement memberStatement = connection.prepareStatement("SELECT * FROM factionMembers WHERE uuid = ?")) {

                memberStatement.setString(1, playerUUID.toString());

                ResultSet set = memberStatement.executeQuery();

                if (set.next()) {
                    String name = set.getString("factionName");

                    memberStatement.close();
                    connection.close();

                    return getFaction(name).join();
                }

                memberStatement.close();
                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }

            return null;
        });
    }

    public CompletableFuture<Integer> getGems(String name) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM factionIslands WHERE factionName = ?")) {

                statement.setString(1, name);
                ResultSet set = statement.executeQuery();

                if (set.next()) {
                    int gems = set.getInt("gems");

                    return gems;
                }
                statement.close();
                connection.close();

                return 0;
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> addGems(String name, int addition) {
        return CompletableFuture.runAsync(() -> {
            int gemCount = getGems(name).join();
            int newCount = gemCount + addition;
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE factionIslands SET gems = ? WHERE factionName = ?")) {

                statement.setInt(1, newCount);
                statement.setString(2, name);

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> updateFactionName(String name, String original_name) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE factions set name WHERE name = ?")) {

                statement.setString(1, name);
                statement.setString(2, original_name);
                statement.executeUpdate();

                statement.close();
                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> leaveFaction(String name, OfflinePlayer player) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM factionMembers WHERE factionName = ? AND uuid = ?")) {

                statement.setString(1, name);
                statement.setString(2, player.getUniqueId().toString());

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<OfflinePlayer> getFactionOwner(String name) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM factionMembers WHERE factionName = ? AND rank = 'owner'")) {

                statement.setString(1, name);
                ResultSet set = statement.executeQuery();

                if (set.next()) {
                    UUID uuid = UUID.fromString(set.getString("uuid"));

                    return Bukkit.getOfflinePlayer(uuid);
                }

                statement.close();
                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }

            return null;
        });
    }

    public CompletableFuture<List<OfflinePlayer>> getMembersByRank(String name, String rank) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM factionMembers WHERE factionName = ? AND rank = ?")) {

                statement.setString(1, name);
                statement.setString(2, rank);
                ResultSet set = statement.executeQuery();

                List<OfflinePlayer> players = new ArrayList<>();
                if (set.next()) {
                    UUID uuid = UUID.fromString(set.getString("uuid"));
                    OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

                    if (player.hasPlayedBefore()) {
                        players.add(player);
                    }
                }

                statement.close();
                connection.close();

                return players;
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<String> updateMemberRank(String factionName, Player player, String rank) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement oldRankStatement = connection.prepareStatement("SELECT * FROM factionMembers WHERE factionName = ? AND uuid = ?");
                 PreparedStatement statement = connection.prepareStatement("UPDATE factionMembers SET rank = ? WHERE factionName = ? AND uuid = ?")) {

                oldRankStatement.setString(1, factionName);
                oldRankStatement.setString(2, player.getUniqueId().toString());

                statement.setString(1, rank);
                statement.setString(2, factionName);
                statement.setString(3, player.getUniqueId().toString());

                statement.executeUpdate();
                ResultSet set = oldRankStatement.executeQuery();
                if (set.next()) {
                    return set.getString("rank");
                }

                statement.close();
                oldRankStatement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }

            return null;
        });
    }

    public CompletableFuture<String> getMOTD(String name) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM factions WHERE name = ?")) {

                statement.setString(1, name);
                ResultSet set = statement.executeQuery();

                if (set.next()) {
                    String MOTD = set.getString("motd");

                    return MOTD;
                }

                statement.close();
                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }

            return "&aNone";
        });
    }

    public CompletableFuture<Void> setMOTD(String factionName, String MOTD) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE factions SET motd = ? WHERE name = ?")) {

                statement.setString(1, MOTD);
                statement.setString(2, factionName);

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    // ------------------ RUNES  ------------------ //

    public CompletableFuture<Integer> getRunes(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM islands WHERE uuid = ?")) {

                statement.setString(1, playerUUID.toString());
                ResultSet set = statement.executeQuery();

                if (set.next()) {
                    return set.getInt("runes");
                }

                statement.close();
                connection.close();

                return 0;
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Integer> getRunes(String factionName) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM factionIslands WHERE factionName = ?")) {

                statement.setString(1, factionName);
                ResultSet set = statement.executeQuery();

                if (set.next()) {
                    return set.getInt("runes");
                }

                statement.close();
                connection.close();

                return 0;
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> addRunes(UUID playerUUID, int addition) {
        return CompletableFuture.runAsync(() -> {
            int currentRunes = getRunes(playerUUID).join();
            int newCount = currentRunes + addition;
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE islands SET runes = ? WHERE uuid = ?")) {

                statement.setInt(1, newCount);
                statement.setString(2, playerUUID.toString());

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> addRunes(String factionName, int addition) {
        return CompletableFuture.runAsync(() -> {
            int currentRunes = getRunes(factionName).join();
            int newCount = currentRunes + addition;

            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE factionIslands SET runes = ? WHERE factionName = ?")) {

                statement.setInt(1, newCount);
                statement.setString(2, factionName);

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> removeRunes(UUID playerUUID, int removal) {
        return CompletableFuture.runAsync(() -> {
            int currentRunes = getRunes(playerUUID).join();
            int newCount = currentRunes - removal;
            if (newCount < 0) throw new IllegalArgumentException("Attempted to remove more runes than the player had!");

            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE islands SET runes = ? WHERE uuid = ?")) {

                statement.setInt(1, newCount);
                statement.setString(2, playerUUID.toString());

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> removeRunes(String factionName, int removal) {
        return CompletableFuture.runAsync(() -> {
            int currentRunes = getRunes(factionName).join();
            int newCount = currentRunes - removal;
            if (newCount < 0)
                throw new IllegalArgumentException("Attempted to remove more runes than the Faction had!");

            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE factionIslands SET runes = ? WHERE factionName = ?")) {

                statement.setInt(1, newCount);
                statement.setString(2, factionName);

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    // ------------------ FACTION ADMINISTRATION ------------------ //

    public CompletableFuture<Void> kickPlayer(OfflinePlayer player, String factionName) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE * FROM factionMembers WHERE uuid = ? AND factionName = ?")) {

                statement.setString(1, player.getUniqueId().toString());
                statement.setString(2, factionName);

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> banPlayer(String factionName, OfflinePlayer player) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement kickStatement = connection.prepareStatement("DELETE * FROM factionMembers WHERE uuid = ? AND factionName = ?");
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO factionBans (factionName, uuid) VALUES (?, ?);")) {

                kickStatement.setString(1, player.getUniqueId().toString());
                kickStatement.setString(2, factionName);

                statement.setString(1, factionName);
                statement.setString(2, player.getUniqueId().toString());

                kickStatement.executeUpdate();
                kickStatement.close();

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<List<OfflinePlayer>> getBannedPlayers(String factionName) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM factionBans WHERE factionName = ?")) {

                statement.setString(1, factionName);
                ResultSet set = statement.executeQuery();

                List<OfflinePlayer> players = new ArrayList<>();
                while (set.next()) {
                    UUID uuid = UUID.fromString(set.getString("uuid"));
                    OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                    if (player.hasPlayedBefore()) {
                        players.add(player);
                    }
                }

                statement.close();
                connection.close();

                return players;
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Boolean> isPlayerBanned(String factionName, OfflinePlayer player) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM factionBans WHERE factionName = ? AND uuid = ?")) {

                statement.setString(1, factionName);
                statement.setString(2, player.getUniqueId().toString());

                ResultSet set = statement.executeQuery();
                if (set.next()) {
                    return true;
                }

                statement.close();
                connection.close();

                return false;
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> unbanPlayer(String factionName, OfflinePlayer player) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM factionBans WHERE factionName = ? AND uuid = ?")) {

                statement.setString(1, factionName);
                statement.setString(2, player.getUniqueId().toString());

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    // ------------------ FACTION AUDIT LOGGING ------------------ //

    public CompletableFuture<Void> createAuditLog(UUID playerUUID, String type, String description, String factionName) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO auditLogs (factionName, type, uuid, description, timestamp) VALUES (?, ?, ?, ?, ?);")) {

                statement.setString(1, factionName);
                statement.setString(2, type);
                statement.setString(3, playerUUID.toString());
                statement.setString(4, description);
                statement.setLong(5, System.currentTimeMillis());

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<List<AuditLogData>> getAuditLogs(String factionName) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM auditLogs WHERE factionName = ? ORDER BY timestamp DESC")) {

                statement.setString(1, factionName);
                ResultSet set = statement.executeQuery();

                List<AuditLogData> data = new ArrayList<>();
                while (set.next()) {
                    UUID uuid = UUID.fromString(set.getString("uuid"));
                    String type = set.getString("type");
                    String description = set.getString("description");
                    long timestamp = set.getLong("timestamp");

                    data.add(new AuditLogData(Bukkit.getOfflinePlayer(uuid), factionName, type, description, timestamp));
                }

                return data;
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    // ------------------ FACTION INVITES ------------------ //

    public CompletableFuture<Void> createInvite(UUID invitedPlayerUUID, String factionName, String type, Player inviter) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO factionInvites (factionName, uuid, inviter, type, accepted, timestamp) VALUES (?, ?, ?, ?, ?, ?);")) {

                statement.setString(1, factionName);
                statement.setString(2, invitedPlayerUUID.toString());
                if (inviter != null) {
                    statement.setString(3, inviter.getUniqueId().toString());
                } else {
                    statement.setString(3, "");
                }
                statement.setString(4, type);
                statement.setBoolean(5, false);
                statement.setLong(6, System.currentTimeMillis());

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Boolean> hasJoinRequest(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM factionInvites WHERE uuid = ? AND type = 'incoming'")) {

                statement.setString(1, player.getUniqueId().toString());

                ResultSet set = statement.executeQuery();
                if (set.next()) {
                    return true;
                }

                statement.close();
                connection.close();

                return false;
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<JoinRequestData> getPlayerOutgoingJoinRequest(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM factionInvites where uuid = ? AND type = 'incoming'")) {

                statement.setString(1, player.getUniqueId().toString());
                ResultSet set = statement.executeQuery();

                if (set.next()) {
                    String factionName = set.getString("factionName");
                    long timestamp = set.getLong("timestamp");
                    boolean accepted = set.getBoolean("accepted");

                    return new JoinRequestData(factionName, accepted, timestamp);
                }

                statement.close();
                connection.close();

                return null;
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Boolean> joinRequestExists(String factionName, Player player) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM factionInvites WHERE factionName = ? AND uuid = ? AND type = 'incoming'")) {

                statement.setString(1, factionName);
                statement.setString(2, player.getUniqueId().toString());

                ResultSet set = statement.executeQuery();
                if (set.next()) {
                    return true;
                }

                statement.close();
                connection.close();

                return false;
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<List<InviteData>> getInvitesOfType(String factionName, String type) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM factionInvites WHERE factionName = ? AND type = ? ORDER BY timestamp DESC")) {

                statement.setString(1, factionName);
                statement.setString(2, type);

                ResultSet set = statement.executeQuery();
                List<InviteData> data = new ArrayList<>();
                while (set.next()) {

                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(set.getString("uuid")));
                    long timestamp = set.getLong("timestamp");
                    String uuid = set.getString("inviter");
                    OfflinePlayer inviter = !uuid.isEmpty() ? Bukkit.getOfflinePlayer(UUID.fromString(uuid)) : null;

                    data.add(new InviteData(offlinePlayer, inviter, factionName, type, timestamp));
                }

                statement.close();
                connection.close();

                return data;
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<List<InviteData>> getInvitesOfPlayer(OfflinePlayer player) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM factionInvites WHERE uuid = ? AND type = 'outgoing' ORDER BY timestamp DESC")) {

                statement.setString(1, player.getUniqueId().toString());

                ResultSet set = statement.executeQuery();
                List<InviteData> data = new ArrayList<>();
                while (set.next()) {
                    String factionName = set.getString("factionName");
                    long timestamp = set.getLong("timestamp");
                    String uuid = set.getString("inviter");
                    OfflinePlayer inviter = !uuid.isEmpty() ? Bukkit.getOfflinePlayer(UUID.fromString(uuid)) : null;

                    data.add(new InviteData(player, inviter, factionName, "outgoing", timestamp));
                }

                statement.close();
                connection.close();

                return data;
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> acceptJoinRequest(String factionName, UUID playerUUID) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE factionInvites SET accepted = true WHERE factionName = ? AND uuid = ? AND type = 'incoming'")) {

                statement.setString(1, factionName);
                statement.setString(2, playerUUID.toString());

                statement.executeUpdate();
                statement.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> revokeInvite(String factionName, UUID playerUUID, String type) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM factionInvites WHERE factionName = ? AND uuid = ? AND type = ?")) {

                statement.setString(1, factionName);
                statement.setString(2, playerUUID.toString());
                statement.setString(3, type);

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    // ------------------ NOTIFICATIONS ------------------ //

    public CompletableFuture<Void> createNotification(UUID playerUUID, String type, String description) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO notifications (uuid, type, description, timestamp) VALUES (?, ?, ?, ?)")) {

                statement.setString(1, playerUUID.toString());
                statement.setString(2, type);
                statement.setString(3, description);
                statement.setLong(4, System.currentTimeMillis());

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> removeNotification(OfflinePlayer player, NotificationData data) {
        return CompletableFuture.runAsync(() -> {
            try {
                try (Connection connection = dataSource.getConnection();
                     PreparedStatement statement = connection.prepareStatement("DELETE FROM notifications WHERE uuid = ? AND timestamp = ?")) {

                    statement.setString(1, player.getUniqueId().toString());
                    statement.setLong(2, data.getTimestamp());

                    statement.executeUpdate();
                    statement.close();

                    connection.close();
                }
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<List<NotificationData>> getNotifications(OfflinePlayer player) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM notifications WHERE uuid = ? ORDER BY timestamp DESC")) {

                statement.setString(1, player.getUniqueId().toString());
                ResultSet set = statement.executeQuery();

                List<NotificationData> data = new ArrayList<>();
                while (set.next()) {
                    String type = set.getString("type");
                    String desc = set.getString("description");
                    long timestamp = set.getLong("timestamp");

                    data.add(new NotificationData(player.getUniqueId(), type, desc, timestamp));
                }

                statement.close();
                connection.close();

                return data;
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    // ------------------ DEFENCES ------------------ //

    public CompletableFuture<List<Location>> getDefenceLocations(String factionName) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM defenceLocations WHERE type = 'faction' AND factionName = ?")) {

                List<Location> locs = new ArrayList<>();
                World world = Bukkit.getWorld(Settings.ISLAND_FACTION_WORLD.getString());
                if (world == null)
                    throw new RuntimeException("Could not find configured world: " + Settings.ISLAND_FACTION_WORLD.getString());

                statement.setString(1, factionName);
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    int x = set.getInt("x");
                    int y = set.getInt("y");
                    int z = set.getInt("z");

                    locs.add(new Location(world, x, y, z));
                }

                statement.close();
                connection.close();

                return locs;
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<List<Location>> getDefenceLocations(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM defenceLocations WHERE type = 'player' AND uuid = ?")) {

                List<Location> locs = new ArrayList<>();
                World world = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
                if (world == null)
                    throw new RuntimeException("Could not find configured world: " + Settings.ISLAND_PLAYER_WORLD.getString());

                statement.setString(1, playerUUID.toString());
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    int x = set.getInt("x");
                    int y = set.getInt("y");
                    int z = set.getInt("z");

                    locs.add(new Location(world, x, y, z));
                }

                statement.close();
                connection.close();

                return locs;
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> registerDefenceLocation(String factionName, Location location) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO defenceLocations(uuid, type, factionName, x, y, z) VALUES (?, ?, ?, ?, ?, ?)")) {

                statement.setString(1, "N/A");
                statement.setString(2, "faction");
                statement.setString(3, factionName);
                statement.setInt(4, location.getBlockX());
                statement.setInt(5, location.getBlockY());
                statement.setInt(6, location.getBlockZ());

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> registerDefenceLocations(String factionName, List<Location> locations) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO defenceLocations(uuid, type, factionName, x, y, z) VALUES (?, ?, ?, ?, ?, ?)")) {

                connection.setAutoCommit(false);
                for (Location location : locations) {
                    statement.setString(1, "N/A");
                    statement.setString(2, "faction");
                    statement.setString(3, factionName);
                    statement.setInt(4, location.getBlockX());
                    statement.setInt(5, location.getBlockY());
                    statement.setInt(6, location.getBlockZ());

                    statement.addBatch();
                }

                statement.executeBatch();
                statement.close();

                connection.commit();
                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> registerDefenceLocations(UUID playerUUID, List<Location> locations) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO defenceLocations(uuid, type, factionName, x, y, z) VALUES (?, ?, ?, ?, ?, ?)")) {

                connection.setAutoCommit(false);
                for (Location location : locations) {
                    statement.setString(1, playerUUID.toString());
                    statement.setString(2, "player");
                    statement.setString(3, "N/A");
                    statement.setInt(4, location.getBlockX());
                    statement.setInt(5, location.getBlockY());
                    statement.setInt(6, location.getBlockZ());

                    statement.addBatch();
                }

                statement.executeBatch();
                statement.close();

                connection.commit();
                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> registerDefenceLocation(UUID playerUUID, Location location) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO defenceLocations(uuid, type, factionName, x, y, z) VALUES (?, ?, ?, ?, ?, ?)")) {

                statement.setString(1, playerUUID.toString());
                statement.setString(2, "player");
                statement.setString(3, "N/A");
                statement.setInt(4, location.getBlockX());
                statement.setInt(5, location.getBlockY());
                statement.setInt(6, location.getBlockZ());

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> removeDefence(UUID playerUUID, Location location) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM defenceLocations WHERE uuid = ? AND type = 'player' and x = ? and y = ? and z = ?")) {

                statement.setString(1, playerUUID.toString());
                statement.setInt(2, location.getBlockX());
                statement.setInt(3, location.getBlockY());
                statement.setInt(4, location.getBlockZ());

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> removeDefence(String factionName, Location location) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM defenceLocations WHERE factionName = ? AND type = 'faction' and x = ? and y = ? and z = ?")) {

                statement.setString(1, factionName);
                statement.setInt(2, location.getBlockX());
                statement.setInt(3, location.getBlockY());
                statement.setInt(4, location.getBlockZ());

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> removeDefences(String factionName, List<Location> locations) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM defenceLocations WHERE factionName = ? AND type = 'faction' and x = ? and y = ? and z = ?")) {

                connection.setAutoCommit(false);
                for (Location location : locations) {
                    statement.setString(1, factionName);
                    statement.setInt(2, location.getBlockX());
                    statement.setInt(3, location.getBlockY());
                    statement.setInt(4, location.getBlockZ());

                    statement.addBatch();
                }

                statement.executeBatch();
                statement.close();

                connection.commit();
                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> removeDefences(UUID playerUUID, List<Location> locations) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM defenceLocations WHERE uuid = ? AND type = 'player' and x = ? and y = ? and z = ?")) {

                connection.setAutoCommit(false);
                for (Location location : locations) {
                    statement.setString(1, playerUUID.toString());
                    statement.setInt(2, location.getBlockX());
                    statement.setInt(3, location.getBlockY());
                    statement.setInt(4, location.getBlockZ());

                    statement.addBatch();
                }

                statement.executeBatch();
                statement.close();

                connection.commit();
                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> removeAllDefences(String factionName) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM defenceLocations WHERE factionName = ? AND type = 'faction'")) {

                statement.setString(1, factionName);

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<Void> removeAllDefences(UUID playerUUID) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM defenceLocations WHERE uuid = ? AND type = 'player'")) {

                statement.setString(1, playerUUID.toString());

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }


    // ------------------ MISC ------------------ //

    public void handleError(SQLException error) {
        Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> {
            SLogger.fatal("----------------------- DATABASE EXCEPTION -----------------------");
            SLogger.fatal("There was an error while performing database actions:");
            SLogger.fatal(error.getMessage());
            SLogger.fatal("Please see https://docs.terrabytedev.com/skyfactions/errors-and-debugging for more information.");
            SLogger.fatal("Please contact the devs.");
            SLogger.fatal("----------------------- DATABASE EXCEPTION -----------------------");
        });
    }

    public boolean isActive() {
        return !this.dataSource.isClosed();
    }
}
