package net.skullian.skyfactions.paper.command.faction.cmds;

import net.skullian.skyfactions.paper.api.SpigotFactionAPI;
import net.skullian.skyfactions.paper.command.CommandTemplate;
import net.skullian.skyfactions.paper.command.CommandsUtility;
import net.skullian.skyfactions.paper.config.types.Messages;
import net.skullian.skyfactions.paper.api.SpigotPlayerAPI;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.paper.util.ErrorUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
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
            SpigotFactionAPI.isInFaction(player).whenComplete((isInFaction, ex) -> {
                if (ex != null) {
                    ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                    return;
                }

                if (!isInFaction) {
                    Messages.NOT_IN_FACTION.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
                } else {

                    SpigotFactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, throwable) -> {
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
            SpigotFactionAPI.getFaction(name).whenComplete((faction, ex) -> {
                if (ex != null) {
                    ErrorUtil.handleError(player, "get the Faction", "SQL_FACTION_GET", ex);
                    return;
                }

                if (faction != null) {
                    sendInfo(player, faction);
                } else {
                    Messages.FACTION_NOT_FOUND.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "name", name);
                }
            });
        }
    }

    private void sendInfo(Player player, Faction faction) {

        Messages.FACTION_INFO_LIST.send(player,
                SpigotPlayerAPI.getLocale(player.getUniqueId()),
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
