package net.skullian.torrent.skyfactions.command.island.cmds;

import net.skullian.torrent.skyfactions.command.CommandTemplate;
import org.bukkit.attribute.Attribute;
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
        /*TextHologram hologram = new TextHologram(player.getName())
                .setText(TextUtility.color("aaa\naaaaaaa"))
                .setSeeThroughBlocks(true)
                .setBillboard(Display.Billboard.VERTICAL)
                .setShadow(true)
                .setViewRange(15)
                .setScale(1.0F, 1.0F, 1.0F);

        hologram.spawn(player.getLocation().add(0, 1, 0));
        DefenceHandler.hologramsMap.put(hologram.getId(), hologram);*/

        player.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).setBaseValue(1);
    }

    public static List<String> permissions = List.of("skyfactions.admin.superomegatemporaryadminpermission");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
