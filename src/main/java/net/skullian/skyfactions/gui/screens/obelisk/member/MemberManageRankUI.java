package net.skullian.skyfactions.gui.screens.obelisk.member;

import net.skullian.skyfactions.api.GUIAPI;
import net.skullian.skyfactions.config.types.GUIEnums;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.faction.RankType;
import net.skullian.skyfactions.gui.data.GUIData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.EmptyItem;
import net.skullian.skyfactions.gui.items.obelisk.member_manage.rank.MemberRankChangeConfirmationItem;
import net.skullian.skyfactions.gui.items.obelisk.member_manage.rank.MemberRankChangeItem;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.List;

public class MemberManageRankUI {

    private final List<MemberRankChangeItem> items = new ArrayList<>();
    private MemberRankChangeConfirmationItem confirmItem = null;

    public void promptPlayer(Player player, Faction faction, OfflinePlayer subject) {
        try {
            GUIData data = GUIAPI.getGUIData(GUIEnums.OBELISK_MANAGE_MEMBER_RANK_GUI.getPath(), player);
            Gui.Builder.Normal gui = registerItems(Gui.normal()
                    .setStructure(data.getLAYOUT()), player, faction, subject);

            Window window = Window.single()
                    .setViewer(player)
                    .setTitle(TextUtility.legacyColor(data.getTITLE().replaceAll("<player_name>", subject.getName()), PlayerHandler.getLocale(player.getUniqueId()), player))
                    .setGui(gui)
                    .build();

            SoundUtil.playSound(player, data.getOPEN_SOUND(), data.getOPEN_PITCH(), 1f);
            window.open();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, PlayerHandler.getLocale(player.getUniqueId()), "operation", "open the member rank management GUI", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    private Gui.Builder.Normal registerItems(Gui.Builder.Normal builder, Player player, Faction faction, OfflinePlayer subject) {
        try {
            List<ItemData> data = GUIAPI.getItemData(GUIEnums.OBELISK_MANAGE_MEMBER_RANK_GUI.getPath(), player);
            for (ItemData itemData : data) {
                switch (itemData.getITEM_ID()) {
                    case "BORDER", "PLAYER_HEAD":
                        builder.addIngredient(itemData.getCHARACTER(), new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player));
                        break;

                    case "MEMBER", "FIGHTER", "MODERATOR", "ADMIN":
                        MemberRankChangeItem item = new MemberRankChangeItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player, RankType.valueOf(itemData.getITEM_ID()), faction, subject, this);
                        builder.addIngredient(itemData.getCHARACTER(), item);

                        this.items.add(item);
                        break;

                    case "CONFIRM":
                        MemberRankChangeConfirmationItem confirmationItem = new MemberRankChangeConfirmationItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player, faction, subject, faction.getRankType(player.getUniqueId()));
                        builder.addIngredient(itemData.getCHARACTER(), confirmationItem);

                        this.confirmItem = confirmationItem;
                        break;
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return builder;
    }

    public void onSelect(RankType newRank) {
        confirmItem.setSELECTED(newRank);
        for (MemberRankChangeItem item : this.items) {
            item.setTYPE(newRank);
        }
    }
}
