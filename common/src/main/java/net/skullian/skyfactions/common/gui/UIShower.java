package net.skullian.skyfactions.common.gui;

import net.skullian.skyfactions.common.gui.screens.PaginatedScreen;
import net.skullian.skyfactions.common.gui.screens.Screen;
import net.skullian.skyfactions.common.gui.screens.obelisk.RunesSubmitUI;

public abstract class UIShower {

    public abstract void show(Screen screen);

    public abstract void show(PaginatedScreen screen);

    public abstract void show(RunesSubmitUI screen);
}
