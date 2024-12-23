package net.skullian.skyfactions.paper.api;

import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import fr.skytasul.music.JukeBox;
import fr.skytasul.music.PlayerData;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.api.SoundAPI;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.paper.util.DependencyHandler;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.common.util.SkyLocation;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import org.bukkit.Location;
import org.bukkit.Sound;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SpigotSoundAPI extends SoundAPI {
    @Override
    public void playSound(SkyLocation location, String identifier, float pitch, float volume) {
        Sound sound = Sound.valueOf(identifier);
        Location bukkitLocation = SpigotAdapter.adapt(location);

        bukkitLocation.getWorld().playSound(bukkitLocation, sound, volume, pitch);
    }

    @Override
    public void playMusic(SkyUser def, SkyUser att) {
        if (DependencyHandler.isEnabled("JukeBox")) {
            PlayerData playerData = JukeBox.getInstance().datas.getDatas(SpigotAdapter.adapt(def).getPlayer());
            PlayerData attackerData = JukeBox.getInstance().datas.getDatas(SpigotAdapter.adapt(att).getPlayer());

            playerData.stopPlaying(true);
            attackerData.stopPlaying(true);
        }

        if (DependencyHandler.isEnabled("NoteBlockAPI")) {
            List<String> songs = Settings.RAIDING_MUSIC_FILES.getList();
            List<Song> nbsSongs = new ArrayList<>();

            for (String song : songs) {
                Song nbsSong = NBSDecoder.parse(new File(SkyApi.getInstance().getFileAPI().getConfigFolderPath() + "/songs/" + song));
                if (nbsSong != null) nbsSongs.add(nbsSong);
                    else SLogger.fatal("Failed to decode NBS Song [{}]", song);
            }

            Playlist playlist = new Playlist(nbsSongs.toArray(new Song[0]));
            RadioSongPlayer radioSongPlayer = new RadioSongPlayer(playlist);
            radioSongPlayer.addPlayer(SpigotAdapter.adapt(att).getPlayer());
            radioSongPlayer.addPlayer(SpigotAdapter.adapt(def).getPlayer());

            radioSongPlayer.setPlaying(true);
        }
    }
}
