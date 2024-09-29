package net.skullian.torrent.skyfactions.gui.items.obelisk.audit_log;

import net.skullian.torrent.skyfactions.config.types.Messages;
import net.skullian.torrent.skyfactions.db.AuditLogData;
import net.skullian.torrent.skyfactions.gui.data.ItemData;
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

public class AuditPaginationItem extends AbstractItem {

    private String NAME;
    private String SOUND;
    private int PITCH;
    private List<String> LORE;
    private AuditLogData DATA;
    private ItemStack STACK;

    public AuditPaginationItem(ItemData data, ItemStack stack, AuditLogData auditData) {
        this.NAME = data.getNAME();
        this.SOUND = data.getSOUND();
        this.PITCH = data.getPITCH();
        this.LORE = data.getLORE();
        this.DATA = auditData;
        this.STACK = stack;
    }

    @Override
    public ItemProvider getItemProvider() {
        ItemBuilder builder = new ItemBuilder(STACK)
                .setDisplayName(TextUtility.color(NAME.replace("%audit_title%", DATA.getType())));

        for (String loreLine : LORE) {
            if (loreLine.contains("%audit_description%")) {
                for (String part : TextUtility.toParts(DATA.getDescription())) {
                    builder.addLoreLines(part);
                }

                continue;
            }

            builder.addLoreLines(TextUtility.color(loreLine
                    .replace("%timestamp%", Messages.AUDIT_FACTION_TIMESTAMP_FORMAT.get("%time%", TextUtility.formatExtendedElapsedTime(DATA.getTimestamp())))
            ));
        }

        return builder;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.setCancelled(true);

        if (!SOUND.equalsIgnoreCase("none")) {
            SoundUtil.playSound(player, SOUND, PITCH, 1);
        }
    }
}
