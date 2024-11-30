package net.skullian.skyfactions.common.gui.screens;

import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.gui.data.GUIData;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.impl.BaseSkyItem;
import net.skullian.skyfactions.common.user.SkyUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Screen {
    public final String guiPath;
    public final GUIData guiData;
    public final SkyUser player;

    public Screen(String guiPath, SkyUser player) {
        this.guiPath = guiPath;

        this.guiData = GUIAPI.getGUIData(guiPath, player);
        this.player = player;
    }

    public void show() {
        SkyApi.getInstance().getUIShower().show(player, this);
    }

    public abstract @Nullable BaseSkyItem handleItem(@NotNull ItemData itemData);
}

