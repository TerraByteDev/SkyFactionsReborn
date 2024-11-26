package net.skullian.skyfactions.core.gui.screens.obelisk.defence;

import lombok.Builder;
import net.skullian.skyfactions.core.api.GUIAPI;
import net.skullian.skyfactions.core.config.types.GUIEnums;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.EmptyItem;
import net.skullian.skyfactions.core.gui.items.GeneralCancelItem;
import net.skullian.skyfactions.core.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.core.gui.items.obelisk.defence.ObeliskConfirmPurchaseItem;
import net.skullian.skyfactions.core.gui.items.obelisk.defence.ObeliskPaginatedDefenceItem;
import net.skullian.skyfactions.core.gui.screens.Screen;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;

public class ObeliskPurchaseDefenceUI extends Screen {
    private final String obeliskType;
    private final DefenceStruct struct;
    private final Faction faction;

    @Builder
    public ObeliskPurchaseDefenceUI(Player player, String obeliskType, DefenceStruct struct, Faction faction) {
        super(player, GUIEnums.OBELISK_PURCHASE_DEFENCE_GUI.getPath());
        this.obeliskType = obeliskType;
        this.struct = struct;
        this.faction = faction;

        initWindow();
    }

    public static void promptPlayer(Player player, String obeliskType, DefenceStruct struct, Faction faction) {
        try {
            ObeliskPurchaseDefenceUI.builder().player(player).obeliskType(obeliskType).struct(struct).faction(faction).build().show();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "open the defence purchase confirmation GUI", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    protected Item handleItem(@NotNull ItemData itemData) {
        if (itemData.getITEM_ID().equals("DEFENCE")) {
            itemData.setBASE64_TEXTURE(struct.getITEM_SKULL());
            itemData.setMATERIAL(struct.getITEM_MATERIAL());
            itemData.setLORE(struct.getITEM_LORE());

            return new ObeliskPaginatedDefenceItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), struct, false, obeliskType, faction, player);
        }

        return switch (itemData.getITEM_ID()) {
            case "BACK" ->
                    new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), obeliskType, player);
            case "CONFIRM" ->
                    new ObeliskConfirmPurchaseItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), obeliskType, struct, faction, player);
            case "CANCEL" -> new GeneralCancelItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "BORDER" -> new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            default -> null;
        };
    }
}
