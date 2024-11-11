package net.skullian.skyfactions.gui.screens.defence;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.GUIAPI;
import net.skullian.skyfactions.config.types.GUIEnums;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.defence.struct.DefenceData;
import net.skullian.skyfactions.defence.struct.DefenceStruct;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.gui.data.GUIData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.EmptyItem;
import net.skullian.skyfactions.gui.items.defence.*;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

import java.util.List;

public class DefenceManageUI {

    public static void promptPlayer(Player player, DefenceData defenceData, DefenceStruct struct, Faction faction) {
        Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> {
            try {
                GUIData data = GUIAPI.getGUIData(GUIEnums.DEFENCE_MANAGEMENT_UI.getInternalPath(), player);
                Gui.Builder.Normal gui = registerItems(Gui.normal()
                        .setStructure(data.getLAYOUT()), player, struct, defenceData, faction);

                Window window = Window.single()
                        .setViewer(player)
                        .setTitle(TextUtility.legacyColor(data.getTITLE(), PlayerHandler.getLocale(player.getUniqueId()), player))
                        .setGui(gui)
                        .build();

                SoundUtil.playSound(player, data.getOPEN_SOUND(), data.getOPEN_PITCH(), 1f);
                window.open();
            } catch (IllegalArgumentException error) {
                error.printStackTrace();
                Messages.ERROR.send(player, PlayerHandler.getLocale(player.getUniqueId()), "operation", "open your defence management GUI", "debug", "GUI_LOAD_EXCEPTION");
            }
        });
    }

    private static Gui.Builder.Normal registerItems(Gui.Builder.Normal builder, Player player, DefenceStruct struct, DefenceData defenceData, @Nullable Faction faction) {
        try {
            List<ItemData> data = GUIAPI.getItemData(GUIEnums.DEFENCE_MANAGEMENT_UI.getInternalPath(), player);
            for (ItemData itemData : data) {
                switch (itemData.getITEM_ID()) {

                    case "DEFENCE":
                        builder.addIngredient(itemData.getCHARACTER(), new DefenceDisplayItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player, struct, defenceData));
                        break;

                    case "AMMO":
                        builder.addIngredient(itemData.getCHARACTER(), new DefenceAmmoItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player, defenceData, faction));
                        break;

                    case "PASSIVE_TOGGLE":
                        builder.addIngredient(itemData.getCHARACTER(), new DefencePassiveToggleItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player, defenceData, faction));
                        break;

                    case "HOSTILE_TOGGLE":
                        builder.addIngredient(itemData.getCHARACTER(), new DefenceHostileToggleItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player, defenceData, faction));
                        break;

                    case "UPGRADE":
                        builder.addIngredient(itemData.getCHARACTER(), new DefenceUpgradeItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player, struct, defenceData, faction));
                        break;

                    case "REMOVE":
                        builder.addIngredient(itemData.getCHARACTER(), new DefenceRemoveItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player, struct, defenceData, faction));
                        break;

                    case "BORDER":
                        builder.addIngredient(itemData.getCHARACTER(), new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player));
                        break;
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return builder;
    }
}
