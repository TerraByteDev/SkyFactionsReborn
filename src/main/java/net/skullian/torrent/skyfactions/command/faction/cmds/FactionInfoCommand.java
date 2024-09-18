package net.skullian.torrent.skyfactions.command.faction.cmds;

import net.skullian.torrent.skyfactions.api.FactionAPI;
import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.faction.Faction;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public void perform(Player player, String[] args) {
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;

        if (args.length == 1) {
            if (!FactionAPI.isInFaction(player)) {
                Messages.NOT_IN_FACTION.send(player);
                return;
            }

            Faction faction = FactionAPI.getFaction(player);
            if (faction != null) {
                sendInfo(player, faction);
            }
        } else if (args.length > 1) {
            String name = args[1];

            Faction faction = FactionAPI.getFaction(name);
            if (faction != null) {
                sendInfo(player, faction);
            } else {
                Messages.FACTION_NOT_FOUND.send(player, "%name%", name);
            }
        }
    }

    private void sendInfo(Player player, Faction faction) {

        Messages.FACTION_INFO_LIST.send(player,
                "%faction_name%", faction.getName(),
                "%motd%", TextUtility.color(faction.getMOTD()),

                "%owner%", faction.getOwner().getName(),
                "%admins%", buildString(faction.getAdmins()),
                "%moderators%", buildString(faction.getModerators()),
                "%fighters%", buildString(faction.getFighters()),
                "%members%", buildString(faction.getMembers())
                );
    }

    private String buildString(List<OfflinePlayer> list) {
        if (list.size() <= 0) {
            return "&aNone";
        } else if (list.size() > 0) {
            return String.join(", ", list.stream().map(OfflinePlayer::getName).collect(Collectors.toList()));
        }

        return "&aNone";
    }

    @Override
    public List<String> permission() {
        return List.of("skyfactions.faction.info", "skyfactions.faction", "skyfactions.player");
    }
}
