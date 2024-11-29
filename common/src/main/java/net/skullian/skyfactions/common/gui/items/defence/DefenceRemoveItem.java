package net.skullian.skyfactions.common.gui.items.defence;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.DefencesConfig;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.defence.Defence;
import net.skullian.skyfactions.common.defence.struct.DefenceData;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;
import net.skullian.skyfactions.common.faction.AuditLogType;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import net.skullian.skyfactions.common.util.SkyItemStack;

import java.util.List;

public class DefenceRemoveItem extends SkyItem {

    private boolean HAS_PERMISSIONS = false;

    public DefenceRemoveItem(ItemData data, SkyItemStack stack, SkyUser player, DefenceStruct struct, DefenceData defenceData, Faction faction) {
        super(data, stack, player, List.of(struct, defenceData, faction).toArray());
    }

    @Override
    public SkyItemStack.SkyItemStackBuilder process(SkyItemStack.SkyItemStackBuilder builder) {
        if (!(getOPTIONALS()[2] instanceof String)) {
            Faction faction = (Faction) getOPTIONALS()[2];

            if (SkyApi.getInstance().getDefenceAPI().hasPermissions(DefencesConfig.PERMISSION_REMOVE_DEFENCE.getList(), getPLAYER(), faction)) this.HAS_PERMISSIONS = true;
                else return SkyApi.getInstance().getDefenceAPI().processPermissions(builder, getPLAYER());
        }

        return builder;
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        if (!this.HAS_PERMISSIONS) {
            SkyApi.getInstance().getSoundAPI().playSound(player, Settings.ERROR_SOUND.getString(), Settings.ERROR_SOUND_PITCH.getInt(), 1);
            return;
        }
        DefenceStruct struct = (DefenceStruct) getOPTIONALS()[0];
        DefenceData defenceData = (DefenceData) getOPTIONALS()[1];

        Defence defence = SkyApi.getInstance().getDefenceAPI().getDefenceFromData(defenceData);
        defence.disable();

        defence.remove().whenComplete((ignored, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "remove a defence", "DEFENCE_PROCESSING_EXCEPTION", ex);
                return;
            }

            SkyApi.getInstance().getDefenceAPI().returnDefence(struct, getPLAYER());
            Messages.DEFENCE_REMOVE_SUCCESS.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()));

            if (!(getOPTIONALS()[2] instanceof String)) {
                Faction faction = (Faction) getOPTIONALS()[2];
                faction.createAuditLog(player.getUniqueId(), AuditLogType.DEFENCE_REMOVAL, "player_name", player.getName(), "defence_name", struct.getNAME());
            }
        });
    }
}
