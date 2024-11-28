package net.skullian.skyfactions.core.gui.screens.obelisk;

import lombok.Builder;
import net.skullian.skyfactions.core.api.SpigotGUIAPI;
import net.skullian.skyfactions.core.config.types.GUIEnums;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.common.database.struct.AuditLogData;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.data.PaginationItemData;
import net.skullian.skyfactions.core.gui.items.EmptyItem;
import net.skullian.skyfactions.core.gui.items.PaginationBackItem;
import net.skullian.skyfactions.core.gui.items.PaginationForwardItem;
import net.skullian.skyfactions.core.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.core.gui.items.obelisk.audit_log.AuditPaginationItem;
import net.skullian.skyfactions.core.gui.screens.PaginatedScreen;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;

import java.util.ArrayList;
import java.util.List;

public class FactionAuditLogUI extends PaginatedScreen {
    private final List<AuditLogData> auditLogData;

    @Builder
    public FactionAuditLogUI(Player player, List<AuditLogData> auditLogData) {
        super(player, GUIEnums.OBELISK_AUDIT_LOG_GUI.getPath());
        this.auditLogData = auditLogData;

        initWindow();
    }

    public static void promptPlayer(Player player, Faction faction) {
        List<AuditLogData> auditLogData = faction.getAuditLogs();

        try {
            FactionAuditLogUI.builder().player(player).auditLogData(auditLogData).build().show();
        } catch (Exception e) {
            e.printStackTrace();
            Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "open faction audit log", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    protected Item handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID()) {
            case "PROMPT", "BORDER" ->
                    new EmptyItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "BACK" ->
                    new ObeliskBackItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), "faction", player);
            default -> null;
        };
    }

    @Nullable
    @Override
    protected Item handlePaginationItem(@NotNull PaginationItemData paginationItem) {
        return switch (paginationItem.getITEM_ID()) {
            case "FORWARD_BUTTON" ->
                    new PaginationForwardItem(paginationItem, SpigotGUIAPI.createItem(paginationItem, player.getUniqueId()));
            case "BACK_BUTTON" ->
                    new PaginationBackItem(paginationItem, SpigotGUIAPI.createItem(paginationItem, player.getUniqueId()));
            default -> null;
        };
    }

    @NotNull
    @Override
    protected List<Item> getModels(Player player, ItemData data) {
        List<Item> items = new ArrayList<>();
        for (AuditLogData auditLog : auditLogData) {
            items.add(new AuditPaginationItem(data, SpigotGUIAPI.createItem(data, auditLog.getPlayer().getUniqueId()), auditLog, player));
        }

        return items;
    }
}
