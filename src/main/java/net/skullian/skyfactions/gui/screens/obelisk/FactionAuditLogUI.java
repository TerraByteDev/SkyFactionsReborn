package net.skullian.skyfactions.gui.screens.obelisk;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.skullian.skyfactions.config.types.GUIEnums;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.GUIAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.database.struct.AuditLogData;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.gui.data.GUIData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.data.PaginationItemData;
import net.skullian.skyfactions.gui.items.EmptyItem;
import net.skullian.skyfactions.gui.items.PaginationBackItem;
import net.skullian.skyfactions.gui.items.PaginationForwardItem;
import net.skullian.skyfactions.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.gui.items.obelisk.audit_log.AuditPaginationItem;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

public class FactionAuditLogUI {

    public static void promptPlayer(Player player, Faction faction) {
        faction.getAuditLogs().whenComplete((auditLogData, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> {
                try {
                    GUIData data = GUIAPI.getGUIData(GUIEnums.OBELISK_AUDIT_LOG_GUI.getPath(), player);
                    PagedGui.Builder gui = registerItems(PagedGui.items()
                            .setStructure(data.getLAYOUT()), player, faction, auditLogData);

                    Window window = Window.single()
                            .setViewer(player)
                            .setTitle(TextUtility.legacyColor(data.getTITLE(), PlayerHandler.getLocale(player.getUniqueId()), player))
                            .setGui(gui)
                            .build();

                    SoundUtil.playSound(player, data.getOPEN_SOUND(), data.getOPEN_PITCH(), 1f);
                    window.open();
                } catch (IllegalArgumentException error) {
                    error.printStackTrace();
                    Messages.ERROR.send(player, PlayerHandler.getLocale(player.getUniqueId()), "operation", "open the audit log GUI", "debug", "GUI_LOAD_EXCEPTION");
                }
            });
        });
    }

    private static PagedGui.Builder registerItems(PagedGui.Builder builder, Player player, Faction faction, List<AuditLogData> auditLogData) {
        try {
            builder.addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL);
            List<ItemData> data = GUIAPI.getItemData(GUIEnums.OBELISK_AUDIT_LOG_GUI.getPath(), player);
            List<PaginationItemData> paginationData = GUIAPI.getPaginationData(player);

            for (ItemData itemData : data) {
                switch (itemData.getITEM_ID()) {
                    case "PROMPT", "BORDER":
                        builder.addIngredient(itemData.getCHARACTER(), new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player));
                        break;

                    case "BACK":
                        builder.addIngredient(itemData.getCHARACTER(), new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "faction", player));
                        break;

                    case "MODEL":
                        builder.setContent(getItems(player, itemData, faction, auditLogData));
                        break;
                }
            }
            for (PaginationItemData paginationItem : paginationData) {
                switch (paginationItem.getITEM_ID()) {

                    case "FORWARD_BUTTON":
                        builder.addIngredient(paginationItem.getCHARACTER(), new PaginationForwardItem(paginationItem, GUIAPI.createItem(paginationItem, player.getUniqueId())));
                        break;

                    case "BACK_BUTTON":
                        builder.addIngredient(paginationItem.getCHARACTER(), new PaginationBackItem(paginationItem, GUIAPI.createItem(paginationItem, player.getUniqueId())));
                        break;
                }
            }
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
        }

        return builder;
    }

    private static List<Item> getItems(Player player, ItemData data, Faction faction, List<AuditLogData> auditLogData) {
        List<Item> items = new ArrayList<>();

        for (AuditLogData auditLog : auditLogData) {
            items.add(new AuditPaginationItem(data, GUIAPI.createItem(data, auditLog.getPlayer().getUniqueId()), auditLog, player));
        }

        return items;
    }
}
