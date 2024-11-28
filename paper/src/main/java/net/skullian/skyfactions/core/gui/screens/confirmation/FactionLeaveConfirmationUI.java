package net.skullian.skyfactions.core.gui.screens.confirmation;

import lombok.Builder;
import net.skullian.skyfactions.core.api.SpigotGUIAPI;
import net.skullian.skyfactions.core.config.types.GUIEnums;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.EmptyItem;
import net.skullian.skyfactions.core.gui.items.GeneralCancelItem;
import net.skullian.skyfactions.core.gui.items.faction_leave.LeaveConfirmationItem;
import net.skullian.skyfactions.core.gui.screens.Screen;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;

public class FactionLeaveConfirmationUI extends Screen {
    @Builder
    public FactionLeaveConfirmationUI(Player player) {
        super(player, GUIEnums.FACTION_LEAVE_GUI.getPath());

        initWindow();
    }

    public static void promptPlayer(Player player) {
        try {
            FactionLeaveConfirmationUI.builder().player(player).build().show();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "leave your faction", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    protected Item handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID()) {
            case "PROMPT", "BORDER" ->
                    new EmptyItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "CONFIRM" ->
                    new LeaveConfirmationItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "CANCEL" -> new GeneralCancelItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), player);
            default -> null;
        };
    }
}
