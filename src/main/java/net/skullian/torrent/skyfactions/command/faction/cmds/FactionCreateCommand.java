package net.skullian.torrent.skyfactions.command.faction.cmds;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.api.FactionAPI;
import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.config.types.Messages;
import net.skullian.torrent.skyfactions.config.types.Settings;
import org.bukkit.entity.Player;

import java.util.List;

public class FactionCreateCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Create a new faction.";
    }

    @Override
    public String getSyntax() {
        return "/faction create <name>";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;

        if (args.length == 1) {
            Messages.INCORRECT_USAGE.send(player, "%usage%", getSyntax());
        } else if (args.length > 1) {
            Messages.FACTION_CREATION_PROCESSING.send(player);

            StringBuilder msg = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                msg.append(args[i]).append(" ");
            }
            String name = msg.toString().trim();

            if (FactionAPI.isInFaction(player)) {
                Messages.ALREADY_IN_FACTION.send(player);
            } else if (FactionAPI.getFaction(name) != null) {
                Messages.FACTION_CREATION_NAME_DUPLICATE.send(player);

            } else {
                if (FactionAPI.hasValidName(player, name)) {
                    int cost = Settings.FACTION_CREATION_COST.getInt();
                    if (cost > 0) {
                        if (!SkyFactionsReborn.ec.hasEnoughMoney(player, cost)) {
                            Messages.FACTION_INSUFFICIENT_FUNDS.send(player, "%creation_cost%", cost);
                            return;
                        }

                        SkyFactionsReborn.ec.economy.withdrawPlayer(player, cost);
                        ;
                    }

                    FactionAPI.createFaction(player, name);
                }
            }
        }
    }

    public static List<String> permissions = List.of("skyfactions.faction.create", "skyfactions.faction", "skyfactions.player");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
