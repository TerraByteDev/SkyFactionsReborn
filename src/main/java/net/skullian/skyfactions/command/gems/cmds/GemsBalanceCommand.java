package net.skullian.skyfactions.command.gems.cmds;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.gems.GemsCommandHandler;
import net.skullian.skyfactions.config.types.Messages;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("gems")
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

    @Command("balance")
    @Permission(value = { "skyfactions.gems.balance", "skyfactions.gems" }, mode = Permission.Mode.ANY_OF)
    public void perform(
            CommandSourceStack commandSourceStack
    ) {
        CommandSender sender = commandSourceStack.getSender();
        if (!(sender instanceof Player player)) return;
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        if (CommandsUtility.manageCooldown(player)) return;

        SkyFactionsReborn.databaseHandler.getGems(player.getUniqueId()).thenAccept(count -> {
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
