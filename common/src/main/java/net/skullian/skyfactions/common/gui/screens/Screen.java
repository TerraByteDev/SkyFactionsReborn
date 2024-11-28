package net.skullian.skyfactions.common.gui.screens;

import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.gui.data.GUIData;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.user.SkyUser;
import org.jetbrains.annotations.Nullable;

public abstract class Screen {

    protected final SkyUser player;
    protected final String guiPath;

    protected final GUIData guiData;

    public Screen(SkyUser player, String guiPath) {
        this.player = player;
        this.guiPath = guiPath;

        this.guiData = GUIAPI.getGUIData(guiPath, player);
    }

    public abstract void initWindow();

    public abstract void show();

    public abstract @Nullable SkyItem
}
