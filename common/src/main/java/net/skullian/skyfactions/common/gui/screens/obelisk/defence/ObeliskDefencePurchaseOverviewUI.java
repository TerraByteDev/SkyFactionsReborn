package net.skullian.skyfactions.common.gui.screens.obelisk.defence;

import lombok.Builder;
import net.skullian.skyfactions.core.api.SpigotGUIAPI;
import net.skullian.skyfactions.core.config.types.GUIEnums;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.defence.DefencesFactory;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.data.PaginationItemData;
import net.skullian.skyfactions.core.gui.items.EmptyItem;
import net.skullian.skyfactions.core.gui.items.PaginationBackItem;
import net.skullian.skyfactions.core.gui.items.PaginationForwardItem;
import net.skullian.skyfactions.core.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.core.gui.items.obelisk.defence.ObeliskPaginatedDefenceItem;
import net.skullian.skyfactions.core.gui.screens.confirmation.PaginatedScreen;
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
            Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "open the defences purchase GUI", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    protected Item handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID()) {
            case "PROMPT", "BORDER" ->
                    new EmptyItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "BACK" ->
                    new ObeliskBackItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), obeliskType, player);
            default -> null;
        };
    }

    @Nullable
    @Override
    protected Item handlePaginationItem(@NotNull PaginationItemData paginationItem) {
        return switch (paginationItem.getITEM_ID()) {
            case "FORWARD_BUTTON" ->
                    new PaginationForwardItem(paginationItem, SpigotGUIAPI.createItem(paginationItem, player.getUniqueId()));
            case "BACK_BUTTON" ->
                    new PaginationBackItem(paginationItem, SpigotGUIAPI.createItem(paginationItem, player.getUniqueId()));
            default -> null;
        };
    }

    @NotNull
    @Override
    protected List<Item> getModels(Player player, ItemData data) {
        List<Item> items = new ArrayList<>();

        for (Map.Entry<String, DefenceStruct> defence : DefencesFactory.defences.getOrDefault(SpigotPlayerAPI.getLocale(player.getUniqueId()), DefencesFactory.getDefaultStruct()).entrySet()) {
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

            items.add(new ObeliskPaginatedDefenceItem(newData, SpigotGUIAPI.createItem(newData, player.getUniqueId()), struct, true, obeliskType, faction, player));
        }
        return items;
    }
}
