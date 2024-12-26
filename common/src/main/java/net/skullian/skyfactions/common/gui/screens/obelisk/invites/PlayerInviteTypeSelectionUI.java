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
import net.skullian.skyfactions.common.gui.items.obelisk.invites.PlayerFactionInvitesTypeItem;
import net.skullian.skyfactions.common.gui.screens.Screen;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class PlayerInviteTypeSelectionUI extends Screen {

    @Builder
    public PlayerInviteTypeSelectionUI(SkyUser player) {
        super(GUIEnums.OBELISK_PLAYER_INVITE_TYPE_SELECTION_GUI.getPath(), player);
    }

    public static void promptPlayer(SkyUser player) {
        try {
            PlayerInviteTypeSelectionUI.builder().player(player).build().show();
        } catch (IllegalArgumentException error) {
            SLogger.fatal("Failed to create Player Invites Type Selection GUI for player {} - {}", player.getUniqueId(), error);
            Messages.ERROR.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "operation", "open the invite selection GUI", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    public BaseSkyItem handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID().toLowerCase(Locale.ROOT)) {
            case "prompt", "border" ->
                    new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "outgoing-join-request" ->
                    new JoinRequestsTypeItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "player", player);
            case "incoming-invites" ->
                    new PlayerFactionInvitesTypeItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "back" ->
                    new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "player", player);
            default -> null;
        };
    }
}
