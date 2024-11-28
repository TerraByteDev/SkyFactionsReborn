package net.skullian.skyfactions.core.gui.screens.obelisk.member;

import lombok.Builder;
import net.skullian.skyfactions.core.api.SpigotGUIAPI;
import net.skullian.skyfactions.core.config.types.GUIEnums;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.faction.RankType;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.EmptyItem;
import net.skullian.skyfactions.core.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.core.gui.items.obelisk.member_manage.rank.MemberRankChangeConfirmationItem;
import net.skullian.skyfactions.core.gui.items.obelisk.member_manage.rank.MemberRankChangeItem;
import net.skullian.skyfactions.core.gui.screens.Screen;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;

import java.util.ArrayList;
import java.util.List;

public class MemberManageRankUI extends Screen {
    private final List<MemberRankChangeItem> items = new ArrayList<>();
    private MemberRankChangeConfirmationItem confirmItem = null;
    private final Faction faction;
    private final OfflinePlayer subject;


    @Builder
    public MemberManageRankUI(Player player, Faction faction, OfflinePlayer subject) {
        super(player, GUIEnums.OBELISK_MANAGE_MEMBER_RANK_GUI.getPath());
        this.faction = faction;
        this.subject = subject;

        initWindow();
    }

    public static void promptPlayer(Player player, Faction faction, OfflinePlayer subject) {
        try {
            MemberManageRankUI.builder().player(player).faction(faction).subject(subject).build().show();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "open the member rank management GUI", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    public void onSelect(RankType newRank) {
        confirmItem.setSELECTED(newRank);
        for (MemberRankChangeItem item : this.items) {
            item.setTYPE(newRank);
            item.onSelect();
        }
    }

    @Nullable
    @Override
    protected Item handleItem(@NotNull ItemData itemData) {
        if (itemData.getITEM_ID().equals("CONFIRM")) {
            MemberRankChangeConfirmationItem confirmationItem = new MemberRankChangeConfirmationItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), player, faction, subject, faction.getRankType(player.getUniqueId()));

            this.confirmItem = confirmationItem;
            return confirmationItem;
        }

        if (List.of("MEMBER", "FIGHTER", "MODERATOR", "ADMIN").contains(itemData.getITEM_ID())) {
            MemberRankChangeItem item = new MemberRankChangeItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), player, RankType.valueOf(itemData.getITEM_ID()), faction, subject, this);

            this.items.add(item);
            return item;
        }

        return switch (itemData.getITEM_ID()) {
            case "BORDER", "PLAYER_HEAD" ->
                    new EmptyItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "BACK" ->
                    new ObeliskBackItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), "faction", player);
            default -> null;
        };
    }
}
