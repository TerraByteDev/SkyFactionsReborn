package net.skullian.torrent.skyfactions.command.gems.cmds;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.config.Messages;
import org.bukkit.entity.Player;

import java.util.List;

public class GemsBalanceCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "balance";
    }

    @Override
    public String getDescription() {
        return "Get your gems balance.";
    }

    @Override
    public String getSyntax() {
        return "/gems balance";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;

        SkyFactionsReborn.db.getGems(player).thenAccept(count -> {
            Messages.GEMS_COUNT_MESSAGE.send(player, "%count%", count);
        }).exceptionally(ex -> {
            ex.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "get your gems balance", "%debug%", "SQL_GEMS_GET");
            return null;
        });
    }

    public static List<String> permissions = List.of("skyfactions.gems.balance", "skyfactions.gems", "skyfactions.player");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
