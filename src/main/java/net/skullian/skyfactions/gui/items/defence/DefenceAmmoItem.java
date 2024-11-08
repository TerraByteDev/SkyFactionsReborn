package net.skullian.skyfactions.gui.items.defence;

import net.skullian.skyfactions.api.DefenceAPI;
import net.skullian.skyfactions.config.types.DefencesConfig;
import net.skullian.skyfactions.defence.struct.DefenceData;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import java.util.List;

public class DefenceAmmoItem extends SkyItem {

    private boolean hasPermissions = false;

    public DefenceAmmoItem(ItemData data, ItemStack stack, Player player, DefenceData defenceData, Faction faction) {
        super(data, stack, player, List.of(defenceData, faction).toArray());
    }

    @Override
    public ItemBuilder process(ItemBuilder builder) {
        if (getOptionals()[2] != null) {
            Faction faction = (Faction) getOptionals()[1];

            if (DefenceAPI.hasPermissions(DefencesConfig.PERMISSION_REPLENISH_AMMO.getList(), getPLAYER(), faction)) this.hasPermissions = true;
            else return DefenceAPI.processPermissions(builder, getPLAYER());
        }

        return builder;
    }

    @Override
    public Object[] replacements() {
        DefenceData data = (DefenceData) getOptionals()[0];

        return List.of(
                "ammo", data.getAMMO()
        ).toArray();
    }

    @Override
    public void onClick(ClickType clickType, Player player, InventoryClickEvent event) {
        // todo
    }
}
