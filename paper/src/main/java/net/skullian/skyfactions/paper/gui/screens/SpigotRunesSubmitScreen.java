package net.skullian.skyfactions.paper.gui.screens;

import lombok.Builder;
import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.impl.BaseSkyItem;
import net.skullian.skyfactions.common.gui.screens.obelisk.RunesSubmitUI;
import net.skullian.skyfactions.common.util.SLogger;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SpigotRunesSubmitScreen {
    private final RunesSubmitUI screen;
    private final Gui.Builder.Normal builder;

    @Builder
    public SpigotRunesSubmitScreen(RunesSubmitUI screen) {
        this.screen = screen;
        this.builder = Gui.normal().setStructure(screen.guiData.getLAYOUT());

        show();
    }

    public void show() {
        registerItems();

        VirtualInventory inventory = new VirtualInventory(screen.getInvSize());
        inventory.setPreUpdateHandler((handler) -> {
            ItemStack stack = handler.getNewItem();
            if (stack == null) return;

            handler.setCancelled(SkyApi.getInstance().getRunesAPI().isStackProhibited(SpigotAdapter.adapt(stack), screen.player));
            if (!handler.isCancelled()) screen.getInventory().put(handler.getSlot(), SpigotAdapter.adapt(stack));
        });
        this.builder.addIngredient('.', inventory);

        Gui gui = builder.build();

        Player player = SpigotAdapter.adapt(screen.player).getPlayer();
        if (player != null) {
            Window window = Window.single()
                    .setViewer(player)
                    .setTitle(TextUtility.legacyColor(screen.guiData.getTITLE(), SkyApi.getInstance().getPlayerAPI().getLocale(screen.player.getUniqueId()), screen.player))
                    .setGui(gui)
                    .addCloseHandler(() -> {
                        Player fetchedPlayer = SpigotAdapter.adapt(screen.player).getPlayer();
                        if (fetchedPlayer != null) {
                            for (SkyItemStack skyItemStack : screen.getInventory().values()) {
                                ItemStack item = ItemStack.deserializeBytes((byte[]) skyItemStack.getSerializedBytes());
                                if (item == null || item.getType().name().equalsIgnoreCase("AIR")) return;

                                Map<Integer, ItemStack> map = fetchedPlayer.getInventory().addItem(SpigotAdapter.adapt(skyItemStack, screen.player, false));
                                for (ItemStack stack : map.values()) {
                                    fetchedPlayer.getWorld().dropItemNaturally(fetchedPlayer.getLocation(), stack);
                                }
                            }
                        } else {
                            SLogger.warn("Attempted to handle rune UI for null player.");
                        }
                    })
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
