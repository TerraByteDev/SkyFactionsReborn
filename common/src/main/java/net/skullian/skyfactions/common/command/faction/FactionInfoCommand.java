package net.skullian.skyfactions.common.command.faction;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

@Command("faction")
public class FactionInfoCommand extends CommandTemplate {
    @Override
    public String getParent() {
        return "faction";
    }

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
            SkyUser player,
            @Argument(value = "name") @Nullable String name
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

        if (name == null) {
            SkyApi.getInstance().getFactionAPI().isInFaction(player.getUniqueId()).whenComplete((isInFaction, ex) -> {
                if (ex != null) {
                    ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                } else if (!isInFaction) {
                    Messages.NOT_IN_FACTION.send(player, locale);
                } else {

                    SkyApi.getInstance().getFactionAPI().getFaction(player.getUniqueId()).whenComplete((faction, throwable) -> {
                        if (throwable != null) {
                            ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", throwable);
                            return;
                        }

                        if (faction != null) {
                            sendInfo(player, faction, locale);
                        }
                    });
                }

            });
        } else {
            SkyApi.getInstance().getFactionAPI().getFaction(name).whenComplete((faction, ex) -> {
                if (ex != null) {
                    ErrorUtil.handleError(player, "get the Faction", "SQL_FACTION_GET", ex);
                    return;
                }

                if (faction != null) {
                    sendInfo(player, faction, locale);
                } else {
                    Messages.FACTION_NOT_FOUND.send(player, locale, "name", name);
                }
            });
        }
    }

    private void sendInfo(SkyUser player, Faction faction, String locale) {

        Messages.FACTION_INFO_LIST.send(player,
                locale,
                "faction_name", faction.getName(),
                "motd", faction.getMOTD(),
                "owner", faction.getOwner().getName(),
                "admins", buildString(faction.getAdmins()),
                "moderators", buildString(faction.getModerators()),
                "fighters", buildString(faction.getFighters()),
                "members", buildString(faction.getMembers())
        );
    }

    private String buildString(List<SkyUser> list) {
        if (!list.isEmpty()) {
            return String.join(", ", list.stream().map(SkyUser::getName).collect(Collectors.toList()));
        }

        return "<red>None";
    }

    public static List<String> permissions = List.of("skyfactions.faction.info", "skyfactions.faction");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
