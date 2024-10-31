package net.skullian.skyfactions.gui.items.rune_submit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.RunesAPI;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.gui.obelisk.RunesSubmitUI;

public class RuneSubmitItem extends SkyItem {

    private RunesSubmitUI UI;
    private String TYPE;

    public RuneSubmitItem(ItemData data, ItemStack stack, String type, RunesSubmitUI ui, Player player) {
        super(data, stack, player, null);
        this.UI = ui;
        this.TYPE = type;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        player.closeInventory();

        List<ItemStack> stacks = new ArrayList<>(UI.ITEMS.values());
        UI.ITEMS.clear(); // clear so when the close handler triggers, it doesn't give items back

        player.removeMetadata("rune_ui", SkyFactionsReborn.getInstance());
        if (TYPE.equals("player")) {
            RunesAPI.handleRuneConversion(stacks, player);
        } else if (TYPE.equals("faction")) {
            RunesAPI.handleRuneFactionConversion(stacks, player);
        }
    }
}
