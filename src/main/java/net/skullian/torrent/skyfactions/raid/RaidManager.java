package net.skullian.torrent.skyfactions.raid;

import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import fr.skytasul.music.JukeBox;
import fr.skytasul.music.PlayerData;
import lombok.extern.log4j.Log4j2;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.config.Messages;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Log4j2(topic = "SkyFactionsReborn")
public class RaidManager {

    public static Map<UUID, UUID> currentRaids = new HashMap<>();

    public static String getCooldownDuration(Player player) throws SQLException {
        try {
            long cooldownDurationInMilliseconds = SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getLong("Raiding.RAIDING_COOLDOWN");
            AtomicLong lastTime = new AtomicLong();
            SkyFactionsReborn.db.getLastRaid(player).thenAccept(time -> {
                lastTime.set(time);
            }).get();
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
        SkyFactionsReborn.db.updateLastRaid(player).thenAccept(result -> {
            SkyFactionsReborn.db.getGems(player).thenAccept(count -> {
                SkyFactionsReborn.db.subtractGems(player, count, SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getInt("Raiding.RAIDING_COST")).thenAccept(res -> {

                    playMusic(player);

                }).exceptionally(error -> {
                    error.printStackTrace();
                    Messages.ERROR.send(player, "%operation%", "start a raid");
                    return null;
                });
            }).exceptionally(error -> {
                error.printStackTrace();
                Messages.ERROR.send(player, "%operation%", "start a raid");
                return null;
            });
        }).exceptionally(ex -> {
            ex.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "start a raid");
            return null;
        });
    }

    private static void playMusic(Player player) {
        PlayerData data = JukeBox.getInstance().datas.getDatas(player);
        data.stopPlaying(true);


        List<String> songs = SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getStringList("Raiding.MUSIC_FILE_NAMES");
        List<Song> nbsSongs = new ArrayList<>();

        for (String song : songs) {
            Song nbsSong = NBSDecoder.parse(new File(SkyFactionsReborn.getInstance().getDataFolder(), "/songs/" + song));
            if (nbsSong != null) {
                nbsSongs.add(nbsSong);
            } else {
                LOGGER.error("Failed to find NBS Song [{}]!", song);
            }
        }

        Playlist playlist = new Playlist(nbsSongs.toArray(nbsSongs.toArray(new Song[0])));

        RadioSongPlayer radioSongPlayer = new RadioSongPlayer(playlist);
        radioSongPlayer.addPlayer(player);

        radioSongPlayer.setPlaying(true);
    }

    public static boolean hasEnoughGems(Player player) {
        try {
            int required = SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getInt("Raiding.RAIDING_COST");
            AtomicInteger currentGems = new AtomicInteger();
            SkyFactionsReborn.db.getGems(player).thenAccept(gemCount -> {
                currentGems.set(gemCount);
            }).exceptionally(ex -> {
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


}
