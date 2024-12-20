package net.skullian.skyfactions.paper.gui.screens;

import lombok.Builder;
import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.impl.BaseSkyItem;
import net.skullian.skyfactions.common.gui.screens.obelisk.RunesSubmitUI;
import net.skullian.skyfactions.common.util.SkyItemStack;
import net.skullian.skyfactions.common.util.text.TextUtility;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import net.skullian.skyfactions.paper.gui.items.impl.SpigotAsyncSkyItem;
import net.skullian.skyfactions.paper.gui.items.impl.SpigotSkyItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.window.Window;

import java.util.List;
import java.util.Map;

public class SpigotRunesSubmitScreen {
    private final RunesSubmitUI screen;
    private final Gui.Builder.Normal builder;

    @Builder
    public SpigotRunesSubmitScreen(RunesSubmitUI screen) {
        this.screen = screen;
        this.builder = Gui.normal().setStructure(screen.guiData.getLAYOUT());

        VirtualInventory inventory = new VirtualInventory(screen.getInvSize());
        inventory.setPreUpdateHandler((handler) -> {
            handler.setCancelled(SkyApi.getInstance().getRunesAPI().isStackProhibited(SpigotAdapter.adapt(handler.getNewItem()), screen.player));
            if (!handler.isCancelled()) screen.getInventory().put(handler.getSlot(), SpigotAdapter.adapt(handler.getNewItem()));
        });

        this.builder.addIngredient('.', inventory);

        show();
    }

    public void show() {
        registerItems();
        Gui gui = builder.build();

        Window window = Window.single()
                .setViewer(SpigotAdapter.adapt(screen.player).getPlayer())
                .setTitle(TextUtility.legacyColor(screen.guiData.getTITLE(), SkyApi.getInstance().getPlayerAPI().getLocale(screen.player.getUniqueId()), screen.player))
                .setGui(gui)
                .addCloseHandler(() -> {
                    Player player = SpigotAdapter.adapt(screen.player).getPlayer();
                    for (SkyItemStack item : screen.getInventory().values()) {
                        if (item == null || item.getMaterial().equals("AIR")) return;

                        Map<Integer, ItemStack> map = player.getInventory().addItem(SpigotAdapter.adapt(item, screen.player, false));
                        for (ItemStack stack : map.values()) {
                            player.getWorld().dropItemNaturally(player.getLocation(), stack);
                        }
                    }
                })
                .build();

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
