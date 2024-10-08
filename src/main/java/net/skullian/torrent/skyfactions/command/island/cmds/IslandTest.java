package net.skullian.torrent.skyfactions.command.island.cmds;

import net.skullian.torrent.skyfactions.command.CommandTemplate;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        ItemStack stack = player.getInventory().getItemInMainHand();
        ItemMeta meta = stack.getItemMeta();

        player.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).setBaseValue(0.2);

        stack.setItemMeta(meta);
    }

    public static List<String> permissions = List.of("skyfactions.admin.superomegatemporaryadminpermission");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
