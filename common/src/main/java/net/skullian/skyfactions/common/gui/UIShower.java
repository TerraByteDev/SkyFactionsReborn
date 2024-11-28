package net.skullian.skyfactions.common.gui;

import net.skullian.skyfactions.common.gui.screens.PaginatedScreen;
import net.skullian.skyfactions.common.gui.screens.Screen;
import net.skullian.skyfactions.common.user.SkyUser;

public abstract class UIShower {

    public abstract void show(SkyUser player, Screen screen);

    public abstract void show(SkyUser player, PaginatedScreen screen);
}
