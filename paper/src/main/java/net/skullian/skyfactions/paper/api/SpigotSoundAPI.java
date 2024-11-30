package net.skullian.skyfactions.paper.api;

import fr.skytasul.music.JukeBox;
import fr.skytasul.music.PlayerData;
import net.skullian.skyfactions.common.api.SoundAPI;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.paper.util.DependencyHandler;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.common.util.SkyLocation;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import org.bukkit.Sound;

import java.util.List;

public class SpigotSoundAPI extends SoundAPI {
    @Override
    public void playSound(SkyLocation location, String identifier, float pitch, float volume) {
        Sound sound = Sound.valueOf(identifier);
        if (sound == null) SLogger.warn("Attempted to play a sound of {} when it returned null!", identifier);
        else SpigotAdapter.adapt(location).playSound(location, sound, volume, pitch);
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
            List<String> s
        }
    }
}
