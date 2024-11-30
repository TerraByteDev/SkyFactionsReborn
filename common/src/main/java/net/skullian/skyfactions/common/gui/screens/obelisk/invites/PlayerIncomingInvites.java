package net.skullian.skyfactions.common.gui.screens.obelisk.invites;

import lombok.Builder;
import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.api.InvitesAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.GUIEnums;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.PaginationItemData;
import net.skullian.skyfactions.common.gui.items.EmptyItem;
import net.skullian.skyfactions.common.gui.items.PaginationBackItem;
import net.skullian.skyfactions.common.gui.items.PaginationForwardItem;
import net.skullian.skyfactions.common.gui.items.impl.BaseSkyItem;
import net.skullian.skyfactions.common.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.common.gui.items.obelisk.invites.PlayerFactionInvitePaginationItem;
import net.skullian.skyfactions.common.gui.screens.PaginatedScreen;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PlayerIncomingInvites extends PaginatedScreen {
    private final List<InviteData> inviteData;

    @Builder
    public PlayerIncomingInvites(SkyUser player, List<InviteData> inviteData) {
        super(GUIEnums.OBELISK_PLAYER_INCOMING_INVITES_GUI.getPath(), player);
        this.inviteData = inviteData;

        ;
    }

    public static void promptPlayer(SkyUser player) {
        InvitesAPI.getPlayerIncomingInvites(player.getUniqueId()).whenComplete((inviteData, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your invites", "SQL_INVITE_GET", ex);
                return;
            }

            try {
                player.addMetadata("inFactionRelatedUI");
                PlayerIncomingInvites.builder().player(player).inviteData(inviteData).build().show();
            } catch (IllegalArgumentException error) {
                error.printStackTrace();
                Messages.ERROR.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "operation", "open the incoming faction invites GUI", "debug", "GUI_LOAD_EXCEPTION");
            }
        });
    }

    @Nullable
    @Override
    public BaseSkyItem handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID()) {
            case "PROMPT", "BORDER" ->
                    new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "BACK" ->
                    new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "player", player);
            default -> null;
        };
    }

    @Nullable
    @Override
    protected BaseSkyItem handlePaginationItem(@NotNull PaginationItemData paginationItem) {
        return switch (paginationItem.getITEM_ID()) {
            case "FORWARD_BUTTON" ->
                    new PaginationForwardItem(paginationItem, player);
            case "BACK_BUTTON" ->
                    new PaginationBackItem(paginationItem, player);
            default -> null;
        };
    }

    @NotNull
    @Override
    protected List<BaseSkyItem> getModels(SkyUser player, ItemData itemData) {
        List<BaseSkyItem> items = new ArrayList<>();
        for (InviteData data : inviteData) {
            itemData.setNAME(itemData.getNAME().replace("<faction_name>", data.getFactionName()));
            items.add(new PlayerFactionInvitePaginationItem(itemData, GUIAPI.createItem(itemData, data.getInviter().getUniqueId()), player, data));
        }

        return items;
    }
}
