package net.skullian.torrent.skyfactions.util;

import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import fr.skytasul.music.JukeBox;
import fr.skytasul.music.PlayerData;
import lombok.extern.log4j.Log4j2;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Log4j2(topic = "SkyFactionsReborn")
public class SoundUtil {

    public static void playSound(Player player, String identifier, float pitch, float volume) {
        Sound sound = Sound.sound(Key.key(identifier), Sound.Source.MASTER, volume, pitch);
        player.playSound(sound, Sound.Emitter.self());
    }

    public static void playMusic(Player player) {
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

    public static void soundAlarm(Player player) {
        CompletableFuture.runAsync(() -> {
            if (player != null && player.isOnline()) {
                String name = SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getString("Raiding.ALARM_SOUND");
                float pitch = Float.parseFloat(SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getString("Raiding.ALARM_PITCH"));
                int dur = 100;
                int val = 50;
                int it = dur / val + 1;

                for (int i = 0; i < it; i++) {
                    playSound(player, name, pitch, 1f);
                    try {
                        Thread.sleep(val);
                    } catch (InterruptedException error) {
                        error.printStackTrace();
                    }
                }
            }
        });
    }
}
