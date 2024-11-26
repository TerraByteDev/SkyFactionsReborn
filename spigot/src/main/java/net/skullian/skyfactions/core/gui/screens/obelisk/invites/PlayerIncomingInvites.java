package net.skullian.skyfactions.core.gui.screens.obelisk.invites;

import lombok.Builder;
import net.skullian.skyfactions.core.SkyFactionsReborn;
import net.skullian.skyfactions.core.api.GUIAPI;
import net.skullian.skyfactions.core.api.SpigotInvitesAPI;
import net.skullian.skyfactions.core.config.types.GUIEnums;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.data.PaginationItemData;
import net.skullian.skyfactions.core.gui.items.EmptyItem;
import net.skullian.skyfactions.core.gui.items.PaginationBackItem;
import net.skullian.skyfactions.core.gui.items.PaginationForwardItem;
import net.skullian.skyfactions.core.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.core.gui.items.obelisk.invites.PlayerFactionInvitePaginationItem;
import net.skullian.skyfactions.core.gui.screens.PaginatedScreen;
import net.skullian.skyfactions.core.util.ErrorUtil;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;

import java.util.ArrayList;
import java.util.List;

public class PlayerIncomingInvites extends PaginatedScreen {
    private final List<InviteData> inviteData;

    @Builder
    public PlayerIncomingInvites(Player player, List<InviteData> inviteData) {
        super(player, GUIEnums.OBELISK_PLAYER_INCOMING_INVITES_GUI.getPath());
        this.inviteData = inviteData;

        initWindow();
    }

    public static void promptPlayer(Player player) {
        SpigotInvitesAPI.getPlayerIncomingInvites(player.getUniqueId()).whenComplete((inviteData, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your invites", "SQL_INVITE_GET", ex);
                return;
            }

            try {
                player.setMetadata("inFactionRelatedUI", new FixedMetadataValue(SkyFactionsReborn.getInstance(), true));
                PlayerIncomingInvites.builder().player(player).inviteData(inviteData).build().show();
            } catch (IllegalArgumentException error) {
                error.printStackTrace();
                Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "open the incoming faction invites GUI", "debug", "GUI_LOAD_EXCEPTION");
            }
        });
    }

    @Nullable
    @Override
    protected Item handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID()) {
            case "PROMPT", "BORDER" ->
                    new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "BACK" ->
                    new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "player", player);
            default -> null;
        };
    }

    @Nullable
    @Override
    protected Item handlePaginationItem(@NotNull PaginationItemData paginationItem) {
        return switch (paginationItem.getITEM_ID()) {
            case "FORWARD_BUTTON" ->
                    new PaginationForwardItem(paginationItem, GUIAPI.createItem(paginationItem, player.getUniqueId()));
            case "BACK_BUTTON" ->
                    new PaginationBackItem(paginationItem, GUIAPI.createItem(paginationItem, player.getUniqueId()));
            default -> null;
        };
    }

    @NotNull
    @Override
    protected List<Item> getModels(Player player, ItemData itemData) {
        List<Item> items = new ArrayList<>();
        for (InviteData data : inviteData) {
            itemData.setNAME(itemData.getNAME().replace("<faction_name>", data.getFactionName()));
            items.add(new PlayerFactionInvitePaginationItem(itemData, GUIAPI.createItem(itemData, data.getInviter().getUniqueId()), player, data));
        }

        return items;
    }
}
