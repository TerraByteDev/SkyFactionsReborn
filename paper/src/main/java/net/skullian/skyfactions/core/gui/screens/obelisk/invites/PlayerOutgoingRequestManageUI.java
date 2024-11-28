package net.skullian.skyfactions.core.gui.screens.obelisk.invites;

import lombok.Builder;
import net.skullian.skyfactions.core.SkyFactionsReborn;
import net.skullian.skyfactions.core.api.SpigotGUIAPI;
import net.skullian.skyfactions.core.config.types.GUIEnums;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.common.faction.JoinRequestData;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.AirItem;
import net.skullian.skyfactions.core.gui.items.EmptyItem;
import net.skullian.skyfactions.core.gui.items.obelisk.invites.FactionPlayerJoinRequestConfirmItem;
import net.skullian.skyfactions.core.gui.items.obelisk.invites.FactionPlayerJoinRequestDenyItem;
import net.skullian.skyfactions.core.gui.items.obelisk.invites.FactionPlayerJoinRequestRevoke;
import net.skullian.skyfactions.core.gui.items.obelisk.invites.InvitePromptItem;
import net.skullian.skyfactions.core.gui.screens.Screen;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
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
                player.setMetadata("inFactionRelatedUI", new FixedMetadataValue(SkyFactionsReborn.getInstance(), true));
                PlayerOutgoingRequestManageUI.builder().player(player).joinRequest(joinRequest).build().show();
            } catch (IllegalArgumentException error) {
                error.printStackTrace();
                Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "manage your outgoing join request", "debug", "GUI_LOAD_EXCEPTION");
            }
        });
    }

    @Nullable
    @Override
    protected Item handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID()) {
            case "PROMPT" ->
                    new InvitePromptItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), joinRequest.getFactionName(), player);
            case "ACCEPT" -> joinRequest.isAccepted() ?
                    new FactionPlayerJoinRequestConfirmItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), joinRequest, player) : new AirItem(player);
            case "DENY" -> joinRequest.isAccepted() ?
                    new FactionPlayerJoinRequestDenyItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), joinRequest, player) : new AirItem(player);
            case "REVOKE" ->
                    new FactionPlayerJoinRequestRevoke(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), joinRequest, player);
            case "BORDER" -> new EmptyItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), player);
            default -> null;
        };
    }
}
