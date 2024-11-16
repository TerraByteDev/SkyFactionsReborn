package net.skullian.skyfactions.gui.screens;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.GUIAPI;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.gui.data.GUIData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.data.PaginationItemData;
import net.skullian.skyfactions.gui.items.AirItem;
import net.skullian.skyfactions.util.SLogger;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

import java.util.List;

public abstract class PaginatedScreen {
    protected final Player player;
    protected final String guiPath;
    protected final GUIData inviteData;

    protected Window window;

    public PaginatedScreen(Player player, String guiPath) {
        this.player = player;
        this.guiPath = guiPath;

        this.inviteData = GUIAPI.getGUIData(guiPath, player);
    }

    protected final void initWindow() {
        window = Window.single()
                .setViewer(player)
                .setTitle(TextUtility.legacyColor(inviteData.getTITLE(), PlayerHandler.getLocale(player.getUniqueId()), player))
                .setGui(registerItems())
                .build();
    }

    public final void show() {
        if (Bukkit.isPrimaryThread()) {
            showUnsafe();
        } else {
            Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), this::showUnsafe);
        }
    }

    private void showUnsafe() {
        SoundUtil.playSound(player, inviteData.getOPEN_SOUND(), inviteData.getOPEN_PITCH(), 1f);
        window.open();
    }

    protected abstract @Nullable Item handleItem(@NotNull ItemData itemData);

    protected abstract @Nullable Item handlePaginationItem(@NotNull PaginationItemData paginationItem);

    protected abstract @NotNull List<Item> getModels(Player player, ItemData data);

    private Gui registerItems() {
        PagedGui.Builder builder = PagedGui.items().setStructure(inviteData.getLAYOUT());
        builder.addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL);

        List<ItemData> data = GUIAPI.getItemData(guiPath, player);
        for (ItemData itemData : data) {
            if (itemData.getITEM_ID().equals("MODEL")) {
                builder.setContent(getModels(player, itemData));
                continue;
            }

            Item item = handleItem(itemData);
            if (item == null) {
                SLogger.warn("PaginatedScreen inheritor handleItem returned null for {}. ({})", itemData.getITEM_ID(), guiPath);
                item = new AirItem(player);
            }

            builder.addIngredient(itemData.getCHARACTER(), item);
        }

        List<PaginationItemData> paginationData = GUIAPI.getPaginationData(player);
        for (PaginationItemData paginationItem : paginationData) {
            Item item = handlePaginationItem(paginationItem);
            if (item == null) {
                SLogger.warn("PaginatedScreen inheritor handlePaginationItem returned null for {}. ({})", paginationItem.getITEM_ID(), guiPath);
                item = new AirItem(player);
            }

            builder.addIngredient(paginationItem.getCHARACTER(), item);
        }

        return builder.build();
    }
}
