package net.skullian.skyfactions.gui.screens.defence;

import lombok.Builder;
import net.skullian.skyfactions.api.GUIAPI;
import net.skullian.skyfactions.config.types.GUIEnums;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.defence.struct.DefenceData;
import net.skullian.skyfactions.defence.struct.DefenceStruct;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.EmptyItem;
import net.skullian.skyfactions.gui.items.defence.*;
import net.skullian.skyfactions.gui.screens.Screen;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;

public class DefenceManageUI extends Screen {
    private final DefenceData defenceData;
    private final DefenceStruct struct;
    private final Faction faction;

    @Builder
    public DefenceManageUI(Player player, DefenceData defenceData, DefenceStruct struct, Faction faction) {
        super(player, GUIEnums.DEFENCE_MANAGEMENT_UI.getPath());
        this.defenceData = defenceData;
        this.struct = struct;
        this.faction = faction;

        initWindow();
    }

    public static void promptPlayer(Player player, DefenceData defenceData, DefenceStruct struct, Faction faction) {
        try {
            DefenceManageUI.builder().player(player).defenceData(defenceData).struct(struct).faction(faction).build().show();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, PlayerHandler.getLocale(player.getUniqueId()), "operation", "open your defence management GUI", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    protected Item handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID()) {
            case "DEFENCE" ->
                    new DefenceDisplayItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player, struct, defenceData);
            case "AMMO" ->
                    new DefenceAmmoItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player, defenceData, faction);
            case "PASSIVE_TOGGLE" ->
                    new DefencePassiveToggleItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player, defenceData, faction);
            case "HOSTILE_TOGGLE" ->
                    new DefenceHostileToggleItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player, defenceData, faction);
            case "UPGRADE" ->
                    new DefenceUpgradeItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player, struct, defenceData, faction);
            case "REMOVE" ->
                    new DefenceRemoveItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player, struct, defenceData, faction);
            case "BORDER" -> new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            default -> null;
        };
    }
}
