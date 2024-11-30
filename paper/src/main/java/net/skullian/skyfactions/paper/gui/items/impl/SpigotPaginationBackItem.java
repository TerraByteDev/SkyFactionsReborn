package net.skullian.skyfactions.paper.gui.items.impl;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.gui.items.impl.SkyPageItem;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;

public class SpigotPaginationBackItem extends PageItem {

    private SkyPageItem ITEM;

    public SpigotPaginationBackItem(SkyPageItem item) {
        super(false);
        this.ITEM = item;
    }

    @Override
    public ItemProvider getItemProvider(PagedGui<?> gui) {
        this.ITEM.setCurrentPage(gui.getCurrentPage());
        this.ITEM.setPageAmount(gui.getPageAmount());

        return SpigotAdapter.adapt(this.ITEM.getSTACK(), this.ITEM.getPLAYER());
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (clickType == ClickType.LEFT) {
            getGui().goBack();
        }

        if (!ITEM.getDATA().getSOUND().equalsIgnoreCase("none")) {
            SkyApi.getInstance().getSoundAPI().playSound(SkyApi.getInstance().getUserManager().getUser(player.getUniqueId()), ITEM.getDATA().getSOUND(), ITEM.getDATA().getPITCH(), 1);
        }
    }

}
