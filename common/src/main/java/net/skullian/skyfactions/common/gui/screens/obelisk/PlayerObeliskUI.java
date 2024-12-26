package net.skullian.skyfactions.common.gui.screens.obelisk;

import lombok.Builder;
import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.GUIEnums;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.EmptyItem;
import net.skullian.skyfactions.common.gui.items.impl.BaseSkyItem;
import net.skullian.skyfactions.common.gui.items.obelisk.ObeliskHeadItem;
import net.skullian.skyfactions.common.gui.items.obelisk.ObeliskInvitesItem;
import net.skullian.skyfactions.common.gui.items.obelisk.ObeliskRuneItem;
import net.skullian.skyfactions.common.gui.items.obelisk.defence.ObeliskDefencePurchaseItem;
import net.skullian.skyfactions.common.gui.items.obelisk.notification.ObeliskPlayerNotificationsItem;
import net.skullian.skyfactions.common.gui.screens.Screen;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class PlayerObeliskUI extends Screen {

    @Builder
    public PlayerObeliskUI(SkyUser player) {
        super(GUIEnums.OBELISK_PLAYER_GUI.getPath(), player);
    }

    public static void promptPlayer(SkyUser player) {
        try {
            PlayerObeliskUI.builder().player(player).build().show();
        } catch (IllegalArgumentException error) {
            SLogger.fatal("Failed to create Player Obelisk GUI for player {} - {}", player.getUniqueId(), error);
            Messages.ERROR.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "operation", "open player obelisk", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    public BaseSkyItem handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID().toLowerCase(Locale.ROOT)) {
            case "head" -> new ObeliskHeadItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);

            case "defences" ->
                    new ObeliskDefencePurchaseItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "player", null, player);

            case "runes-conversion" ->
                    new ObeliskRuneItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "player", player);

            case "invites" ->
                    new ObeliskInvitesItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "player", null, player);

            case "notifications" ->
                    new ObeliskPlayerNotificationsItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);

            case "border" -> new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            default -> null;
        };
    }
}
