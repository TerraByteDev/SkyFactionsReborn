package net.skullian.torrent.skyfactions.util.gui.items.obelisk;

import net.skullian.torrent.skyfactions.api.GUIAPI;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.util.SoundUtil;
import net.skullian.torrent.skyfactions.util.gui.RuneSubmitUI;
import net.skullian.torrent.skyfactions.util.gui.data.GUIData;
import net.skullian.torrent.skyfactions.util.gui.data.ItemData;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.io.IOException;
import java.util.List;

public class ObeliskRuneItem extends AbstractItem {

    private String NAME;
    private String SOUND;
    private int PITCH;
    private List<String> LORE;
    private ItemStack STACK;
    private String TYPE;

    public ObeliskRuneItem(ItemData data, ItemStack stack, String type) {
        this.NAME = data.getNAME();
        this.SOUND = data.getSOUND();
        this.PITCH = data.getPITCH();
        this.LORE = data.getLORE();
        this.STACK = stack;
        this.TYPE = type;
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
        try {
            event.setCancelled(true);

            if (!SOUND.equalsIgnoreCase("none")) {
                SoundUtil.playSound(player, SOUND, PITCH, 1);
            }

            GUIData data = GUIAPI.getGUIData("runes_ui");
            new RuneSubmitUI(RuneSubmitUI.createStructure(player, TYPE, data), data).promptPlayer(player);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            event.getInventory().close();
            Messages.ERROR.send(player, "%operation%", "open the rune UI", "%debug%", "GUI_LOAD_EXCEPTION");
        }
    }

}
