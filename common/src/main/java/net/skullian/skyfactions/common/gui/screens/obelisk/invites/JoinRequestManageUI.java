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
import net.skullian.skyfactions.common.gui.items.obelisk.invites.FactionJoinRequestAcceptItem;
import net.skullian.skyfactions.common.gui.items.obelisk.invites.FactionJoinRequestRejectItem;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JoinRequestManageUI extends Screen {
    private final InviteData inviteData;

    @Builder
    public JoinRequestManageUI(SkyUser player, InviteData inviteData) {
        super(GUIEnums.OBELISK_JOIN_REQUEST_MANAGE_GUI.getPath(), player);
        this.inviteData = inviteData;

        init();
    }

    public static void promptPlayer(SkyUser player, InviteData inviteData) {
        try {
            JoinRequestManageUI.builder().player(player).inviteData(inviteData).build().show();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "operation", "open the join request manage GUI", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    protected BaseSkyItem handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID()) {
            case "PROMPT", "BORDER" ->
                    new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "BACK" ->
                    new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "faction", player);
            case "REJECT" ->
                    new FactionJoinRequestRejectItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), inviteData, player);
            case "ACCEPT" ->
                    new FactionJoinRequestAcceptItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), inviteData, player);
            default -> null;
        };
    }
}
