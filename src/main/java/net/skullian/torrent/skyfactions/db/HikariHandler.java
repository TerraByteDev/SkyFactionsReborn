package net.skullian.torrent.skyfactions.db;

import com.google.common.net.HostAndPort;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.config.Settings;
import net.skullian.torrent.skyfactions.island.PlayerIsland;
import org.bukkit.Bukkit;
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
import java.util.concurrent.TimeUnit;

@Log4j2(topic = "SkyFactionsReborn")
public class HikariHandler {

    private transient HikariDataSource dataSource;
    public int cachedNextID;

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void initialise(String type) throws SQLException {
        LOGGER.info("Setting up Database.");

        createDataSource(new File(SkyFactionsReborn.getInstance().getDataFolder(), "/data/data.sqlite3"), type);
        setupTables();
    }

    private void createDataSource(
            @NotNull File file, @NotNull String type) {
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

            LOGGER.info("Using SQLite Database.");
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

            LOGGER.info("Using MySQL database '{}' on: {}:{}.",
                    databaseName, host.getHost(), host.getPortOrDefault(3306));
            dataSource = new HikariDataSource(mysqlConfig);
        } else {
            throw new IllegalStateException("Unknown database type: " + type);
        }
    }

    private void setupTables() throws SQLException {
        LOGGER.info("Registering SQL Tables.");

        try (Connection connection = dataSource.getConnection();
             PreparedStatement islandsTable = connection.prepareStatement("""
                     CREATE TABLE IF NOT EXISTS islands (
                     [id] INTEGER PRIMARY KEY,
                     [uuid] BLOB NOT NULL,
                     [last_raided] INTEGER NOT NULL
                     );
                     """);

            PreparedStatement playerDataTable = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS playerData (
                    [uuid] BLOB PRIMARY KEY UNIQUE NOT NULL,
                    [faction] STRING NOT NULL,
                    [gems] INTEGER NOT NULL,
                    [discord_id] STRING NOT NULL,
                    [last_raid] INTEGER NOT NULL
                    );
                    """);

            PreparedStatement factionIslandTable = connection.prepareStatement("""
                     CREATE TABLE IF NOT EXISTS factionIslands (
                     [id] INTEGER PRIMARY KEY,
                     [uuid] BLOB NOT NULL,
                     [last_raided] INTEGER NOT NULL
                     );
                    """);

            PreparedStatement trustedPlayerTable = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS trustedPlayers (
                    [island_id] INTEGER PRIMARY KEY NOT NULL,
                    [uuid] BLOB NOT NULL
                    );
                    """)) {

            islandsTable.executeUpdate();
            islandsTable.close();

            playerDataTable.executeUpdate();
            playerDataTable.close();

            factionIslandTable.executeUpdate();
            factionIslandTable.close();

            trustedPlayerTable.executeUpdate();
            trustedPlayerTable.close();

            connection.close();
        }
    }

    public void closeConnection() throws SQLException {
        LOGGER.info("Disabling Database.");
        dataSource.close();
        LOGGER.info("Database closed.");
    }

    // ------------------ ISLAND ------------------ //

    public CompletableFuture<Boolean> hasIsland(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM islands WHERE uuid = ?")) {

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

    public CompletableFuture<Void> createIsland(Player player, PlayerIsland island) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO islands (id, uuid, last_raided) VALUES (?, ?, ?)")) {

                statement.setInt(1, island.getId());
                statement.setString(2, player.getUniqueId().toString());
                statement.setInt(3, 0);

                statement.executeUpdate();
                statement.close();

                connection.close();
            } catch (SQLException error) {
                handleError(error);
                throw new RuntimeException(error);
            }
        });
    }

    public CompletableFuture<PlayerIsland> getPlayerIsland(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM islands where uuid = ?")) {

                statement.setString(1, player.getUniqueId().toString());
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

    public CompletableFuture<Void> setCachedNextID() {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM islands WHERE id = (SELECT MAX(id) FROM islands);")) {

                ResultSet set = statement.executeQuery();
                if (set.next()) {
                    int id = set.getInt("id");
                    this.cachedNextID = (id + 1);
                } else {
                    this.cachedNextID = 1;
                }

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
        return CompletableFuture.runAsync(() -> {
            try {
                try (Connection connection = dataSource.getConnection();
                    PreparedStatement statement = connection.prepareStatement("DELETE FROM islands WHERE uuid = ?")) {
                    statement.setString(1, player.getUniqueId().toString());

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
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO playerData (uuid, faction, gems, discord_id, last_raid) VALUES (?, ?, ?, ?, ?);")) {

                statement.setString(1, player.getUniqueId().toString());
                statement.setString(2, "none");
                statement.setInt(3, 0);
                statement.setString(4, "none");
                statement.setInt(5, 0);

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

    public CompletableFuture<Integer> getGems(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM playerData WHERE uuid = ?")) {

                statement.setString(1, player.getUniqueId().toString());

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

    public CompletableFuture<Void> subtractGems(Player player, int current, int amount) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE playerData set gems = ? WHERE uuid = ?")) {

                statement.setInt(1, (current - amount));
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

    public CompletableFuture<Void> addGems(Player player, int current, int amount) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE playerData set gems = ? WHERE uuid = ?")) {

                statement.setInt(1, (current + amount));
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

    // ------------------ TRUSTING ------------------ //

    public CompletableFuture<Boolean> isPlayerTrusted(Player player, int id) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM trustedPlayers WHERE island_id = ? AND uuid = ?")) {

                statement.setInt(1, id);
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
            }

            return false;
        });
    }

    public CompletableFuture<Void> trustPlayer(Player player, int id) {
        return CompletableFuture.runAsync(() -> {
           try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT INTO trustedPlayers (island_id, uuid) VALUES (?, ?)")) {

               statement.setInt(1, id);
               statement.setString(2, player.getUniqueId().toString());

               statement.executeUpdate();
               statement.close();

               connection.close();
           } catch (SQLException error) {
               handleError(error);
           }
        });
    }

    public CompletableFuture<Void> removeTrust(Player player, int id) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("DELETE FROM trustedPlayers WHERE island_id = ? AND uuid = ?")) {

                statement.setInt(1, id);
                statement.setString(2, player.getUniqueId().toString());

                statement.executeUpdate();

                statement.close();
                connection.close();
            } catch (SQLException error) {
                handleError(error);
            }
        });
    }

    // ------------------ MISC ------------------ //

    public void handleError(SQLException error) {
        Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> {
            LOGGER.fatal("----------------------- DATABASE EXCEPTION -----------------------");
            LOGGER.fatal("There was an error while performing database actions.");
            LOGGER.fatal(""); // TODO: LINK TO DEBUGGING DOCS
            LOGGER.fatal("Please contact the devs.");
            LOGGER.fatal("----------------------- DATABASE EXCEPTION -----------------------");
            error.printStackTrace();
        });
    }
}
