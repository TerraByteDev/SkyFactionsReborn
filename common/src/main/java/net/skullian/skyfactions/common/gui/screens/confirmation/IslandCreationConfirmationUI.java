package net.skullian.skyfactions.common.gui.screens.confirmation;

import lombok.Builder;
import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.GUIEnums;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.EmptyItem;
import net.skullian.skyfactions.common.gui.items.GeneralCancelItem;
import net.skullian.skyfactions.common.gui.items.impl.BaseSkyItem;
import net.skullian.skyfactions.common.gui.items.island_creation.CreationConfirmationItem;
import net.skullian.skyfactions.common.gui.screens.Screen;
import net.skullian.skyfactions.common.user.SkyUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IslandCreationConfirmationUI extends Screen {
    @Builder
    public IslandCreationConfirmationUI(SkyUser player) {
        super(GUIEnums.CREATE_ISLAND_GUI.getPath(), player);

        ;
    }

    public static void promptPlayer(SkyUser player) {
        try {
            IslandCreationConfirmationUI.builder().player(player).build().show();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Messages.ERROR.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "operation", "create your island", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    public BaseSkyItem handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID()) {
            case "CANCEL" -> new GeneralCancelItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "CONFIRM" ->
                    new CreationConfirmationItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "PROMPT", "BORDER" ->
                    new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            default -> null;
        };
    }
}
