package net.skullian.torrent.skyfactions.gui.items.obelisk;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.gui.data.ItemData;
import net.skullian.torrent.skyfactions.util.ErrorHandler;
import net.skullian.torrent.skyfactions.util.SoundUtil;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.List;

public class ObeliskFactionOverviewItem extends AbstractItem {

    private String NAME;
    private String SOUND;
    private int PITCH;
    private List<String> LORE;
    private ItemStack STACK;
    private Player PLAYER;

    public ObeliskFactionOverviewItem(ItemData data, ItemStack stack, Player player) {
        this.NAME = data.getNAME();
        this.SOUND = data.getSOUND();
        this.PITCH = data.getPITCH();
        this.LORE = data.getLORE();
        this.STACK = stack;
        this.PLAYER = player;
    }

    @Override
    public ItemProvider getItemProvider() {
        SkyFactionsReborn.db.getFaction(PLAYER).handle((faction, throwable) -> {
            if (throwable != null) {
                ErrorHandler.handleError(PLAYER, "get Faction data", "SQL_FACTION_GET", throwable);
            } else {
                ItemBuilder builder = new ItemBuilder(STACK)
                        .setDisplayName(TextUtility.color(NAME.replace("%faction_name%", faction.getName())));

                for (String loreLine : LORE) {
                    builder.addLoreLines(TextUtility.color(loreLine
                            .replace("%level%", String.valueOf(faction.getLevel()))
                            .replace("%member_count%", String.valueOf(faction.getTotalMemberCount()))
                            .replace("%rune_count%", String.valueOf(faction.getRunes()))
                            .replace("%gem_count%", String.valueOf(faction.getGems()))
                    ));
                }

                return builder;
            }

            return null;
        });

        return null;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.setCancelled(true);

        if (!SOUND.equalsIgnoreCase("none")) {
            SoundUtil.playSound(player, SOUND, PITCH, 1);
        }
    }


}
