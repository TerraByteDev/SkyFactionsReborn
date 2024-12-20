package net.skullian.skyfactions.common.gui.screens.obelisk;

import lombok.Builder;
import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.GUIEnums;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.database.struct.AuditLogData;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.PaginationItemData;
import net.skullian.skyfactions.common.gui.items.EmptyItem;
import net.skullian.skyfactions.common.gui.items.PaginationBackItem;
import net.skullian.skyfactions.common.gui.items.PaginationForwardItem;
import net.skullian.skyfactions.common.gui.items.impl.BaseSkyItem;
import net.skullian.skyfactions.common.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.common.gui.items.obelisk.audit_log.AuditPaginationItem;
import net.skullian.skyfactions.common.gui.screens.PaginatedScreen;
import net.skullian.skyfactions.common.user.SkyUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FactionAuditLogUI extends PaginatedScreen {
    private final List<AuditLogData> auditLogData;

    @Builder
    public FactionAuditLogUI(SkyUser player, List<AuditLogData> auditLogData) {
        super(GUIEnums.OBELISK_AUDIT_LOG_GUI.getPath(), player);
        this.auditLogData = auditLogData;

        ;
    }

    public static void promptPlayer(SkyUser player, Faction faction) {
        List<AuditLogData> auditLogData = faction.getAuditLogs();

        try {
            FactionAuditLogUI.builder().player(player).auditLogData(auditLogData).build().show();
        } catch (Exception e) {
            e.printStackTrace();
            Messages.ERROR.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "operation", "open faction audit log", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    public BaseSkyItem handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID()) {
            case "PROMPT", "BORDER" ->
                    new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "BACK" ->
                    new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "faction", player);
            default -> null;
        };
    }

    @NotNull
    @Override
    public List<BaseSkyItem> getModels(SkyUser player, ItemData data) {
        List<BaseSkyItem> items = new ArrayList<>();
        for (AuditLogData auditLog : auditLogData) {
            items.add(new AuditPaginationItem(data, GUIAPI.createItem(data, auditLog.getPlayer().getUniqueId()), auditLog, player));
        }

        return items;
    }
}
