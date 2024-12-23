package net.skullian.skyfactions.common.gui.screens.obelisk.invites;

import lombok.Builder;
import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.GUIEnums;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.EmptyItem;
import net.skullian.skyfactions.common.gui.items.impl.BaseSkyItem;
import net.skullian.skyfactions.common.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.common.gui.items.obelisk.invites.InvitePromptItem;
import net.skullian.skyfactions.common.gui.items.obelisk.invites.PlayerIncomingInviteAccept;
import net.skullian.skyfactions.common.gui.items.obelisk.invites.PlayerIncomingInviteDeny;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.common.gui.screens.Screen;
import net.skullian.skyfactions.common.util.SLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerManageIncomingInviteUI extends Screen {
    private final InviteData inviteData;

    @Builder
    public PlayerManageIncomingInviteUI(SkyUser player, InviteData inviteData) {
        super(GUIEnums.OBELISK_PLAYER_INVITE_MANAGE_GUI.getPath(), player);
        this.inviteData = inviteData;
    }

    public static void promptPlayer(SkyUser player, InviteData inviteData) {
        try {
            PlayerManageIncomingInviteUI.builder().player(player).inviteData(inviteData).build().show();
        } catch (IllegalArgumentException error) {
            SLogger.fatal("Failed to create Incoming Invite Manage GUI for player {} - {}", player.getUniqueId(), error);
            Messages.ERROR.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "operation", "manage an incoming Faction invite", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    public BaseSkyItem handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID()) {
            case "PROMPT" ->
                    new InvitePromptItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), inviteData.getFactionName(), player);
            case "ACCEPT" ->
                    new PlayerIncomingInviteAccept(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), inviteData, player);
            case "DENY" ->
                    new PlayerIncomingInviteDeny(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), inviteData, player);
            case "BACK" ->
                    new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "player", player);
            case "BORDER" -> new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            default -> null;
        };
    }
}
