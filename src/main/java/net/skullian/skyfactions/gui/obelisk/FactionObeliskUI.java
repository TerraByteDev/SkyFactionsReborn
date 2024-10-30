package net.skullian.skyfactions.gui.obelisk;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.GUIAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.gui.data.GUIData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.EmptyItem;
import net.skullian.skyfactions.gui.items.obelisk.*;
import net.skullian.skyfactions.gui.items.obelisk.defence.ObeliskDefencePurchaseItem;
import net.skullian.skyfactions.util.ErrorHandler;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

import java.util.List;

public class FactionObeliskUI {

    public static void promptPlayer(Player player) {
        Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> {
            try {
                GUIData data = GUIAPI.getGUIData("obelisk/faction_obelisk", player);
                Gui.Builder.Normal gui = registerItems(Gui.normal()
                        .setStructure(data.getLAYOUT()), player);

                Window window = Window.single()
                        .setViewer(player)
                        .setTitle(TextUtility.color(data.getTITLE(), player))
                        .setGui(gui)
                        .build();

                SoundUtil.playSound(player, data.getOPEN_SOUND(), data.getOPEN_PITCH(), 1f);
                window.open();
            } catch (IllegalArgumentException error) {
                error.printStackTrace();
                Messages.ERROR.send(player, player.locale(), "%operation%", "open your obelisk", "%debug%", "GUI_LOAD_EXCEPTION");
            }
        });
    }

    private static Gui.Builder.Normal registerItems(Gui.Builder.Normal builder, Player player) {
        try {
            List<ItemData> data = GUIAPI.getItemData("obelisk/faction_obelisk", player);
            FactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, exc) -> {
                if (exc != null) {
                    ErrorHandler.handleError(player, "open your obelisk", "GUI_LOAD_EXCEPTION", exc);
                    return;
                } else if (faction == null) {
                    Messages.ERROR.send(player, player.locale(), "%operation%", "open your obelisk", "%debug%", "FACTION_NOT_FOUND");
                    return;
                }

                for (ItemData itemData : data) {
                    switch (itemData.getITEM_ID()) {

                        case "FACTION":
                            builder.addIngredient(itemData.getCHARACTER(), new ObeliskFactionOverviewItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player));
                            break;

                        case "DEFENCES":
                            builder.addIngredient(itemData.getCHARACTER(), new ObeliskDefencePurchaseItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "faction", faction));
                            break;

                        case "RUNES_CONVERSION":
                            builder.addIngredient(itemData.getCHARACTER(), new ObeliskRuneItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "faction", player));
                            break;

                        case "MEMBER_MANAGEMENT":
                            builder.addIngredient(itemData.getCHARACTER(), new ObeliskMemberManagementItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), faction));
                            break;

                        case "AUDIT_LOGS":
                            builder.addIngredient(itemData.getCHARACTER(), new ObeliskAuditLogItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId())));
                            break;

                        case "INVITES":
                            builder.addIngredient(itemData.getCHARACTER(), new ObeliskInvitesItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "faction", faction));
                            break;

                        case "BORDER":
                            builder.addIngredient(itemData.getCHARACTER(), new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId())));
                            break;
                    }
                }
            });

            return builder;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return builder;
    }
}
