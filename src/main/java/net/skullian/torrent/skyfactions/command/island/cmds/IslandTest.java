package net.skullian.torrent.skyfactions.command.island.cmds;

import net.skullian.torrent.skyfactions.command.CommandTemplate;
import org.bukkit.entity.Player;

import java.util.List;

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

    }

    public static List<String> permissions = List.of("skyfactions.admin.superomegatemporaryadminpermission");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
