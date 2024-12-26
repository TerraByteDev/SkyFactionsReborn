package net.skullian.skyfactions.common.gui.screens.obelisk.defence;

import lombok.Builder;
import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.GUIEnums;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.EmptyItem;
import net.skullian.skyfactions.common.gui.items.impl.BaseSkyItem;
import net.skullian.skyfactions.common.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.common.gui.items.obelisk.defence.ObeliskPaginatedDefenceItem;
import net.skullian.skyfactions.common.gui.screens.PaginatedScreen;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ObeliskDefencePurchaseOverviewUI extends PaginatedScreen {
    private final String obeliskType;
    private final Faction faction;

    @Builder
    public ObeliskDefencePurchaseOverviewUI(SkyUser player, String obeliskType, Faction faction) {
        super(GUIEnums.OBELISK_DEFENCE_PURCHASE_OVERVIEW_GUI.getPath(), player);
        this.obeliskType = obeliskType;
        this.faction = faction;
    }

    public static void promptPlayer(SkyUser player, String obeliskType, Faction faction) {
        try {
            ObeliskDefencePurchaseOverviewUI.builder().player(player).obeliskType(obeliskType).faction(faction).build().show();
        } catch (IllegalArgumentException error) {
            SLogger.fatal("Failed to create Defence Purchase Overview GUI for player {} - {}", player.getUniqueId(), error);
            Messages.ERROR.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "operation", "open the defences purchase GUI", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    public BaseSkyItem handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID().toLowerCase(Locale.ROOT)) {
            case "prompt", "border" ->
                    new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "back" ->
                    new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), obeliskType, player);
            default -> null;
        };
    }

    @NotNull
    @Override
    public List<BaseSkyItem> getModels(SkyUser player, ItemData data) {
        List<BaseSkyItem> items = new ArrayList<>();

        for (Map.Entry<String, DefenceStruct> defence : SkyApi.getInstance().getDefenceFactory().getDefences().getOrDefault(SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), SkyApi.getInstance().getDefenceFactory().getDefaultStruct()).entrySet()) {
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
