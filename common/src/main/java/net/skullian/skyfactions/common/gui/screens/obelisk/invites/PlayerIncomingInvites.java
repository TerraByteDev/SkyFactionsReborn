package net.skullian.skyfactions.common.gui.screens.obelisk.invites;

import lombok.Builder;
import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.api.InvitesAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.GUIEnums;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.EmptyItem;
import net.skullian.skyfactions.common.gui.items.impl.BaseSkyItem;
import net.skullian.skyfactions.common.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.common.gui.items.obelisk.invites.PlayerFactionInvitePaginationItem;
import net.skullian.skyfactions.common.gui.screens.PaginatedScreen;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import net.skullian.skyfactions.common.util.SLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlayerIncomingInvites extends PaginatedScreen {
    private final List<InviteData> inviteData;

    @Builder
    public PlayerIncomingInvites(SkyUser player, List<InviteData> inviteData) {
        super(GUIEnums.OBELISK_PLAYER_INCOMING_INVITES_GUI.getPath(), player);
        this.inviteData = inviteData;
    }

    public static void promptPlayer(SkyUser player) {
        InvitesAPI.getPlayerIncomingInvites(player.getUniqueId()).whenComplete((inviteData, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your invites", "SQL_INVITE_GET", ex);
                return;
            }

            try {
                player.addMetadata("inFactionRelatedUI", true);
                PlayerIncomingInvites.builder().player(player).inviteData(inviteData).build().show();
            } catch (IllegalArgumentException error) {
                SLogger.fatal("Failed to create Incoming Invites GUI for player {} - {}", player.getUniqueId(), error);
                Messages.ERROR.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "operation", "open the incoming faction invites GUI", "debug", "GUI_LOAD_EXCEPTION");
            }
        });
    }

    @Nullable
    @Override
    public BaseSkyItem handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID().toLowerCase(Locale.ROOT)) {
            case "prompt", "border" ->
                    new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "back" ->
                    new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "player", player);
            default -> null;
        };
    }

    @NotNull
    @Override
    public List<BaseSkyItem> getModels(SkyUser player, ItemData itemData) {
        List<BaseSkyItem> items = new ArrayList<>();
        for (InviteData data : inviteData) {
            itemData.setNAME(itemData.getNAME().replace("<faction_name>", data.getFactionName()));
            items.add(new PlayerFactionInvitePaginationItem(itemData, GUIAPI.createItem(itemData, data.getInviter().getUniqueId()), player, data));
        }

        return items;
    }
}
