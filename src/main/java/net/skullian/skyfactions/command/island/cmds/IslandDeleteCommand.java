package net.skullian.skyfactions.command.island.cmds;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.IslandAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CooldownHandler;
import net.skullian.skyfactions.command.PermissionsHandler;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class IslandDeleteCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getDescription() {
        return "Delete your island. PERMANENT.";
    }

    @Override
    public String getSyntax() {
        return "/island delete <confirm>";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;
        SkyFactionsReborn.db.hasIsland(player).thenAccept(has -> {
            if (!has) {
                Messages.NO_ISLAND.send(player);
            } else {

                if (args.length == 1) {
                    Messages.DELETION_CONFIRM.send(player);
                    IslandAPI.awaitingDeletion.add(player.getUniqueId());
                } else if (args.length > 1) {
                    if (args[1].equals("confirm")) {

                        if (IslandAPI.awaitingDeletion.contains(player.getUniqueId())) {
                            World hubWorld = Bukkit.getWorld(Settings.HUB_WORLD_NAME.getString());
                            // todo migrate to gui command conf
                            if (hubWorld != null) {
                                Messages.DELETION_PROCESSING.send(player);

                                SkyFactionsReborn.worldBorderApi.resetWorldBorderToGlobal(player); // reset the world border
                                List<Integer> hubLocArray = Settings.HUB_LOCATION.getIntegerList();
                                Location location = new Location(hubWorld, hubLocArray.get(0), hubLocArray.get(1), hubLocArray.get(2));
                                IslandAPI.teleportPlayerToLocation(player, location);

                                IslandAPI.awaitingDeletion.remove(player.getUniqueId());
                                IslandAPI.removePlayerIsland(player);
                            } else {
                                Messages.ERROR.send(player, "%operation%", "delete your island", "%debug%", "WORLD_NOT_EXIST");
                            }
                        } else {
                            Messages.DELETION_BLOCK.send(player);
                        }
                    } else {
                        Messages.INCORRECT_USAGE.send(player, "%usage%", getSyntax());
                    }
                }
            }
        });
    }

    public static List<String> permissions = List.of("skyfactions.island.delete", "skyfactions.island");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
