package net.skullian.skyfactions.command.gems.cmds;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CooldownHandler;
import net.skullian.skyfactions.command.PermissionsHandler;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.util.ErrorHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class GemsGiveCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "Gives other players gems (ADMIN COMMAND)";
    }

    @Override
    public String getSyntax() {
        return "/gems give <type> <Player name / Faction name> <amount>";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;
        if (args.length < 2) {
            Messages.INCORRECT_USAGE.send(player, "%usage%", getSyntax());
        } else {

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
            if (!offlinePlayer.hasPlayedBefore()) {
                Messages.UNKNOWN_PLAYER.send(player, "%player%", args[1]);
            } else {
                SkyFactionsReborn.databaseHandler.addGems(offlinePlayer.getPlayer(), Integer.parseInt(args[2])).whenComplete((ignored, exc) -> {
                    if (exc != null) {
                        ErrorHandler.handleError(player, "give someone gems", "SQL_GEMS_MODIFY", exc);
                        return;
                    }

                    Messages.GEM_ADD_SUCCESS.send(player, "%amount%", args[2], "%player%", offlinePlayer.getName());
                });
            }

        }
    }

    public static List<String> permissions = List.of("skyfactions.gems.give");

    @Override
    public List<String> permission() {
        return permissions;
    }
}