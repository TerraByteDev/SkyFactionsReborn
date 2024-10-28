package net.skullian.skyfactions.gui.items.obelisk.audit_log;

import org.bukkit.inventory.ItemStack;

import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.db.AuditLogData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.util.text.TextUtility;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public class AuditPaginationItem extends SkyItem {

    private AuditLogData DATA;

    public AuditPaginationItem(ItemData data, ItemStack stack, AuditLogData auditData) {
        super(data, stack, null);
        
        this.DATA = auditData;
    }

    @Override
    public ItemProvider getItemProvider() {
        ItemBuilder builder = new ItemBuilder(getSTACK())
                .setDisplayName(TextUtility.color(getDATA().getNAME().replace("%audit_title%", DATA.getType())));

        for (String loreLine : getDATA().getLORE()) {
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
}
