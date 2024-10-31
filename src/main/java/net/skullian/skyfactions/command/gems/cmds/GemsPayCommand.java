package net.skullian.skyfactions.command.gems.cmds;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.api.GemsAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;

@Command("gems")
public class GemsPayCommand extends CommandTemplate {

    @Override
    public String getName() {
        return "pay";
    }

    @Override
    public String getDescription() {
        return "Give some of your gems to other players.";
    }

    @Override
    public String getSyntax() {
        return "/gems pay <player> <amount>";
    }

    @Command("pay <player> <amount>")
    @Permission(value = { "skyfactions.gems.pay", "skyfactions.gems" }, mode = Permission.Mode.ANY_OF)
    public void perform(
            CommandSourceStack commandSourceStack,
            @Argument(value = "player") String playerName,
            @Argument(value = "amount") int amount
    ) {
        CommandSender sender = commandSourceStack.getSender();
        if (!(sender instanceof Player player)) return;
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        if (CommandsUtility.manageCooldown(player)) return;

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        if (!offlinePlayer.hasPlayedBefore()) {
            Messages.UNKNOWN_PLAYER.send(sender, player.locale().getLanguage(), "%player%", playerName);
        } else {

            int playerGemCount = GemsAPI.getGems(player.getUniqueId());
            if (playerGemCount >= amount) {
                GemsAPI.subtractGems(player.getUniqueId(), amount);
                GemsAPI.addGems(offlinePlayer.getUniqueId(), amount);

                Messages.GEM_ADD_SUCCESS.send(player, player.locale().getLanguage(), "%amount%", amount, "%player%", offlinePlayer.getName());
            } else {
                Messages.INSUFFICIENT_GEMS_COUNT.send(player, player.locale().getLanguage());
            }
        }
    }

    public static List<String> permissions = List.of("skyfactions.gems.pay", "skyfactions.gems");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
