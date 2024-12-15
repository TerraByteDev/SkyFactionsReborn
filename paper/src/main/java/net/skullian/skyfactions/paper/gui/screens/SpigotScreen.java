package net.skullian.skyfactions.paper.gui.screens;

import lombok.Builder;
import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.impl.BaseSkyItem;
import net.skullian.skyfactions.common.gui.screens.Screen;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.common.util.text.TextUtility;
import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import net.skullian.skyfactions.paper.gui.items.impl.SpigotAsyncSkyItem;
import net.skullian.skyfactions.paper.gui.items.impl.SpigotSkyItem;
import org.bukkit.Bukkit;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

import java.util.List;

public class SpigotScreen {
    private final Screen screen;
    private final Gui.Builder.Normal builder;

    @Builder
    public SpigotScreen(Screen screen) {
        this.screen = screen;
        this.builder = Gui.normal().setStructure(screen.guiData.getLAYOUT());
        Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> {
            SLogger.info("SHOWING");
            show();
        });
    }

    public void show() {
        SLogger.info("REGISTERING");
        registerItems();
        Gui gui = builder.build();
        SLogger.info("REGISTERED AND BUILDED");
        Window window = Window.single()
                .setViewer(SpigotAdapter.adapt(screen.player).getPlayer())
                .setTitle(TextUtility.legacyColor(screen.guiData.getTITLE(), SkyApi.getInstance().getPlayerAPI().getLocale(screen.player.getUniqueId()), screen.player))
                .setGui(gui)
                .build();

        SLogger.info("BUILDED");
        window.open();
    }

    private void registerItems() {
        List<ItemData> data = GUIAPI.getItemData(screen.guiPath, screen.player);
        for (ItemData itemData : data) {
            BaseSkyItem item = screen.handleItem(itemData);
            if (item.isASYNC()) builder.addIngredient(itemData.getCHARACTER(), new SpigotAsyncSkyItem(item));
                else builder.addIngredient(itemData.getCHARACTER(), new SpigotSkyItem(item));
        }
    }

}
