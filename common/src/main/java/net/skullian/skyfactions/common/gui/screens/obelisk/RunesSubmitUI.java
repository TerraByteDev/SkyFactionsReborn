package net.skullian.skyfactions.common.gui.screens.obelisk;

import lombok.Builder;
import net.skullian.skyfactions.core.api.SpigotGUIAPI;
import net.skullian.skyfactions.core.api.SpigotRunesAPI;
import net.skullian.skyfactions.core.config.types.GUIEnums;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.EmptyItem;
import net.skullian.skyfactions.core.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.core.gui.items.rune_submit.RuneSubmitItem;
import net.skullian.skyfactions.common.gui.screens.Screen;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.item.Item;

import java.util.Map;

public class RunesSubmitUI extends Screen {
    private final VirtualInventory inventory;
    private final String type;

    @Builder
    public RunesSubmitUI(Player player, String type) {
        super(player, GUIEnums.RUNES_SUBMIT_GUI.getPath());
        this.type = type;

        int invSize = 0;
        for (String row : guiData.getLAYOUT()) {
            invSize += (int) row.chars()
                    .filter(ch -> ch == '.')
                    .count();
        }

        VirtualInventory inventory = new VirtualInventory(invSize);
        inventory.setPreUpdateHandler((handler) -> handler.setCancelled(SpigotRunesAPI.isStackProhibited(handler.getNewItem(), player)));
        this.inventory = inventory;

        initWindow();
        window.addCloseHandler(() -> {
            for (ItemStack item : this.inventory.getItems()) {
                if (item == null || item.getType().equals(Material.AIR)) return;
                Map<Integer, ItemStack> map = player.getInventory().addItem(item);

                for (ItemStack stack : map.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), stack);
                }
            }
        });
    }

    public static void promptPlayer(Player player, String type) {
        try {
            RunesSubmitUI.builder().player(player).type(type).build().show();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "open your runes submit GUI", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    protected Item handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID()) {
            case "PROMPT", "BORDER" ->
                    new EmptyItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "BACK" ->
                    new ObeliskBackItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), type, player);
            case "SUBMIT" ->
                    new RuneSubmitItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), type, inventory, player);
            default -> null;
        };
    }

    @Override
    protected void postRegister(Gui.Builder.Normal builder) {
        builder.addIngredient('.', inventory);
    }
}
