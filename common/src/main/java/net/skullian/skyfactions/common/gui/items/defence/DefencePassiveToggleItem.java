package net.skullian.skyfactions.common.gui.items.defence;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.DefencesConfig;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.defence.Defence;
import net.skullian.skyfactions.common.defence.struct.DefenceData;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

import java.util.List;

public class DefencePassiveToggleItem extends SkyItem {

    private boolean HAS_PERMISSIONS = false;

    public DefencePassiveToggleItem(ItemData data, SkyItemStack stack, SkyUser player, DefenceData defenceData, Faction faction) {
        super(data, stack, player, List.of(defenceData, faction).toArray());
    }

    @Override
    public Object[] replacements() {
        DefenceData data = (DefenceData) getOPTIONALS()[0];
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(getPLAYER().getUniqueId());

        return List.of(
                "operation", data.isTARGET_PASSIVE() ? Messages.DEFENCE_DISABLE_PLACEHOLDER.getString(locale)
                        : Messages.DEFENCE_ENABLE_PLACEHOLDER.getString(locale)
        ).toArray();
    }

    @Override
    public SkyItemStack.SkyItemStackBuilder process(SkyItemStack.SkyItemStackBuilder builder) {
        if (!(getOPTIONALS()[2] instanceof String)) {
            Faction faction = (Faction) getOPTIONALS()[1];

            if (SkyApi.getInstance().getDefenceAPI().hasPermissions(DefencesConfig.PERMISSION_TOGGLE_ENTITY_TARGETING.getList(), getPLAYER(), faction)) this.HAS_PERMISSIONS = true;
                else return SkyApi.getInstance().getDefenceAPI().processPermissions(builder, getPLAYER());
        } else this.HAS_PERMISSIONS = true;

        return builder;
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        if (!this.HAS_PERMISSIONS) {
            SkyApi.getInstance().getSoundAPI().playSound(player, Settings.ERROR_SOUND.getString(), Settings.ERROR_SOUND_PITCH.getInt(), 1);
            return;
        }
        DefenceData data = (DefenceData) getOPTIONALS()[0];
        Defence defence = SkyApi.getInstance().getDefenceAPI().getDefenceFromData(data);
        if (defence == null) return;

        data.setTARGET_PASSIVE(!data.isTARGET_PASSIVE());
        defence.setData(data);

        update();
    }
}
