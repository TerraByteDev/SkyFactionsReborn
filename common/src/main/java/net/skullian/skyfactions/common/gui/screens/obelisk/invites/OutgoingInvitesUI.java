package net.skullian.skyfactions.common.gui.screens.obelisk.invites;

import lombok.Builder;
import net.skullian.skyfactions.common.api.GUIAPI;
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
import net.skullian.skyfactions.common.gui.items.obelisk.invites.OutgoingInvitePaginationItem;
import net.skullian.skyfactions.common.gui.screens.PaginatedScreen;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class OutgoingInvitesUI extends PaginatedScreen {
    private final List<InviteData> inviteData;

    @Builder
    public OutgoingInvitesUI(SkyUser player, List<InviteData> inviteData) {
        super(GUIEnums.OBELISK_INVITE_OUTGOING_GUI.getPath(), player);
        this.inviteData = inviteData;

        ;
    }

    public static void promptPlayer(SkyUser player) {
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());
        SkyApi.getInstance().getFactionAPI().getFaction(player.getUniqueId()).whenComplete((faction, exc) -> {
            if (exc != null) {
                ErrorUtil.handleError(player, "open the outgoing invites GUI", "GUI_LOAD_EXCEPTION", exc);
                return;
            } else if (faction == null) {
                Messages.ERROR.send(player, locale, "operation", "open the outgoing invutes GUI", "debug", "FACTION_NOT_FOUND");
                return;
            }

            List<InviteData> outgoingInvites = faction.getOutgoingInvites();
            try {
                OutgoingInvitesUI.builder().player(player).inviteData(outgoingInvites).build().show();
            } catch (IllegalArgumentException error) {
                error.printStackTrace();
                Messages.ERROR.send(player, locale, "operation", "open the outgoing invites GUI", "debug", "GUI_LOAD_EXCEPTION");
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
                    new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "faction", player);
            default -> null;
        };
    }

    @NotNull
    @Override
    public List<BaseSkyItem> getModels(SkyUser player, ItemData itemData) {
        List<BaseSkyItem> items = new ArrayList<>();
        for (InviteData data : inviteData) {
            itemData.setNAME(itemData.getNAME().replace("player_name", data.getPlayer().getName()));
            items.add(new OutgoingInvitePaginationItem(itemData, GUIAPI.createItem(itemData, data.getPlayer().getUniqueId()), data, player));
        }

        return items;
    }
}
