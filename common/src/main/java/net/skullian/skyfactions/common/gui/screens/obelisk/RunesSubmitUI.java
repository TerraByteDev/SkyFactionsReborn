package net.skullian.skyfactions.common.gui.screens.obelisk;

import lombok.Builder;
import lombok.Getter;
import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.GUIEnums;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.EmptyItem;
import net.skullian.skyfactions.common.gui.items.impl.BaseSkyItem;
import net.skullian.skyfactions.common.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.common.gui.items.rune_submit.RuneSubmitItem;
import net.skullian.skyfactions.common.gui.screens.Screen;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.common.util.SkyItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Getter
public class RunesSubmitUI extends Screen {
    private final Map<Integer, SkyItemStack> inventory = new HashMap<>();
    private final String type;
    private int invSize;

    @Builder
    public RunesSubmitUI(SkyUser player, String type) {
        super(GUIEnums.RUNES_SUBMIT_GUI.getPath(), player);
        this.type = type;

        for (String row : guiData.getLAYOUT()) {
            this.invSize += (int) row.chars()
                    .filter(ch -> ch == '.')
                    .count();
        }
    }

    public static void promptPlayer(SkyUser player, String type) {
        try {
            SkyApi.getInstance().getUIShower().show(RunesSubmitUI.builder().player(player).type(type).build());
        } catch (IllegalArgumentException error) {
            SLogger.fatal("Failed to create Runes Submit GUI for player {} - {}", player.getUniqueId(), error);
            Messages.ERROR.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "operation", "open your runes submit GUI", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    public BaseSkyItem handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID().toLowerCase(Locale.ROOT)) {
            case "prompt", "border" ->
                    new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "back" ->
                    new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), type, player);
            case "submit" ->
                    new RuneSubmitItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), type, this, player);
            default -> null;
        };
    }
}
