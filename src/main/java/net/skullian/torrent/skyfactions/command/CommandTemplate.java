package net.skullian.torrent.skyfactions.command;

import org.bukkit.entity.Player;

public abstract class CommandTemplate {

    public abstract String getName();

    public abstract String getDescription();

    public abstract String getSyntax();

    public abstract void perform(Player player, String args[]);

    public abstract String permission();

}
