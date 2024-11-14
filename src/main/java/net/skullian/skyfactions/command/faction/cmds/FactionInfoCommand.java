package net.skullian.skyfactions.command.faction.cmds;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.util.ErrorUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

@Command("faction")
public class FactionInfoCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Get info about your faction, or another faction.";
    }

    @Override
    public String getSyntax() {
        return "/faction info <name>";
    }

    @Command("info [name]")
    @Permission(value = {"skyfactions.faction.info", "skyfactions.faction"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            Player player,
            @Argument(value = "name") @Nullable String name
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;

        if (name == null) {
            FactionAPI.isInFaction(player).whenComplete((isInFaction, ex) -> {
                if (ex != null) {
                    ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                    return;
                }

                if (!isInFaction) {
                    Messages.NOT_IN_FACTION.send(player, PlayerHandler.getLocale(player.getUniqueId()));
                } else {

                    FactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, throwable) -> {
                        if (throwable != null) {
                            ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", throwable);
                            return;
                        }

                        if (faction != null) {
                            sendInfo(player, faction);
                        }
                    });
                }
            });
        } else {
            FactionAPI.getFaction(name).whenComplete((faction, ex) -> {
                if (ex != null) {
                    ErrorUtil.handleError(player, "get the Faction", "SQL_FACTION_GET", ex);
                    return;
                }

                if (faction != null) {
                    sendInfo(player, faction);
                } else {
                    Messages.FACTION_NOT_FOUND.send(player, PlayerHandler.getLocale(player.getUniqueId()), "name", name);
                }
            });
        }
    }

    private void sendInfo(Player player, Faction faction) {

        Messages.FACTION_INFO_LIST.send(player,
                PlayerHandler.getLocale(player.getUniqueId()),
                "faction_name", faction.getName(),
                "motd", faction.getMOTD(),
                "owner", faction.getOwner().getName(),
                "admins", buildString(faction.getAdmins()),
                "moderators", buildString(faction.getModerators()),
                "fighters", buildString(faction.getFighters()),
                "members", buildString(faction.getMembers())
        );
    }

    private String buildString(List<OfflinePlayer> list) {
        if (list.size() <= 0) {
            return "<red>None";
        } else if (list.size() > 0) {
            return String.join(", ", list.stream().map(OfflinePlayer::getName).collect(Collectors.toList()));
        }

        return "<red>None";
    }

    public static List<String> permissions = List.of("skyfactions.faction.info", "skyfactions.faction");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
