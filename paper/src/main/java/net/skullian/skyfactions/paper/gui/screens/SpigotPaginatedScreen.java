package net.skullian.skyfactions.paper.gui.screens;

import lombok.Builder;
import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.PaginationItemData;
import net.skullian.skyfactions.common.gui.items.PaginationBackItem;
import net.skullian.skyfactions.common.gui.items.PaginationForwardItem;
import net.skullian.skyfactions.common.gui.items.impl.BaseSkyItem;
import net.skullian.skyfactions.common.gui.screens.PaginatedScreen;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.common.util.text.TextUtility;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import net.skullian.skyfactions.paper.gui.items.impl.SpigotAsyncSkyItem;
import net.skullian.skyfactions.paper.gui.items.impl.SpigotPaginationBackItem;
import net.skullian.skyfactions.paper.gui.items.impl.SpigotPaginationForwardItem;
import net.skullian.skyfactions.paper.gui.items.impl.SpigotSkyItem;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;
import xyz.xenondevs.invui.window.Window;

import java.util.List;

public class SpigotPaginatedScreen {
    private final PaginatedScreen screen;
    private final PagedGui.Builder builder;

    @Builder
    public SpigotPaginatedScreen(PaginatedScreen screen) {
        this.screen = screen;
        this.builder = PagedGui.items().setStructure(screen.guiData.getLAYOUT());

        show();
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
        builder.addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL);

        List<ItemData> data = GUIAPI.getItemData(screen.guiPath, screen.player);
        for (ItemData itemData : data) {
            if (itemData.getITEM_ID().equalsIgnoreCase("MODEL")) {
                builder.setContent(screen.getModels(screen.player, itemData));
                continue;
            }

            BaseSkyItem item = screen.handleItem(itemData);
            if (item != null) {
                if (item.isASYNC()) builder.addIngredient(itemData.getCHARACTER(), new SpigotAsyncSkyItem(item));
                    else builder.addIngredient(itemData.getCHARACTER(), new SpigotSkyItem(item));
            } else {
                SLogger.warn("Failed to fetch UI Item! ID: {} / CHAR: {}", itemData.getITEM_ID(), itemData.getCHARACTER());
            }
        }

        List<PaginationItemData> paginationData = GUIAPI.getPaginationData(screen.player);
        for (PaginationItemData paginationItem : paginationData) {
            PageItem item = handlePaginationItem(paginationItem);
            if (item == null) {
                SLogger.warn("PaginatedScreen inheritor handlePaginationItem returned null for {}. ({})", paginationItem.getITEM_ID(), screen.guiPath);
            } else builder.addIngredient(paginationItem.getCHARACTER(), item);
        }

    }

    private PageItem handlePaginationItem(PaginationItemData paginationItem) {
        return switch(paginationItem.getITEM_ID()) {
            case "FORWARD_BUTTON" ->
                    new SpigotPaginationForwardItem(new PaginationForwardItem(paginationItem, screen.player));
            case "BACK_BUTTON" ->
                    new SpigotPaginationBackItem(new PaginationBackItem(paginationItem, screen.player));
            default -> null;
        };
    }
}
