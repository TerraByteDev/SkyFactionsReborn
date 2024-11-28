package net.skullian.skyfactions.common.gui.screens.confirmation;

import lombok.Builder;
import net.skullian.skyfactions.core.api.SpigotGUIAPI;
import net.skullian.skyfactions.core.config.types.GUIEnums;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.EmptyItem;
import net.skullian.skyfactions.core.gui.items.GeneralCancelItem;
import net.skullian.skyfactions.core.gui.items.island_creation.CreationConfirmationItem;
import net.skullian.skyfactions.common.gui.screens.Screen;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;

public class IslandCreationConfirmationUI extends Screen {
    @Builder
    public IslandCreationConfirmationUI(Player player) {
        super(player, GUIEnums.CREATE_ISLAND_GUI.getPath());

        initWindow();
    }

    public static void promptPlayer(Player player) {
        try {
            IslandCreationConfirmationUI.builder().player(player).build().show();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "create your island", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    protected Item handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID()) {
            case "CANCEL" -> new GeneralCancelItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "CONFIRM" ->
                    new CreationConfirmationItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "PROMPT", "BORDER" ->
                    new EmptyItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), player);
            default -> null;
        };
    }
}
