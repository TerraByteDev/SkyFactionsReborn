package net.skullian.skyfactions.core.gui.items.obelisk.audit_log;

import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.common.faction.AuditLogType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.common.database.struct.AuditLogData;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.impl.old.SkyItem;
import net.skullian.skyfactions.common.util.text.TextUtility;
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
        String locale = SpigotPlayerAPI.getLocale(getPLAYER().getUniqueId());

        String title = AuditLogType.valueOf(DATA.getType()).getTitle(getPLAYER());
        String description = AuditLogType.valueOf(DATA.getType()).getDescription(getPLAYER());

        ItemBuilder builder = new ItemBuilder(getSTACK())
                .setDisplayName(TextUtility.legacyColor(getDATA().getNAME().replace("<audit_title>", title), locale, getPLAYER(), DATA.getReplacements()));

        for (String loreLine : getDATA().getLORE()) {
            if (loreLine.contains("<audit_description>")) {
                for (String part : TextUtility.toParts(description)) {
                    builder.addLoreLines(TextUtility.legacyColor(part, locale, getPLAYER(), DATA.getReplacements()));
                }

                continue;
            }

            builder.addLoreLines(TextUtility.legacyColor(loreLine,
                    locale,
                    getPLAYER(),
                    "timestamp",
                    Messages.replace(Messages.AUDIT_FACTION_TIMESTAMP_FORMAT.getString(locale), locale, getPLAYER(), "<time>", TextUtility.formatExtendedElapsedTime(DATA.getTimestamp()))
            ));
        }

        return builder;
    }
}
