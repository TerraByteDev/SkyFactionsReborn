package net.skullian.skyfactions.common.command;

import java.util.List;

public abstract class CommandTemplate {

    public abstract String getParent();

    public abstract String getName();

    public abstract String getDescription();

    public abstract String getSyntax();

    public abstract List<String> permission();

}
