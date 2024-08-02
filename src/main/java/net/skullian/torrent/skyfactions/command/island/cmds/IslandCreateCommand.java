package net.skullian.torrent.skyfactions.command.island.cmds;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.util.SoundUtil;
import net.skullian.torrent.skyfactions.gui.IslandCreationConfirmationUI;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class IslandCreateCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Create a brand new island!";
    }

    @Override
    public String getSyntax() {
        return "/island create";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;

        try {
            AtomicBoolean has = new AtomicBoolean(false);
            SkyFactionsReborn.db.hasIsland(player).thenAccept(has::set).exceptionally(ex -> {
                ex.printStackTrace();
                Messages.ERROR.send(player, "%operation%", "create an island", "%debug%", "SQL_ISLAND_CHECK");
                return null;
            }).get();

            if (has.get()) {
                Messages.ISLAND_CREATION_DENY.send(player);
                return;
            } else {
                IslandCreationConfirmationUI.promptPlayer(player);
            }
        } catch (InterruptedException | ExecutionException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "create an island", "%debug%", "SQL_ISLAND_CHECK");
        }
    }

    @Override
    public String permission() {
        return "skyfactions.island.create";
    }
}
