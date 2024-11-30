package net.skullian.skyfactions.paper.command.faction.cmds;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.paper.api.SpigotFactionAPI;
import net.skullian.skyfactions.paper.command.CommandTemplate;
import net.skullian.skyfactions.paper.command.CommandsUtility;
import net.skullian.skyfactions.paper.config.types.Messages;
import net.skullian.skyfactions.paper.config.types.Settings;
import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.paper.api.SpigotPlayerAPI;
import net.skullian.skyfactions.paper.util.ErrorUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;

import java.util.List;
import java.util.stream.Collectors;

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

    @Command("invite <target>")
    @Permission(value = {"skyfactions.faction.invite", "skyfactions.faction"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            Player player,
            @Argument(value = "target", suggestions = "playerFactionName") String playerName
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        String locale = SpigotPlayerAPI.getLocale(player.getUniqueId());

        SpigotFactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            }

            if (faction == null) {
                Messages.NOT_IN_FACTION.send(player, locale);
                return;
            } else if (!Settings.FACTION_MANAGE_INVITES_PERMISSIONS.getList().contains(faction.getRankType(player.getUniqueId()).getRankValue())) {
                Messages.PERMISSION_DENY.send(player, locale);
                return;
            } else if (playerName.equalsIgnoreCase(player.getName())) {
                Messages.FACTION_INVITE_SELF_DENY.send(player, locale);
                return;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
            SpigotPlayerAPI.isPlayerRegistered(target.getUniqueId()).whenComplete((isRegistered, ex2) -> {
                if (ex2 != null) {
                    ErrorUtil.handleError(player, "check if that player is registered", "SQL_PLAYER_GET", ex2);
                } else if (!isRegistered) {
                    Messages.UNKNOWN_PLAYER.send(player, locale, "player", playerName);
                }else if (faction.getAllMembers().contains(target)) {
                    Messages.FACTION_INVITE_IN_SAME_FACTION.send(player, locale);
                } else if (faction.isPlayerBanned(target)) {
                    Messages.FACTION_INVITE_PLAYER_BANNED.send(player, locale);
                } else {

                    List<InviteData> invites = faction.getOutgoingInvites();
                    for (InviteData invite : invites) {
                        if (invite.getPlayer().getName().equalsIgnoreCase(target.getName())) {
                            Messages.FACTION_INVITE_DUPLICATE.send(player, locale);
                            return;
                        }
                    }

                    InviteData newInvite = new InviteData(
                            target,
                            player,
                            faction.getName(),
                            "outgoing",
                            System.currentTimeMillis()
                    );
                    faction.createInvite(newInvite);
                    Messages.FACTION_INVITE_CREATE_SUCCESS.send(player, locale, "player_name", target.getName());
                }
            });
        });
    }

    public static List<String> permissions = List.of("skyfactions.faction.invite", "skyfactions.faction");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
