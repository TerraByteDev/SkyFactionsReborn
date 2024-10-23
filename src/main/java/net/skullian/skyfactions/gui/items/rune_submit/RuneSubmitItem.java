package net.skullian.skyfactions.gui.items.rune_submit;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.RunesAPI;
import net.skullian.skyfactions.gui.obelisk.RunesSubmitUI;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RuneSubmitItem extends AbstractItem {

    private String NAME;
    private String SOUND;
    private int PITCH;
    private List<String> LORE;
    private ItemStack STACK;
    private String TYPE;
    private RunesSubmitUI UI;

    public RuneSubmitItem(ItemData data, ItemStack stack, String type, RunesSubmitUI ui) {
        this.NAME = data.getNAME();
        this.SOUND = data.getSOUND();
        this.PITCH = data.getPITCH();
        this.LORE = data.getLORE();
        this.STACK = stack;
        this.TYPE = type;
        this.UI = ui;
    }

    @Override
    public ItemProvider getItemProvider() {
        ItemBuilder builder = new ItemBuilder(STACK)
                .setDisplayName(TextUtility.color(NAME));

        for (String loreLine : LORE) {
            builder.addLoreLines(TextUtility.color(loreLine));
        }

        return builder;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.setCancelled(true);

        if (!SOUND.equalsIgnoreCase("none")) {
            SoundUtil.playSound(player, SOUND, PITCH, 1);
        }
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
