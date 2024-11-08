package net.skullian.skyfactions.gui.items.defence;

import net.skullian.skyfactions.api.DefenceAPI;
import net.skullian.skyfactions.config.types.DefencesConfig;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.defence.Defence;
import net.skullian.skyfactions.defence.struct.DefenceData;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import java.util.List;

public class DefencePassiveToggleItem extends SkyItem {

    private boolean hasPermissions = false;

    public DefencePassiveToggleItem(ItemData data, ItemStack stack, Player player, DefenceData defenceData, Faction faction) {
        super(data, stack, player, List.of(defenceData, faction).toArray());
    }

    @Override
    public Object[] replacements() {
        DefenceData data = (DefenceData) getOptionals()[0];
        String locale = PlayerHandler.getLocale(getPLAYER().getUniqueId());

        return List.of(
                "operation", data.isTARGET_PASSIVE() ? Messages.DEFENCE_DISABLE_PLACEHOLDER.getString(locale)
                        : Messages.DEFENCE_ENABLE_PLACEHOLDER.getString(locale)
        ).toArray();
    }

    @Override
    public ItemBuilder process(ItemBuilder builder) {
        if (getOptionals()[2] != null) {
            Faction faction = (Faction) getOptionals()[1];

            if (DefenceAPI.hasPermissions(DefencesConfig.PERMISSION_TOGGLE_ENTITY_TARGETING.getList(), getPLAYER(), faction)) this.hasPermissions = true;
            else return DefenceAPI.processPermissions(builder, getPLAYER());
        }

        return builder;
    }

    @Override
    public void onClick(ClickType clickType, Player player, InventoryClickEvent event) {
        DefenceData data = (DefenceData) getOptionals()[0];
        Defence defence = DefenceAPI.getDefenceFromData(data);

        data.setTARGET_PASSIVE(!data.isTARGET_PASSIVE());
        defence.setData(data);

        notifyWindows();
    }
}
