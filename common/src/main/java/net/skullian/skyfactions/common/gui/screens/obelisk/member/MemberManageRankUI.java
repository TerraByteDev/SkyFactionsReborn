package net.skullian.skyfactions.common.gui.screens.obelisk.member;

import lombok.Builder;
import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.GUIEnums;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.faction.RankType;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.EmptyItem;
import net.skullian.skyfactions.common.gui.items.impl.BaseSkyItem;
import net.skullian.skyfactions.common.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.common.gui.items.obelisk.member_manage.rank.MemberRankChangeConfirmationItem;
import net.skullian.skyfactions.common.gui.items.obelisk.member_manage.rank.MemberRankChangeItem;
import net.skullian.skyfactions.common.gui.screens.Screen;
import net.skullian.skyfactions.common.user.SkyUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MemberManageRankUI extends Screen {
    private final List<MemberRankChangeItem> items = new ArrayList<>();
    private MemberRankChangeConfirmationItem confirmItem = null;
    private final Faction faction;
    private final SkyUser subject;


    @Builder
    public MemberManageRankUI(SkyUser player, Faction faction, SkyUser subject) {
        super(GUIEnums.OBELISK_MANAGE_MEMBER_RANK_GUI.getPath(), player);
        this.faction = faction;
        this.subject = subject;

        ;
    }

    public static void promptPlayer(SkyUser player, Faction faction, SkyUser subject) {
        try {
            MemberManageRankUI.builder().player(player).faction(faction).subject(subject).build().show();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "operation", "open the member rank management GUI", "debug", "GUI_LOAD_EXCEPTION");
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
    public BaseSkyItem handleItem(@NotNull ItemData itemData) {
        if (itemData.getITEM_ID().equals("CONFIRM")) {
            MemberRankChangeConfirmationItem confirmationItem = new MemberRankChangeConfirmationItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player, faction, subject, faction.getRankType(player.getUniqueId()));

            this.confirmItem = confirmationItem;
            return confirmationItem;
        }

        if (List.of("MEMBER", "FIGHTER", "MODERATOR", "ADMIN").contains(itemData.getITEM_ID())) {
            MemberRankChangeItem item = new MemberRankChangeItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player, RankType.valueOf(itemData.getITEM_ID()), faction, subject, this);

            this.items.add(item);
            return item;
        }

        return switch (itemData.getITEM_ID()) {
            case "BORDER", "PLAYER_HEAD" ->
                    new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "BACK" ->
                    new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "faction", player);
            default -> null;
        };
    }
}
