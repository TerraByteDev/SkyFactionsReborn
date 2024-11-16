package net.skullian.skyfactions.gui.screens.obelisk.defence;

import lombok.Builder;
import net.skullian.skyfactions.api.GUIAPI;
import net.skullian.skyfactions.config.types.GUIEnums;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.defence.DefencesFactory;
import net.skullian.skyfactions.defence.struct.DefenceStruct;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.data.PaginationItemData;
import net.skullian.skyfactions.gui.items.EmptyItem;
import net.skullian.skyfactions.gui.items.PaginationBackItem;
import net.skullian.skyfactions.gui.items.PaginationForwardItem;
import net.skullian.skyfactions.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.gui.items.obelisk.defence.ObeliskPaginatedDefenceItem;
import net.skullian.skyfactions.gui.screens.PaginatedScreen;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ObeliskDefencePurchaseOverviewUI extends PaginatedScreen {
    private final String obeliskType;
    private final Faction faction;

    @Builder
    public ObeliskDefencePurchaseOverviewUI(Player player, String obeliskType, Faction faction) {
        super(player, GUIEnums.OBELISK_DEFENCE_PURCHASE_OVERVIEW_GUI.getPath());
        this.obeliskType = obeliskType;
        this.faction = faction;

        initWindow();
    }

    public static void promptPlayer(Player player, String obeliskType, Faction faction) {
        try {
            ObeliskDefencePurchaseOverviewUI.builder().player(player).obeliskType(obeliskType).faction(faction).build().show();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, PlayerHandler.getLocale(player.getUniqueId()), "operation", "open the defences purchase GUI", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    protected Item handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID()) {
            case "PROMPT", "BORDER" ->
                    new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "BACK" ->
                    new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), obeliskType, player);
            default -> null;
        };
    }

    @Nullable
    @Override
    protected Item handlePaginationItem(@NotNull PaginationItemData paginationItem) {
        return switch (paginationItem.getITEM_ID()) {
            case "FORWARD_BUTTON" ->
                    new PaginationForwardItem(paginationItem, GUIAPI.createItem(paginationItem, player.getUniqueId()));
            case "BACK_BUTTON" ->
                    new PaginationBackItem(paginationItem, GUIAPI.createItem(paginationItem, player.getUniqueId()));
            default -> null;
        };
    }

    @NotNull
    @Override
    protected List<Item> getModels(Player player, ItemData data) {
        List<Item> items = new ArrayList<>();

        for (Map.Entry<String, DefenceStruct> defence : DefencesFactory.defences.getOrDefault(PlayerHandler.getLocale(player.getUniqueId()), DefencesFactory.getDefaultStruct()).entrySet()) {
            DefenceStruct struct = defence.getValue();
            ItemData newData = new ItemData(
                    data.getITEM_ID(),
                    data.getCHARACTER(),
                    struct.getNAME(),
                    struct.getITEM_MATERIAL(),
                    struct.getITEM_SKULL(),
                    data.getSOUND(),
                    data.getPITCH(),
                    struct.getITEM_LORE()
            );

            items.add(new ObeliskPaginatedDefenceItem(newData, GUIAPI.createItem(newData, player.getUniqueId()), struct, true, obeliskType, faction, player));
        }
        return items;
    }
}
