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
        String moderators = String.join(", ", faction.getFactionModerators().stream().map(OfflinePlayer::getName).collect(Collectors.toList()));
        String members = String.join(", ", faction.getMembers().stream().map(OfflinePlayer::getName).collect(Collectors.toList()));

        Messages.FACTION_INFO_LIST.send(player,
                "%name%", faction.getName(),
                "%motd%", TextUtility.color(faction.getMOTD()),
                "%owner%", faction.getOwner().getName(),
                "%moderators%", moderators,
                "%members", members
                );
    }

    @Override
    public String permission() {
        return "skyfactions.faction.info";
    }
}
