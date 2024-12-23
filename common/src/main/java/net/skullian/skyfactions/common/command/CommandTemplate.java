package net.skullian.skyfactions.common.command;

import net.skullian.skyfactions.common.user.SkyUser;

import java.util.List;

public abstract class CommandTemplate {

    public abstract String getParent();

    public abstract String getName();

    public abstract String getDescription();

    public abstract String getSyntax();

    public abstract List<String> permission();

    public boolean hasPermission(SkyUser player, boolean sendDeny) {
        return CommandsUtility.hasPerm(player, permission(), sendDeny);
    }

}
