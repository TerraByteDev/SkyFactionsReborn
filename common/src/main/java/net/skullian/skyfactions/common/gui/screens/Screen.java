package net.skullian.skyfactions.common.gui.screens;

import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.gui.data.GUIData;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.impl.BaseSkyItem;
import net.skullian.skyfactions.common.user.SkyUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Screen {
    protected final String guiPath;
    protected final GUIData guiData;

    public Screen(String guiPath, SkyUser player) {
        this.guiPath = guiPath;
        // GUIAPI should be platform independent (remove player)
        this.guiData = GUIAPI.getGUIData(guiPath, player);
    }

    // init window in here
    protected abstract void init();

    public abstract void show();

    protected abstract @Nullable BaseSkyItem handleItem(@NotNull ItemData itemData);

    protected abstract void registerItems();
}

