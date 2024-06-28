package net.skullian.torrent.skyfactions.util.gui.items;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.util.SoundUtil;
import net.skullian.torrent.skyfactions.util.gui.ItemData;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.List;

public class RaidCancelItem extends AbstractItem {

    private String NAME;
    private String MATERIAL;
    private String SOUND;
    private int PITCH;
    private List<String> LORE;

    public RaidCancelItem(ItemData data) {
        this.NAME = data.getNAME();
        this.MATERIAL = data.getMATERIAL();
        this.SOUND = data.getSOUND();
        this.PITCH = data.getPITCH();
        this.LORE = data.getLORE();
    }

    @Override
    public ItemProvider getItemProvider() {
        // TODO - REMOVE LORE FROM MESSAGES.YML IN FAVOUR OF GUIS YAML
        ItemBuilder builder = new ItemBuilder(Material.getMaterial(MATERIAL))
                .setDisplayName(TextUtility.color(NAME));
        for (String loreLine : LORE) {
            builder.addLoreLines(TextUtility.color(loreLine));
        }

        return builder;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.setCancelled(true);
        event.getInventory().close();

        if (!SOUND.equalsIgnoreCase("none")) {
            SoundUtil.playSound(player, SOUND, PITCH, 1);
        }
    }
}
