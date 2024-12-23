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
import org.bukkit.entity.Player;
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
        Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), this::show);
    }

    public void show() {
        registerItems();
        Gui gui = builder.build();

        Player player = SpigotAdapter.adapt(screen.player).getPlayer();
        if (player != null) {
            Window window = Window.single()
                    .setViewer(player)
                    .setTitle(TextUtility.legacyColor(screen.guiData.getTITLE(), SkyApi.getInstance().getPlayerAPI().getLocale(screen.player.getUniqueId()), screen.player))
                    .setGui(gui)
                    .build();

            window.open();
        } else {
            SLogger.warn("Attempted to open GUI for null player. Did they disconnect?");
        }
    }

    private void registerItems() {
        List<ItemData> data = GUIAPI.getItemData(screen.guiPath, screen.player);
        for (ItemData itemData : data) {
            BaseSkyItem item = screen.handleItem(itemData);
            if (item != null) {
                if (item.isASYNC()) builder.addIngredient(itemData.getCHARACTER(), new SpigotAsyncSkyItem(item));
                    else builder.addIngredient(itemData.getCHARACTER(), new SpigotSkyItem(item));
            } else {
                SLogger.warn("Failed to fetch UI Item! ID: {} / CHAR: {}", itemData.getITEM_ID(), itemData.getCHARACTER());
            }
        }
    }

}
