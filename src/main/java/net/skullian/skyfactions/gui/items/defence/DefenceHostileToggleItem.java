package net.skullian.skyfactions.gui.items.defence;

import net.skullian.skyfactions.api.DefenceAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.defence.Defence;
import net.skullian.skyfactions.defence.struct.DefenceData;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DefenceHostileToggleItem extends SkyItem {

    public DefenceHostileToggleItem(ItemData data, ItemStack stack, Player player, DefenceData defenceData) {
        super(data, stack, player, List.of(defenceData).toArray());
    }

    @Override
    public Object[] replacements() {
        DefenceData data = (DefenceData) getOptionals()[0];
        String locale = PlayerHandler.getLocale(getPLAYER().getUniqueId());

        return List.of(
                "operation", data.isTARGET_HOSTILES() ? Messages.DEFENCE_DISABLE_PLACEHOLDER.getString(locale)
                        : Messages.DEFENCE_ENABLE_PLACEHOLDER.getString(locale)
        ).toArray();
    }

    @Override
    public void onClick(ClickType clickType, Player player, InventoryClickEvent event) {
        DefenceData data = (DefenceData) getOptionals()[0];
        Defence defence = DefenceAPI.getDefenceFromData(data);

        data.setTARGET_HOSTILES(!data.isTARGET_HOSTILES());
        defence.setData(data);

        notifyWindows();
    }
}

