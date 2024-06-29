package net.skullian.torrent.skyfactions.db;

import com.google.common.net.HostAndPort;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.config.Settings;
import net.skullian.torrent.skyfactions.faction.Faction;
import net.skullian.torrent.skyfactions.island.FactionIsland;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Log4j2(topic = "SkyFactionsReborn")
public class HikariHandler {

    private transient HikariDataSource dataSource;
    public int cachedPlayerIslandID;
    public int cachedFactionIslandID;

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
                     [faction_name] BLOB NOT NULL,
                     [last_raided] INTEGER NOT NULL
                     );
                    """);

            PreparedStatement factionTable = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS factions(
                    [name] STRING UNIQUE NOT NULL,
                    [last_raid] INTEGER NOT NULL
                    );
                    """);

            PreparedStatement factionMemberTable = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS factionMembers(
                    [faction_name] STRING PRIMARY KEY NOT NULL,
                    [uuid] BLOB NOT NULL,
                    [rank] STRING NOT NULL
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

            factionTable.executeUpdate();
            factionTable.close();

            factionMemberTable.executeUpdate();
            factionMemberTable.close();

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

                statement.close();
                connection.close();

                return set.next();
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

    // ------------------ FACTIONS ------------------ //

    // TODO - Make all players / factions unraidable for CONFIGURABLE AMOUNT OF TIME!

    public CompletableFuture<Void> registerFaction(Player owner, String name) {
        return CompletableFuture.runAsync(() -> {
           try (Connection connection = dataSource.getConnection();
                PreparedStatement factionRegistration = connection.prepareStatement("INSERT INTO factions (name, last_raid) VALUES (?, ?)");
                PreparedStatement factionOwnerRegistration = connection.prepareStatement("INSERT INTO factionMembers (faction_name, uuid, rank) VALUES (?, ?, ?)")) {

               factionRegistration.setString(1, name);
               factionRegistration.setInt(2, 0);

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

    public CompletableFuture<Boolean> isInFaction(Player player) {
        return CompletableFuture.supplyAsync(() -> {
           try (Connection connection = dataSource.getConnection();
                PreparedStatement memberCheck = connection.prepareStatement("SELECT * FROM factionMembers WHERE uuid = ?")) {

               memberCheck.setString(1, player.getUniqueId().toString());
               ResultSet memberSet = memberCheck.executeQuery();

               memberCheck.close();
               connection.close();

               return memberSet.next();
           } catch (SQLException error) {
               handleError(error);
               throw new RuntimeException(error);
           }
        });
    }

    public CompletableFuture<Void> createFactionIsland(String name, FactionIsland island) {
        return CompletableFuture.runAsync(() -> {
           try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT INTO factionIslands (id, faction_name, last_raided) VALUES (?, ?, ?)")) {

               statement.setInt(1, island.getId());
               statement.setString(2, name);
               statement.setInt(3, island.getLast_raided());

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
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM factionIslands WHERE name = ?")) {

               statement.setString(1, name);
               ResultSet set = statement.executeQuery();

               if (set.next()) {
                   int id = set.getInt("id");
                   int last_raided = set.getInt("last_raided");

                   return new FactionIsland(id, last_raided);
               }

               set.close();
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
                   FactionIsland island = getFactionIsland(name).get();
                   int last_raid = set.getInt("last_raid");

                   return new Faction(island, name, last_raid);
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

    public CompletableFuture<Faction> getFaction(Player player) {
        return CompletableFuture.supplyAsync(() -> {
           try (Connection connection = dataSource.getConnection();
                PreparedStatement memberStatement = connection.prepareStatement("SELECT * FROM factionMembers WHERE uuid = ?")) {

               memberStatement.setString(1, player.getUniqueId().toString());

               ResultSet set = memberStatement.executeQuery();
               if (set.next()) {
                   String name = set.getString("name");

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

    // ------------------ MISC ------------------ //

    public void handleError(SQLException error) {
        Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> {
            LOGGER.fatal("----------------------- DATABASE EXCEPTION -----------------------");
            LOGGER.fatal("There was an error while performing database actions.");
            LOGGER.fatal("Please see https://docs.terrabytedev.com/skyfactions/errors-and-debugging for more information."); // TODO: LINK TO DEBUGGING DOCS
            LOGGER.fatal("Please contact the devs.");
            LOGGER.fatal("----------------------- DATABASE EXCEPTION -----------------------");
            error.printStackTrace();
        });
    }
}
