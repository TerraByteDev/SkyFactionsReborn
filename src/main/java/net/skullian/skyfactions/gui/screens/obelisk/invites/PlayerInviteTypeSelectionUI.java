package net.skullian.skyfactions.gui.screens.obelisk.invites;

import lombok.Builder;
import net.skullian.skyfactions.api.GUIAPI;
import net.skullian.skyfactions.config.types.GUIEnums;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.EmptyItem;
import net.skullian.skyfactions.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.gui.items.obelisk.invites.JoinRequestsTypeItem;
import net.skullian.skyfactions.gui.items.obelisk.invites.PlayerFactionInvitesTypeItem;
import net.skullian.skyfactions.gui.screens.Screen;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;

public class PlayerInviteTypeSelectionUI extends Screen {
    @Builder
    public PlayerInviteTypeSelectionUI(Player player) {
        super(player, GUIEnums.OBELISK_PLAYER_INVITE_TYPE_SELECTION_GUI.getPath());

        initWindow();
    }

    public static void promptPlayer(Player player) {
        try {
            PlayerInviteTypeSelectionUI.builder().player(player).build().show();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, PlayerHandler.getLocale(player.getUniqueId()), "operation", "open the invite selection GUI", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    protected Item handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID()) {
            case "PROMPT", "BORDER" ->
                    new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "OUTGOING_JOIN_REQUEST" ->
                    new JoinRequestsTypeItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "player", player);
            case "INCOMING_INVITES" ->
                    new PlayerFactionInvitesTypeItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "BACK" ->
                    new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "player", player);
            default -> null;
        };
    }
}
