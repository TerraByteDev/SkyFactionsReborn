package net.skullian.torrent.skyfactions.util.gui.items.rune_submit;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.api.RunesAPI;
import net.skullian.torrent.skyfactions.util.SoundUtil;
import net.skullian.torrent.skyfactions.util.gui.data.ItemData;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.ArrayList;
import java.util.List;

public class RuneSubmitItem extends AbstractItem {

    private String NAME;
    private String SOUND;
    private int PITCH;
    private List<String> LORE;
    private ItemStack STACK;
    private String TYPE;
    private AbstractGui GUI;

    public RuneSubmitItem(ItemData data, ItemStack stack, String type, AbstractGui gui) {
        this.NAME = data.getNAME();
        this.SOUND = data.getSOUND();
        this.PITCH = data.getPITCH();
        this.LORE = data.getLORE();
        this.STACK = stack;
        this.TYPE = type;
        this.GUI = gui;
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

        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < event.getClickedInventory().getSize(); i++) {
            if (GUI.hasSlotElement(i)) continue;

            stacks.add(event.getClickedInventory().getItem(i));
        }

        player.removeMetadata("rune_ui", SkyFactionsReborn.getInstance());
        if (TYPE.equals("player")) {
            RunesAPI.handleRuneConversion(stacks, player);
            player.closeInventory();
        } else if (TYPE.equals("faction")) {
            RunesAPI.handleRuneFactionConversion(stacks, player);
            player.closeInventory();
        }
    }
}
