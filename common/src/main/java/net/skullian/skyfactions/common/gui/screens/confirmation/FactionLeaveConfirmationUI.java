package net.skullian.skyfactions.common.gui.screens.confirmation;

import lombok.Builder;
import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.GUIEnums;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.EmptyItem;
import net.skullian.skyfactions.common.gui.items.GeneralCancelItem;
import net.skullian.skyfactions.common.gui.items.faction_leave.LeaveConfirmationItem;
import net.skullian.skyfactions.common.gui.items.impl.BaseSkyItem;
import net.skullian.skyfactions.common.gui.screens.Screen;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FactionLeaveConfirmationUI extends Screen {
    @Builder
    public FactionLeaveConfirmationUI(SkyUser player) {
        super(GUIEnums.FACTION_LEAVE_GUI.getPath(), player);
    }

    public static void promptPlayer(SkyUser player) {
        try {
            FactionLeaveConfirmationUI.builder().player(player).build().show();
        } catch (IllegalArgumentException error) {
            SLogger.fatal("Failed to create Faction Leave Confirmation GUI for player {} - {}", player.getUniqueId(), error);
            Messages.ERROR.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "operation", "leave your faction", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    public BaseSkyItem handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID()) {
            case "PROMPT", "BORDER" ->
                    new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "CONFIRM" ->
                    new LeaveConfirmationItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "CANCEL" -> new GeneralCancelItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            default -> null;
        };
    }
}
