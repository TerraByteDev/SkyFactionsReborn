package net.skullian.skyfactions.common.gui.screens.obelisk.invites;

import lombok.Builder;
import net.skullian.skyfactions.core.api.SpigotGUIAPI;
import net.skullian.skyfactions.core.config.types.GUIEnums;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.EmptyItem;
import net.skullian.skyfactions.core.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.core.gui.items.obelisk.invites.InvitePromptItem;
import net.skullian.skyfactions.core.gui.items.obelisk.invites.PlayerIncomingInviteAccept;
import net.skullian.skyfactions.core.gui.items.obelisk.invites.PlayerIncomingInviteDeny;
import net.skullian.skyfactions.common.gui.screens.Screen;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;

public class PlayerManageIncomingInviteUI extends Screen {
    private final InviteData inviteData;

    @Builder
    public PlayerManageIncomingInviteUI(Player player, InviteData inviteData) {
        super(player, GUIEnums.OBELISK_PLAYER_INVITE_MANAGE_GUI.getPath());
        this.inviteData = inviteData;

        initWindow();
    }

    public static void promptPlayer(Player player, InviteData inviteData) {
        try {
            PlayerManageIncomingInviteUI.builder().player(player).inviteData(inviteData).build().show();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "manage an incoming Faction invite", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    protected Item handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID()) {
            case "PROMPT" ->
                    new InvitePromptItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), inviteData.getFactionName(), player);
            case "ACCEPT" ->
                    new PlayerIncomingInviteAccept(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), inviteData, player);
            case "DENY" ->
                    new PlayerIncomingInviteDeny(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), inviteData, player);
            case "BACK" ->
                    new ObeliskBackItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), "player", player);
            case "BORDER" -> new EmptyItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), player);
            default -> null;
        };
    }
}
