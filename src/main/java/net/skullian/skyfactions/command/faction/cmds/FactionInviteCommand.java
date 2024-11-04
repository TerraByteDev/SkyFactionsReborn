package net.skullian.skyfactions.command.faction.cmds;

import java.util.List;
import java.util.stream.Collectors;

import net.skullian.skyfactions.event.PlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.database.struct.InviteData;
import net.skullian.skyfactions.util.ErrorUtil;

@Command("faction")
public class FactionInviteCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "invite";
    }

    @Override
    public String getDescription() {
        return "Invite a player to your Faction.";
    }

    @Override
    public String getSyntax() {
        return "/faction invite <player>";
    }

    @Suggestions("playerFactionName")
    public List<String> suggestPlayers(CommandContext<CommandSourceStack> context, CommandInput input) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }

    @Command("invite <player>")
    @Permission(value = { "skyfactions.faction.invite", "skyfactions.faction" }, mode = Permission.Mode.ANY_OF)
    public void perform(
            CommandSourceStack commandSourceStack,
            @Argument(value = "player", suggestions = "playerFactionName") String playerName
    ) {
        CommandSender sender = commandSourceStack.getSender();
        if (!(sender instanceof Player player)) return;
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        if (CommandsUtility.manageCooldown(player)) return;

        FactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            }

            if (faction == null) {
                Messages.NOT_IN_FACTION.send(player, PlayerHandler.getLocale(player.getUniqueId()));
                return;
            } else if (playerName.equalsIgnoreCase(player.getName())) {
                Messages.FACTION_INVITE_SELF_DENY.send(player, PlayerHandler.getLocale(player.getUniqueId()));
                return;
            }

                OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
                if (!target.hasPlayedBefore()) {
                    Messages.UNKNOWN_PLAYER.send(player, PlayerHandler.getLocale(player.getUniqueId()), "player", playerName);

                } else if (faction.getAllMembers().contains(target)) {
                    Messages.FACTION_INVITE_IN_SAME_FACTION.send(player, PlayerHandler.getLocale(player.getUniqueId()));

                } else {
                    faction.getOutgoingInvites().whenComplete((invites, throwable) -> {
                        if (throwable != null) {
                            ErrorUtil.handleError(player, "get Faction invites", "SQL_FACTION_GET", throwable);
                            return;
                        }

                        for (InviteData invite : invites) {
                            if (invite.getPlayer().getName().equalsIgnoreCase(target.getName())) {
                                Messages.FACTION_INVITE_DUPLICATE.send(player, PlayerHandler.getLocale(player.getUniqueId()));
                                return;
                            }
                        }
                    });

                    faction.createInvite(target, player);
                    Messages.FACTION_INVITE_CREATE_SUCCESS.send(player, PlayerHandler.getLocale(player.getUniqueId()), "player_name", target.getName());
                }
            });
    }

    public static List<String> permissions = List.of("skyfactions.faction.invite", "skyfactions.faction");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
