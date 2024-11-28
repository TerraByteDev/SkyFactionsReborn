package net.skullian.skyfactions.core.gui.screens.obelisk;

import lombok.Builder;
import net.skullian.skyfactions.core.api.SpigotGUIAPI;
import net.skullian.skyfactions.core.config.types.GUIEnums;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.EmptyItem;
import net.skullian.skyfactions.core.gui.items.obelisk.ObeliskHeadItem;
import net.skullian.skyfactions.core.gui.items.obelisk.ObeliskInvitesItem;
import net.skullian.skyfactions.core.gui.items.obelisk.ObeliskRuneItem;
import net.skullian.skyfactions.core.gui.items.obelisk.defence.ObeliskDefencePurchaseItem;
import net.skullian.skyfactions.core.gui.items.obelisk.notification.ObeliskPlayerNotificationsItem;
import net.skullian.skyfactions.core.gui.screens.Screen;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;

public class PlayerObeliskUI extends Screen {

    @Builder
    public PlayerObeliskUI(Player player) {
        super(player, GUIEnums.OBELISK_PLAYER_GUI.getPath());

        initWindow();
    }

    public static void promptPlayer(Player player) {
        try {
            PlayerObeliskUI.builder().player(player).build().show();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "open player obelisk", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    protected Item handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID()) {
            case "HEAD" -> new ObeliskHeadItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), player);

            case "DEFENCES" ->
                    new ObeliskDefencePurchaseItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), "player", null, player);

            case "RUNES_CONVERSION" ->
                    new ObeliskRuneItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), "player", player);

            case "INVITES" ->
                    new ObeliskInvitesItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), "player", null, player);

            case "NOTIFICATIONS" ->
                    new ObeliskPlayerNotificationsItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), player);

            case "BORDER" -> new EmptyItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), player);
            default -> null;
        };
    }
}
