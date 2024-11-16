package net.skullian.skyfactions.gui.screens.obelisk.invites;

import lombok.Builder;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.GUIAPI;
import net.skullian.skyfactions.config.types.GUIEnums;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.faction.JoinRequestData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.AirItem;
import net.skullian.skyfactions.gui.items.EmptyItem;
import net.skullian.skyfactions.gui.items.obelisk.invites.FactionPlayerJoinRequestConfirmItem;
import net.skullian.skyfactions.gui.items.obelisk.invites.FactionPlayerJoinRequestDenyItem;
import net.skullian.skyfactions.gui.items.obelisk.invites.FactionPlayerJoinRequestRevoke;
import net.skullian.skyfactions.gui.items.obelisk.invites.InvitePromptItem;
import net.skullian.skyfactions.gui.screens.Screen;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;

public class PlayerOutgoingRequestManageUI extends Screen {
    private final JoinRequestData joinRequest;

    @Builder
    public PlayerOutgoingRequestManageUI(Player player, JoinRequestData joinRequest) {
        super(player, GUIEnums.OBELISK_INVITE_OUTGOING_GUI.getPath());
        this.joinRequest = joinRequest;

        initWindow();
    }

    public static void promptPlayer(Player player, JoinRequestData joinRequest) {
        Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> {
            try {
                PlayerOutgoingRequestManageUI.builder().player(player).joinRequest(joinRequest).build().show();
            } catch (IllegalArgumentException error) {
                error.printStackTrace();
                Messages.ERROR.send(player, PlayerHandler.getLocale(player.getUniqueId()), "operation", "manage your outgoing join request", "debug", "GUI_LOAD_EXCEPTION");
            }
        });
    }

    @Nullable
    @Override
    protected Item handleItem(@NotNull ItemData itemData) {
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
