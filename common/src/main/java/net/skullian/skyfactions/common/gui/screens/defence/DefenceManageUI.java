package net.skullian.skyfactions.common.gui.screens.defence;

import lombok.Builder;
import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.GUIEnums;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.defence.struct.DefenceData;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.EmptyItem;
import net.skullian.skyfactions.common.gui.items.defence.*;
import net.skullian.skyfactions.common.gui.items.impl.BaseSkyItem;
import net.skullian.skyfactions.common.gui.screens.Screen;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class DefenceManageUI extends Screen {
    private final DefenceData defenceData;
    private final DefenceStruct struct;
    private final Faction faction;

    @Builder
    public DefenceManageUI(SkyUser player, DefenceData defenceData, DefenceStruct struct, Faction faction) {
        super(GUIEnums.DEFENCE_MANAGEMENT_UI.getPath(), player);
        this.defenceData = defenceData;
        this.struct = struct;
        this.faction = faction;
    }

    public static void promptPlayer(SkyUser player, DefenceData defenceData, DefenceStruct struct, Faction faction) {
        try {
            DefenceManageUI.builder().player(player).defenceData(defenceData).struct(struct).faction(faction).build().show();
        } catch (IllegalArgumentException error) {
            SLogger.fatal("Failed to create Defence Manage GUI for player {} - {}", player.getUniqueId(), error);
            Messages.ERROR.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "operation", "open your defence management GUI", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    public BaseSkyItem handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID().toLowerCase(Locale.ROOT)) {
            case "defence" ->
                    new DefenceDisplayItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player, struct, defenceData);
            case "ammo" ->
                    new DefenceAmmoItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player, defenceData, faction);
            case "passive-toggle" ->
                    new DefencePassiveToggleItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player, defenceData, faction);
            case "hostile-toggle" ->
                    new DefenceHostileToggleItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player, defenceData, faction);
            case "upgrade" ->
                    new DefenceUpgradeItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player, struct, defenceData, faction);
            case "remove" ->
                    new DefenceRemoveItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player, struct, defenceData, faction);
            case "border" -> new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            default -> null;
        };
    }
}
