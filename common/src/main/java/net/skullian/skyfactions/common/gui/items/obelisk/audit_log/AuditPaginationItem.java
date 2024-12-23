package net.skullian.skyfactions.common.gui.items.obelisk.audit_log;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.database.struct.AuditLogData;
import net.skullian.skyfactions.common.faction.AuditLogType;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;
import net.skullian.skyfactions.common.util.text.TextUtility;

import java.util.ArrayList;
import java.util.Collections;

public class AuditPaginationItem extends SkyItem {

    private final AuditLogData DATA;

    public AuditPaginationItem(ItemData data, SkyItemStack stack, AuditLogData auditData, SkyUser player) {
        super(data, stack, player, null);
        
        this.DATA = auditData;
    }

    @Override
    public SkyItemStack getItemStack() {
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(getPLAYER().getUniqueId());

        String title = AuditLogType.valueOf(DATA.getType()).getTitle(getPLAYER());
        String description = AuditLogType.valueOf(DATA.getType()).getDescription(getPLAYER());

        SkyItemStack.SkyItemStackBuilder builder = SkyItemStack.builder()
                .displayName(Messages.replace(getDATA().getNAME().replace("<audit_title>", title), getPLAYER()));

        for (String loreLine : getDATA().getLORE()) {
            if (loreLine.contains("<audit_description>")) {
                builder.lore(TextUtility.toParts(description));
            } else builder.lore(new ArrayList<>(Collections.singleton(
                    Messages.replace(loreLine,
                            getPLAYER(),
                            "<timestamp>",
                            Messages.replace(Messages.AUDIT_FACTION_TIMESTAMP_FORMAT.getString(locale), getPLAYER(), "<time>", TextUtility.formatExtendedElapsedTime(DATA.getTimestamp())))
            )));
        }

        return builder.build();
    }
}
