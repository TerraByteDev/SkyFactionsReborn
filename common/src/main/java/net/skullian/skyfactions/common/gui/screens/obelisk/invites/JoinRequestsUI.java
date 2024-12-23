package net.skullian.skyfactions.common.gui.screens.obelisk.invites;

import lombok.Builder;
import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.GUIEnums;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.EmptyItem;
import net.skullian.skyfactions.common.gui.items.impl.BaseSkyItem;
import net.skullian.skyfactions.common.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.common.gui.items.obelisk.invites.FactionJoinRequestPaginationItem;
import net.skullian.skyfactions.common.gui.screens.PaginatedScreen;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import net.skullian.skyfactions.common.util.SLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class JoinRequestsUI extends PaginatedScreen {
    private final List<InviteData> inviteData;

    @Builder
    public JoinRequestsUI(SkyUser player, List<InviteData> inviteData) {
        super(GUIEnums.OBELISK_INVITE_INCOMING_GUI.getPath(), player);
        this.inviteData = inviteData;
    }

    public static void promptPlayer(SkyUser player) {
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());
        SkyApi.getInstance().getFactionAPI().getFaction(player.getUniqueId()).whenComplete((faction, exc) -> {
            if (exc != null) {
                ErrorUtil.handleError(player, "open the faction join requests GUI", "FACTION_NOT_FOUND", exc);
                return;
            } else if (faction == null) {
                Messages.ERROR.send(player, locale, "operation", "open the faction join requests GUI", "debug", "FACTION_NOT_FOUND");
                return;
            }

            List<InviteData> joinRequests = faction.getJoinRequests();
            try {
                JoinRequestsUI.builder().player(player).inviteData(joinRequests).build().show();
            } catch (IllegalArgumentException error) {
                SLogger.fatal("Failed to create Join Requests GUI for player {} - {}", player.getUniqueId(), error);
                Messages.ERROR.send(player, locale, "operation", "open the faction join requests GUI", "debug", "GUI_LOAD_EXCEPTION");
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
            items.add(new FactionJoinRequestPaginationItem(itemData, GUIAPI.createItem(itemData, data.getPlayer().getUniqueId()), player, data));
        }

        return items;
    }
}
