package net.skullian.skyfactions.gui.screens.obelisk.member;

import net.skullian.skyfactions.api.GUIAPI;
import net.skullian.skyfactions.config.types.GUIEnums;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.gui.data.GUIData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

import java.util.List;

public class MemberManageRankUI {

    public static void promptPlayer(Player player, Faction faction) {
        try {
            GUIData data = GUIAPI.getGUIData(GUIEnums.OBELISK_MANAGE_MEMBER_RANK_GUI.getPath(), player);
            Gui.Builder.Normal gui = registerItems(Gui.normal()
                    .setStructure(data.getLAYOUT()), player, faction);

            Window window = Window.single()
                    .setViewer(player)
                    .setTitle(TextUtility.legacyColor(data.getTITLE(), PlayerHandler.getLocale(player.getUniqueId()), player))
                    .setGui(gui)
                    .build();

            SoundUtil.playSound(player, data.getOPEN_SOUND(), data.getOPEN_PITCH(), 1f);
            window.open();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, PlayerHandler.getLocale(player.getUniqueId()), "operation", "open the member rank management GUI", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    private static Gui.Builder.Normal registerItems(Gui.Builder.Normal builder, Player player, Faction faction) {
        try {
            List<ItemData> data = GUIAPI.getItemData(GUIEnums.OBELISK_MANAGE_MEMBER_RANK_GUI.getPath(), player);
            for (ItemData itemData : data) {
                switch (itemData : data) {
                    switch (itemData.getITEM_ID()) {

                    }
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
