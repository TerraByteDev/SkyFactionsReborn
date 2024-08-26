package net.skullian.torrent.skyfactions.gui.obelisk.member;

import net.skullian.torrent.skyfactions.api.FactionAPI;
import net.skullian.torrent.skyfactions.api.GUIAPI;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.faction.Faction;
import net.skullian.torrent.skyfactions.gui.data.GUIData;
import net.skullian.torrent.skyfactions.gui.data.ItemData;
import net.skullian.torrent.skyfactions.gui.data.PaginationItemData;
import net.skullian.torrent.skyfactions.gui.items.GeneralBorderItem;
import net.skullian.torrent.skyfactions.gui.items.GeneralPromptItem;
import net.skullian.torrent.skyfactions.gui.items.PaginationBackItem;
import net.skullian.torrent.skyfactions.gui.items.PaginationForwardItem;
import net.skullian.torrent.skyfactions.gui.items.obelisk.ObeliskBackItem;
import net.skullian.torrent.skyfactions.gui.items.obelisk.member_manage.MemberPaginationItem;
import net.skullian.torrent.skyfactions.util.SoundUtil;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.List;

public class MemberManagementUI {

    public static void promptPlayer(Player player) {
        try {
            GUIData data = GUIAPI.getGUIData("obelisk/member_management");
            PagedGui.Builder gui = registerItems(PagedGui.items()
                    .setStructure(data.getLAYOUT()), player);

            Window window = Window.single()
                    .setViewer(player)
                    .setTitle(TextUtility.color(data.getTITLE()))
                    .setGui(gui)
                    .build();

            SoundUtil.playSound(player, data.getOPEN_SOUND(), data.getOPEN_PITCH(), 1f);
            window.open();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "open the member management GUI", "%debug%", "GUI_LOAD_EXCEPTION");
        }
    }

    private static PagedGui.Builder registerItems(PagedGui.Builder builder, Player player) {
        try {
            builder.addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL);
            List<ItemData> data = GUIAPI.getItemData("obelisk/member_management", player);
            List<PaginationItemData> paginationData = GUIAPI.getPaginationData(player);

            for (ItemData itemData : data) {
                switch (itemData.getITEM_ID()) {
                    case "PROMPT":
                        builder.addIngredient(itemData.getCHARACTER(), new GeneralPromptItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId())));
                        break;

                    case "MODEL":
                        builder.setContent(getItems(player, itemData));
                        break;

                    case "BACK":
                        builder.addIngredient(itemData.getCHARACTER(), new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "faction"));
                        break;

                    case "BORDER":
                        builder.addIngredient(itemData.getCHARACTER(), new GeneralBorderItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId())));
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

            return builder;
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        return builder;
    }

    private static List<Item> getItems(Player player, ItemData data) {
        List<Item> items = new ArrayList<>();
        Faction faction = FactionAPI.getFaction(player);

        OfflinePlayer owner = faction.getOwner();
        List<OfflinePlayer> admins = faction.getAdmins();
        List<OfflinePlayer> moderators = faction.getModerators();
        List<OfflinePlayer> fighters = faction.getFighters();
        List<OfflinePlayer> members = faction.getMembers();

        data.setNAME(data.getNAME().replace("%player_name%", owner.getName()));
        items.add(new MemberPaginationItem(data, GUIAPI.createItem(data, owner.getUniqueId()), Messages.FACTION_OWNER_TITLE.get(), owner, player));

        for (OfflinePlayer admin : admins) {
            data.setNAME(data.getNAME().replace("%player_name%", admin.getName()));
            items.add(new MemberPaginationItem(data, GUIAPI.createItem(data, admin.getUniqueId()), Messages.FACTION_ADMIN_TITLE.get(), admin, player));
        }
        for (OfflinePlayer moderator : moderators) {
            data.setNAME(data.getNAME().replace("%player_name%", moderator.getName()));
            items.add(new MemberPaginationItem(data, GUIAPI.createItem(data, moderator.getUniqueId()), Messages.FACTION_MODERATOR_TITLE.get(), moderator, player));
        }
        for (OfflinePlayer fighter : fighters) {
            data.setNAME(data.getNAME().replace("%player_name%", fighter.getName()));
            items.add(new MemberPaginationItem(data, GUIAPI.createItem(data, fighter.getUniqueId()), Messages.FACTION_FIGHTER_TITLE.get(), fighter, player));
        }
        for (OfflinePlayer member : members) {
            data.setNAME(data.getNAME().replace("%player_name%", member.getName()));
            items.add(new MemberPaginationItem(data, GUIAPI.createItem(data, member.getUniqueId()), Messages.FACTION_MEMBER_TITLE.get(), member, player));
        }

        return items;
    }
}
