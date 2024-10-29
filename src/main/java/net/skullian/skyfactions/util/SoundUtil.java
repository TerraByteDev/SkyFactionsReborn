package net.skullian.skyfactions.util;

import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import fr.skytasul.music.JukeBox;
import fr.skytasul.music.PlayerData;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.config.types.Settings;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class SoundUtil {

    public static void playSound(Player player, String identifier, float pitch, float volume) {
        if (identifier == null || identifier.equalsIgnoreCase("NONE")) return;
        
        Sound sound = Sound.sound(Key.key(identifier), Sound.Source.MASTER, volume, pitch);
        if (sound == null) {
            SLogger.warn("Attempted to play a sound of {} when it returned null!", identifier);
            return;
        }

        player.playSound(sound, Sound.Emitter.self());
    }

    public static void playSound(Location location, String identifier, float pitch, float volume) {
        if (identifier == null || identifier.equalsIgnoreCase("NONE")) return;
        org.bukkit.Sound sound = org.bukkit.Sound.valueOf(identifier);

        if (sound == null) SLogger.warn("Attempted to play a sound of {} when it returned null!", identifier);
        else location.getWorld().playSound(location, sound, volume, pitch);
    }

    public static void playMusic(Player def, Player att) {

        if (DependencyHandler.enabledDeps.contains("JukeBox")) {
            PlayerData data = JukeBox.getInstance().datas.getDatas(def);
            PlayerData attData = JukeBox.getInstance().datas.getDatas(att);
            data.stopPlaying(true);
            attData.stopPlaying(true);
        }
        if (DependencyHandler.enabledDeps.contains("NoteBlockAPI")) {
            List<String> songs = Settings.RAIDING_MUSIC_FILES.getList();
            List<Song> nbsSongs = new ArrayList<>();

            for (String song : songs) {
                Song nbsSong = NBSDecoder.parse(new File(SkyFactionsReborn.getInstance().getDataFolder(), "/songs/" + song));
                if (nbsSong != null) {
                    nbsSongs.add(nbsSong);
                } else {
                    SLogger.fatal("Failed to find NBS Song [{}]!", song);
                }
            }

            Playlist playlist = new Playlist(nbsSongs.toArray(nbsSongs.toArray(new Song[0])));

            RadioSongPlayer radioSongPlayer = new RadioSongPlayer(playlist);
            radioSongPlayer.addPlayer(att);
            radioSongPlayer.addPlayer(def);

            radioSongPlayer.setPlaying(true);
        }
    }

    public static void soundAlarm(Player player) {
        CompletableFuture.runAsync(() -> {
            if (player != null && player.isOnline()) {
                String name = Settings.ALARM_SOUND.getString();
                float pitch = Settings.ALARM_SOUND_PITCH.getInt();
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
