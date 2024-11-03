package net.skullian.skyfactions.gui.items.obelisk.audit_log;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.db.AuditLogData;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.util.text.TextUtility;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public class AuditPaginationItem extends SkyItem {

    private AuditLogData DATA;

    public AuditPaginationItem(ItemData data, ItemStack stack, AuditLogData auditData, Player player) {
        super(data, stack, player, null);
        
        this.DATA = auditData;
    }

    @Override
    public ItemProvider getItemProvider() {
        String locale = Messages.getDefaulLocale(); // todo make audit system language compat

        ItemBuilder builder = new ItemBuilder(getSTACK())
                .setDisplayName(TextUtility.legacyColor(getDATA().getNAME().replace("audit_title", DATA.getType()), locale, getPLAYER()));

        for (String loreLine : getDATA().getLORE()) {
            if (loreLine.contains("<audit_description>")) {
                for (String part : TextUtility.toParts(DATA.getDescription())) {
                    builder.addLoreLines(part);
                }

                continue;
            }

            builder.addLoreLines(TextUtility.legacyColor(loreLine
                    .replace("timestamp", Messages.replace(Messages.AUDIT_FACTION_TIMESTAMP_FORMAT.getString(locale), locale, getPLAYER(), "time", TextUtility.formatExtendedElapsedTime(DATA.getTimestamp()))),
                    locale,
                    getPLAYER()
            ));
        }

        return builder;
    }
}
