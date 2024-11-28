package net.skullian.skyfactions.core.gui.items.defence;

import net.skullian.skyfactions.core.api.SpigotDefenceAPI;
import net.skullian.skyfactions.core.config.types.DefencesConfig;
import net.skullian.skyfactions.core.config.types.Settings;
import net.skullian.skyfactions.common.defence.struct.DefenceData;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.impl.old.SkyItem;
import net.skullian.skyfactions.core.util.SoundUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import java.util.List;

public class DefenceUpgradeItem extends SkyItem {

    private boolean HAS_PERMISSIONS = false;

    public DefenceUpgradeItem(ItemData data, ItemStack stack, Player player, DefenceStruct struct, DefenceData defenceData, Faction faction) {
        super(data, stack, player, List.of(struct, defenceData, faction).toArray());
    }

    @Override
    public Object[] replacements() {
        DefenceStruct struct = (DefenceStruct) getOptionals()[0];

        return List.of("upgrade_lore", struct.getUPGRADE_LORE()).toArray();
    }

    @Override
    public ItemBuilder process(ItemBuilder builder) {
        if (!(getOptionals()[2] instanceof String)) {
            Faction faction = (Faction) getOptionals()[2];

            if (SpigotDefenceAPI.hasPermissions(DefencesConfig.PERMISSION_UPGRADE_DEFENCE.getList(), getPLAYER(), faction)) this.HAS_PERMISSIONS = true;
            else return SpigotDefenceAPI.processPermissions(builder, getPLAYER());
        }

        return builder;
    }

    @Override
    public void onClick(ClickType clickType, Player player, InventoryClickEvent event) {
        if (!this.HAS_PERMISSIONS) {
            SoundUtil.playSound(player, Settings.ERROR_SOUND.getString(), Settings.ERROR_SOUND_PITCH.getInt(), 1);
            return;
        }
        // todo
    }
}