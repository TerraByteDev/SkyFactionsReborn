package net.skullian.skyfactions.core.gui.screens.obelisk.invites;

import lombok.Builder;
import net.skullian.skyfactions.core.api.GUIAPI;
import net.skullian.skyfactions.core.config.types.GUIEnums;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.EmptyItem;
import net.skullian.skyfactions.core.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.core.gui.items.obelisk.invites.JoinRequestsTypeItem;
import net.skullian.skyfactions.core.gui.items.obelisk.invites.OutgoingInvitesTypeItem;
import net.skullian.skyfactions.core.gui.screens.Screen;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;

public class FactionInviteTypeSelectionUI extends Screen {

    @Builder
    public FactionInviteTypeSelectionUI(Player player) {
        super(player, GUIEnums.OBELISK_INVITE_SELECTION_GUI.getPath());

        initWindow();
    }

    public static void promptPlayer(Player player) {
        try {
            FactionInviteTypeSelectionUI.builder().player(player).build().show();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "open the invite selection GUI", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    protected Item handleItem(@NotNull ItemData itemData) {
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
