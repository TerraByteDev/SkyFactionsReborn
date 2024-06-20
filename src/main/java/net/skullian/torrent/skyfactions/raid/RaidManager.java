package net.skullian.torrent.skyfactions.raid;

import lombok.extern.log4j.Log4j2;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.db.IslandRaidData;
import net.skullian.torrent.skyfactions.island.IslandAPI;
import net.skullian.torrent.skyfactions.island.SkyIsland;
import net.skullian.torrent.skyfactions.util.SoundUtil;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2(topic = "SkyFactionsReborn")
public class RaidManager {

    // attacker, victim
    public static Map<UUID, UUID> currentRaids = new HashMap<>();

    public static String getCooldownDuration(Player player) throws SQLException {
        try {
            long cooldownDurationInMilliseconds = SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getLong("Raiding.RAIDING_COOLDOWN");
            AtomicLong lastTime = new AtomicLong();
            SkyFactionsReborn.db.getLastRaid(player).thenAccept(lastTime::set).get();
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastTime.get() >= cooldownDurationInMilliseconds) {
                return null;
            } else {
                long cooldownDuration = cooldownDurationInMilliseconds - (currentTime - lastTime.get());
                return DurationFormatUtils.formatDuration(cooldownDuration, "HH'h 'mm'm 'ss's'");
            }
        } catch (ExecutionException | InterruptedException error) {
            error.printStackTrace();
            return "ERROR";
        }
    }

    public static void startRaid(Player player) {
        try {
            Messages.RAID_PROCESSING.send(player);
            SkyFactionsReborn.db.updateLastRaid(player, System.currentTimeMillis()).thenAccept(result -> SkyFactionsReborn.db.getGems(player).thenAccept(count -> SkyFactionsReborn.db.subtractGems(player, count, SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getInt("Raiding.RAIDING_COST")).thenAccept(res -> SoundUtil.playMusic(player)).exceptionally(error -> {
                error.printStackTrace();
                Messages.ERROR.send(player, "%operation%", "start a raid");
                return null;
            })).exceptionally(error -> {
                error.printStackTrace();
                Messages.ERROR.send(player, "%operation%", "start a raid");
                return null;
            })).get();

            IslandRaidData island = getRandomRaidable(player);
            if (island != null) {
                UUID playerUUID = UUID.fromString(island.getUuid());
                IslandAPI.saveIslandSchematic(Objects.requireNonNull(Bukkit.getOfflinePlayer(playerUUID).getPlayer()), new SkyIsland(island.getId())).exceptionally(error -> {
                    error.printStackTrace();
                    return null;
                });
                handleRaidedPlayer(player, playerUUID);
                currentRaids.put(player.getUniqueId(), playerUUID);
            }

        } catch (InterruptedException | ExecutionException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "start a raid");
        }
    }

    private static void handleRaidedPlayer(Player attacker, UUID uuid) {
        if (!isPlayerOnline(uuid)) {
            SkyFactionsReborn.dc.pingRaid(attacker, Bukkit.getOfflinePlayer(uuid).getPlayer());
        } else {
            alertPlayer(uuid, attacker);
            teleportToPreparationArea(uuid);

            Bukkit.getScheduler().runTaskLater(SkyFactionsReborn.getInstance(), () -> {
                Player player = Bukkit.getPlayer(uuid);
                if (!player.isOnline()) return;
                SkyFactionsReborn.db.getPlayerIsland(player).thenAccept(island -> {
                    World islandWorld = Bukkit.getWorld(SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getString("Island.ISLAND_WORLD_NAME"));
                    if (islandWorld != null && island != null) {
                        Location returnLoc = island.getCenter(islandWorld);
                        player.teleport(returnLoc);
                    }
                });

            }, (SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getInt("Raiding.RAID_PREPARATION_TIME") * 20L));
            showCountdown(uuid, attacker);
        }
    }

    public static boolean hasEnoughGems(Player player) {
        try {
            int required = SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getInt("Raiding.RAIDING_COST");
            AtomicInteger currentGems = new AtomicInteger();
            SkyFactionsReborn.db.getGems(player).thenAccept(currentGems::set).exceptionally(ex -> {
                ex.printStackTrace();
                Messages.ERROR.send(player, "%operation%", "check your gem count");
                return null;
            }).get();

            if (currentGems.get() < required) {
                Messages.RAID_INSUFFICIENT_GEMS.send(player, "%raid_cost%", required);
                return false;
            } else {
                return true;
            }
        } catch (InterruptedException | ExecutionException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "start a raid");
        }

        return false;
    }

    public static IslandRaidData getRandomRaidable(Player player) {
        try {
            AtomicReference<List<IslandRaidData>> data = new AtomicReference<>(new ArrayList<>());
            SkyFactionsReborn.db.getRaidablePlayers(player).thenAccept(data::set).exceptionally(ex -> {
                ex.printStackTrace();
                Messages.ERROR.send(player, "%operation%", "start a raid");
                return null;
            }).get();

            if (data.get().isEmpty()) {
                Messages.RAID_NO_PLAYERS.send(player);
            } else {
                Random random = new Random();
                return data.get().get(random.nextInt(data.get().size()));
            }
        } catch (InterruptedException | ExecutionException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "start a raid");
        }
        return null;
    }

    private static boolean isPlayerOnline(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        return player.isOnline();
    }

    private static void alertPlayer(UUID uuid, Player attacker) {
        SoundUtil.soundAlarm(uuid);
        List<String> alertList = SkyFactionsReborn.configHandler.MESSAGES_CONFIG.getStringList("Messages.Raiding.RAIDED_NOTIFICATION");
        Player player = Bukkit.getPlayer(uuid);
        if (player.isOnline()) {
            for (String msg : alertList) {
                player.sendMessage(TextUtility.color(msg.replace("%player_name%", player.getName()).replace("%raider%", attacker.getName())));
            }
        }
    }

    private static void teleportToPreparationArea(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player.isOnline()) {
            List<Integer> loc = SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getIntegerList("Raiding.RAID_PREPARATION_POS");
            World world = Bukkit.getWorld(SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getString("Raiding.RAID_PREPARATION_WORLD"));
            if (world != null) {
                Location location = new Location(world, loc.get(0), loc.get(1), loc.get(2));
                player.teleport(location);
            }
        }
    }

    private static void showCountdown(UUID def, Player att) {
        Player defp = Bukkit.getPlayer(def);
        int length = SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getInt("Raiding.COUNTDOWN_DURATION");
        String countdown_sound = SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getString("Raiding.COUNTDOWN_SOUND");
        int countdown_pitch = SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getInt("Raiding.COUNTDOWN_PITCH");
        final Component subtitle = Component.text(TextUtility.color(SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getString("Raiding.COUNTDOWN_SUBTITLE")));

        for (int i = 0; i < length; i++) {
            final Component mainTitle = Component.text(i + 1, NamedTextColor.RED);

            final Title title = Title.title(mainTitle, subtitle);
            defp.showTitle(title);
            att.showTitle(title);

            SoundUtil.playSound(defp, countdown_sound, countdown_pitch, 1f);
            SoundUtil.playSound(att, countdown_sound, countdown_pitch, 1f);
        }

    }
}
