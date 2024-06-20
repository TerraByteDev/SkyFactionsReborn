package net.skullian.torrent.skyfactions.command.island.cmds;

import com.xxmicloxx.NoteBlockAPI.songplayer.NoteBlockSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import fr.skytasul.music.JukeBox;
import fr.skytasul.music.PlayerData;
import fr.skytasul.music.utils.JukeBoxRadio;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.island.IslandAPI;
import org.bukkit.entity.Player;
import org.kingdoms.constants.group.Kingdom;

public class IslandTest extends CommandTemplate {

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public String getDescription() {
        return "test";
    }

    @Override
    public String getSyntax() {
        return "/island test";
    }

    @Override
    public void perform(Player player, String[] args) {


        SkyFactionsReborn.db.registerPlayer(player).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });


    }

    @Override
    public String permission() {
        return "";
    }
}
