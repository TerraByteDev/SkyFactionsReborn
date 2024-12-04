package net.skullian.skyfactions.paper.gui;

import net.skullian.skyfactions.common.gui.UIShower;
import net.skullian.skyfactions.common.gui.screens.PaginatedScreen;
import net.skullian.skyfactions.common.gui.screens.Screen;
import net.skullian.skyfactions.common.gui.screens.obelisk.RunesSubmitUI;
import net.skullian.skyfactions.paper.gui.screens.SpigotPaginatedScreen;
import net.skullian.skyfactions.paper.gui.screens.SpigotScreen;

public class SpigotUIShower extends UIShower {

    @Override
    public void show(Screen screen) {
        new SpigotScreen(screen);
    }

    @Override
    public void show(PaginatedScreen screen) {
        new SpigotPaginatedScreen(screen);
    }

    @Override
    public void show(RunesSubmitUI screen) {

    }

}
