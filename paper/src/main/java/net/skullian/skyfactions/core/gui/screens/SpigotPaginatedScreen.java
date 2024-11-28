package net.skullian.skyfactions.core.gui.screens;

import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.PaginationItemData;
import net.skullian.skyfactions.common.gui.items.AirItem;
import net.skullian.skyfactions.common.gui.items.impl.BaseSkyItem;
import net.skullian.skyfactions.common.gui.screens.PaginatedScreen;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.common.util.text.TextUtility;
import net.skullian.skyfactions.core.SkyFactionsReborn;
import net.skullian.skyfactions.core.api.adapter.SpigotAdapter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.window.Window;

import java.util.List;

public abstract class SpigotPaginatedScreen extends PaginatedScreen {
    protected final SkyUser player;
    protected PagedGui.Builder builder;
    protected Window window;

    public SpigotPaginatedScreen(String guiPath, SkyUser player) {
        super(guiPath, player);

        this.player = player;
        this.builder = PagedGui.items().setStructure(getGuIData().getLAYOUT());
    }

    @Override
    public final void init() {
        registerItems();
        Gui gui = builder.build();

        window = Window.single()
                .setViewer(SpigotAdapter.adapt(player).getPlayer())
                .setTitle(TextUtility.legacyColor(guIData.getTITLE(), SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), player))
                .setGui(gui)
                .build();
    }

    @Override
    public final void show() {
        if (Bukkit.isPrimaryThread()) {
            showUnsafe();
        } else {
            Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), this::showUnsafe);
        }
    }

    private void showUnsafe() {
        SkyApi.getInstance().getSoundAPI().playSound(player, getGuIData().getOPEN_SOUND(), getGuIData().getOPEN_PITCH(), 1f);
        window.open();
    }

    @Override
    protected abstract @Nullable BaseSkyItem handleItem(@NotNull ItemData itemData);

    protected void postRegister(PagedGui.Builder builder) {
    }

    @Override
    protected void registerItems() {
        builder.addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL);

        List<ItemData> data = GUIAPI.getItemData(guiPath, player);
        for (ItemData itemData : data) {
            if (itemData.getITEM_ID().equals("MODEL")) {
                builder.setContent(getModels(player, itemData));
                continue;
            }

            BaseSkyItem item = handleItem(itemData);
            builder.addIngredient(itemData.getCHARACTER(), SpigotAdapter.adapt(item.getSTACK(), player, true));
        }

        List<PaginationItemData> paginationData = GUIAPI.getPaginationData(player);
        for (PaginationItemData paginationItem : paginationData) {
            BaseSkyItem item = handlePaginationItem(paginationItem);
            if (item == null) {
                SLogger.warn("PaginatedScreen inheritor handlePaginationItem returned null for {}. ({})", paginationItem.getITEM_ID(), guiPath);
                item = new AirItem(player);
            }

            builder.addIngredient(paginationItem.getCHARACTER(), SpigotAdapter.adapt(item.getSTACK(), player, true));
        }

        postRegister(builder);
    }





}
