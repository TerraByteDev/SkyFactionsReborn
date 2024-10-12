package net.skullian.skyfactions.command.gems.cmds;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CooldownHandler;
import net.skullian.skyfactions.command.PermissionsHandler;
import net.skullian.skyfactions.config.types.Messages;
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

    public static List<String> permissions = List.of("skyfactions.gems.balance", "skyfactions.gems");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
