package net.skullian.skyfactions.gui.items.defence;

import net.skullian.skyfactions.api.DefenceAPI;
import net.skullian.skyfactions.config.types.DefencesConfig;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.defence.Defence;
import net.skullian.skyfactions.defence.struct.DefenceData;
import net.skullian.skyfactions.defence.struct.DefenceStruct;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.faction.AuditLogType;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.util.ErrorUtil;
import net.skullian.skyfactions.util.SoundUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import java.util.List;

public class DefenceRemoveItem extends SkyItem {

    private boolean HAS_PERMISSIONS = false;

    public DefenceRemoveItem(ItemData data, ItemStack stack, Player player, DefenceStruct struct, DefenceData defenceData, Faction faction) {
        super(data, stack, player, List.of(struct, defenceData, faction).toArray());
    }

    @Override
    public ItemBuilder process(ItemBuilder builder) {
        if (!(getOptionals()[2] instanceof String)) {
            Faction faction = (Faction) getOptionals()[2];

            if (DefenceAPI.hasPermissions(DefencesConfig.PERMISSION_REMOVE_DEFENCE.getList(), getPLAYER(), faction)) this.HAS_PERMISSIONS = true;
                else return DefenceAPI.processPermissions(builder, getPLAYER());
        }

        return builder;
    }

    @Override
    public void onClick(ClickType clickType, Player player, InventoryClickEvent event) {
        if (!this.HAS_PERMISSIONS) {
            SoundUtil.playSound(player, Settings.ERROR_SOUND.getString(), Settings.ERROR_SOUND_PITCH.getInt(), 1);
            return;
        }
        DefenceStruct struct = (DefenceStruct) getOptionals()[0];
        DefenceData defenceData = (DefenceData) getOptionals()[1];

        Defence defence = DefenceAPI.getDefenceFromData(defenceData);
        defence.disable();

        defence.remove().whenComplete((ignored, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "remove a defence", "DEFENCE_PROCESSING_EXCEPTION", ex);
                return;
            }

            DefenceAPI.returnDefence(struct, getPLAYER());
            Messages.DEFENCE_REMOVE_SUCCESS.send(player, PlayerHandler.getLocale(player.getUniqueId()));

            if (!(getOptionals()[2] instanceof String)) {
                Faction faction = (Faction) getOptionals()[2];
                faction.createAuditLog(player.getUniqueId(), AuditLogType.DEFENCE_REMOVAL, "player_name", player.getName(), "defence_name", struct.getNAME());
            }
        });
    }
}
