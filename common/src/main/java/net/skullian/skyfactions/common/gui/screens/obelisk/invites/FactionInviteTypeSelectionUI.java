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
import net.skullian.skyfactions.common.gui.items.obelisk.invites.JoinRequestsTypeItem;
import net.skullian.skyfactions.common.gui.items.obelisk.invites.OutgoingInvitesTypeItem;
import net.skullian.skyfactions.common.gui.screens.Screen;
import net.skullian.skyfactions.common.user.SkyUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FactionInviteTypeSelectionUI extends Screen {

    @Builder
    public FactionInviteTypeSelectionUI(SkyUser player) {
        super(GUIEnums.OBELISK_INVITE_SELECTION_GUI.getPath(), player);

        ;
    }

    public static void promptPlayer(SkyUser player) {
        try {
            FactionInviteTypeSelectionUI.builder().player(player).build().show();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "operation", "open the invite selection GUI", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    public BaseSkyItem handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID()) {
            case "PROMPT", "BORDER" ->
                    new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "OUTGOING_INVITES" ->
                    new OutgoingInvitesTypeItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "INCOMING_INVITES" ->
                    new JoinRequestsTypeItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "faction", player);
            case "BACK" ->
                    new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "faction", player);
            default -> null;
        };
    }
}
