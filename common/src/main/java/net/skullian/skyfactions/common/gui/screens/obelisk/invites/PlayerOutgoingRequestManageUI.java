package net.skullian.skyfactions.common.gui.screens.obelisk.invites;

import lombok.Builder;
import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.GUIEnums;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.faction.JoinRequestData;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.AirItem;
import net.skullian.skyfactions.common.gui.items.EmptyItem;
import net.skullian.skyfactions.common.gui.items.impl.BaseSkyItem;
import net.skullian.skyfactions.common.gui.items.obelisk.invites.FactionPlayerJoinRequestConfirmItem;
import net.skullian.skyfactions.common.gui.items.obelisk.invites.FactionPlayerJoinRequestDenyItem;
import net.skullian.skyfactions.common.gui.items.obelisk.invites.FactionPlayerJoinRequestRevoke;
import net.skullian.skyfactions.common.gui.items.obelisk.invites.InvitePromptItem;
import net.skullian.skyfactions.common.gui.screens.Screen;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerOutgoingRequestManageUI extends Screen {
    private final JoinRequestData joinRequest;

    @Builder
    public PlayerOutgoingRequestManageUI(SkyUser player, JoinRequestData joinRequest) {
        super(GUIEnums.OBELISK_INVITE_OUTGOING_GUI.getPath(), player);
        this.joinRequest = joinRequest;
    }

    public static void promptPlayer(SkyUser player, JoinRequestData joinRequest) {
        try {
            player.addMetadata("inFactionRelatedUI", true);
            PlayerOutgoingRequestManageUI.builder().player(player).joinRequest(joinRequest).build().show();
        } catch (IllegalArgumentException error) {
            SLogger.fatal("Failed to create Outgoing Join Request Manage GUI for player {} - {}", player.getUniqueId(), error);
            Messages.ERROR.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "operation", "manage your outgoing join request", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    public BaseSkyItem handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID()) {
            case "PROMPT" ->
                    new InvitePromptItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), joinRequest.getFactionName(), player);
            case "ACCEPT" -> joinRequest.isAccepted() ?
                    new FactionPlayerJoinRequestConfirmItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), joinRequest, player) : new AirItem(player);
            case "DENY" -> joinRequest.isAccepted() ?
                    new FactionPlayerJoinRequestDenyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), joinRequest, player) : new AirItem(player);
            case "REVOKE" ->
                    new FactionPlayerJoinRequestRevoke(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), joinRequest, player);
            case "BORDER" -> new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            default -> null;
        };
    }
}
