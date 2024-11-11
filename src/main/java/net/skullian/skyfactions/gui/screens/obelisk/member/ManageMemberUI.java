package net.skullian.skyfactions.gui.screens.obelisk.member;

import java.util.List;

import net.skullian.skyfactions.config.types.GUIEnums;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.GUIAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.gui.data.GUIData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.EmptyItem;
import net.skullian.skyfactions.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.gui.items.obelisk.member_manage.MemberBanItem;
import net.skullian.skyfactions.gui.items.obelisk.member_manage.MemberKickItem;
import net.skullian.skyfactions.util.ErrorUtil;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

public class ManageMemberUI {

    public static void promptPlayer(Player player, OfflinePlayer subject) {
        try {
            GUIData data = GUIAPI.getGUIData(GUIEnums.OBELISK_MANAGE_MEMBER_GUI.getPath(), player);
            Gui.Builder.Normal gui = registerItems(Gui.normal()
                    .setStructure(data.getLAYOUT()), subject, player);

            Window window = Window.single()
                    .setViewer(player)
                    .setTitle(TextUtility.legacyColor(data.getTITLE(), PlayerHandler.getLocale(player.getUniqueId()), player))
                    .setGui(gui)
                    .build();

            SoundUtil.playSound(player, data.getOPEN_SOUND(), data.getOPEN_PITCH(), 1f);
            window.open();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Messages.ERROR.send(player, PlayerHandler.getLocale(player.getUniqueId()), "operation", "manage a member", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    private static Gui.Builder.Normal registerItems(Gui.Builder.Normal builder, OfflinePlayer player, Player actor) {
        try {
            FactionAPI.getFaction(actor.getUniqueId()).whenComplete((faction, exc) -> {
                if (exc != null) {
                    ErrorUtil.handleError(actor, "manage a member", "GUI_LOAD_EXCEPTION", exc);
                    return;
                }

                if (faction == null) {
                    Messages.ERROR.send(actor, actor.locale().getLanguage(), "operation", "manage a member", "debug", "FACTION_NOT_FOUND");
                }

                List<ItemData> data = GUIAPI.getItemData(GUIEnums.OBELISK_MANAGE_MEMBER_GUI.getPath(), player);
                for (ItemData itemData : data) {
                    switch (itemData.getITEM_ID()) {

                        case "BORDER", "PLAYER_HEAD":
                            builder.addIngredient(itemData.getCHARACTER(), new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), actor));
                            break;

                        case "KICK":
                            builder.addIngredient(itemData.getCHARACTER(), new MemberKickItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player, actor));
                            break;

                        case "BAN":
                            if (faction.isOwner(actor) || faction.isAdmin(actor)) {
                                builder.addIngredient(itemData.getCHARACTER(), new MemberBanItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player, actor));
                            }
                            break;

                        case "BACK":
                            builder.addIngredient(itemData.getCHARACTER(), new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "faction", actor));
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
