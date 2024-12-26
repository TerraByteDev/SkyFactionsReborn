package net.skullian.skyfactions.common.gui.screens.confirmation;

import lombok.Builder;
import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.GUIEnums;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.EmptyItem;
import net.skullian.skyfactions.common.gui.items.impl.BaseSkyItem;
import net.skullian.skyfactions.common.gui.items.raid_start.RaidCancelItem;
import net.skullian.skyfactions.common.gui.items.raid_start.RaidConfirmationItem;
import net.skullian.skyfactions.common.gui.screens.Screen;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class PlayerRaidConfirmationUI extends Screen {

    @Builder
    public PlayerRaidConfirmationUI(SkyUser player) {
        super(GUIEnums.RAID_START_GUI.getPath(), player);
    }

    public static void promptPlayer(SkyUser player) {
        try {
            PlayerRaidConfirmationUI.builder().player(player).build().show();
        } catch (IllegalArgumentException error) {
            SLogger.fatal("Failed to create Player Raid Confirmation GUI for player {} - {}", player.getUniqueId(), error);
            Messages.ERROR.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "operation", "start a raid", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    public BaseSkyItem handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID().toLowerCase(Locale.ROOT)) {
            case "cancel" -> new RaidCancelItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "confirm" ->
                    new RaidConfirmationItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "prompt", "border" ->
                    new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            default -> null;
        };
    }
}
