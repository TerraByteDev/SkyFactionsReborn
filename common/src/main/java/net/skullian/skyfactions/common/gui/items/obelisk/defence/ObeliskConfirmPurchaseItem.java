package net.skullian.skyfactions.common.gui.items.obelisk.defence;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.AsyncSkyItem;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import net.skullian.skyfactions.common.util.SkyItemStack;

import java.util.List;

public class ObeliskConfirmPurchaseItem extends AsyncSkyItem {

    private final String TYPE;
    private final DefenceStruct STRUCT;
    private final Faction FACTION;

    public ObeliskConfirmPurchaseItem(ItemData data, SkyItemStack stack, String type, DefenceStruct struct, Faction faction, SkyUser player) {
        super(data, stack, player, List.of(type, faction != null ? faction : "", struct).toArray());

        this.TYPE = type;
        this.STRUCT = struct;
        this.FACTION = faction;
    }

    @Override
    public SkyItemStack.SkyItemStackBuilder process(SkyItemStack.SkyItemStackBuilder builder) {
        String type = (String) getOPTIONALS()[0];
        DefenceStruct struct = (DefenceStruct) getOPTIONALS()[2];
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(getPLAYER().getUniqueId());

        if (type.equals("faction")) {
            Faction faction = (Faction) getOPTIONALS()[1];
            if (faction.getRunes() < struct.getBUY_COST()) {
                builder.lore(Messages.DEFENCE_INSUFFICIENT_RUNES_LORE.getStringList(locale));
            }
        } else if (type.equals("player")) {
            int runes = getPLAYER().getRunes().join();
            if (runes < struct.getBUY_COST()) {
                builder.lore(Messages.DEFENCE_INSUFFICIENT_RUNES_LORE.getStringList(locale));
            }
        }
        if (SkyApi.getInstance().getPlayerAPI().hasInventorySpace(getPLAYER())) {
            builder.lore(Messages.DEFENCE_INSUFFICIENT_INVENTORY_LORE.getStringList(locale));
        }

        return builder;
    }

    @Override
    public Object[] replacements() {
        DefenceStruct struct = (DefenceStruct) getOPTIONALS()[2];
        return List.of(
                "defence_cost", struct.getBUY_COST()
        ).toArray();
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());
        if (SkyApi.getInstance().getPlayerAPI().hasInventorySpace(getPLAYER())) {
            SkyApi.getInstance().getSoundAPI().playSound(player, Settings.ERROR_SOUND.getString(), Settings.ERROR_SOUND_PITCH.getInt(), 1);
            return;
        }

        if (TYPE.equals("faction")) {
            if (FACTION.getRunes() < STRUCT.getBUY_COST()) {
                SkyApi.getInstance().getSoundAPI().playSound(player, Settings.ERROR_SOUND.getString(), Settings.ERROR_SOUND_PITCH.getInt(), 1);
                return;
            }

            player.closeInventory();
            Messages.PLEASE_WAIT.send(player, locale, "operation", "purchasing your defence");

            FACTION.subtractRunes(STRUCT.getBUY_COST());
            Messages.PLEASE_WAIT.send(player, locale, "operation", "Purchasing your defence");
            SkyApi.getInstance().getDefenceAPI().addDefence(player, STRUCT, FACTION);
        } else if (TYPE.equals("player")) {

            player.getRunes().whenComplete((runes, ex) -> {
                if (ex != null) {
                    ErrorUtil.handleError(player, "purchase your defence", "SQL_RUNES_GET", ex);
                    return;
                } else if (runes < STRUCT.getBUY_COST()) {
                    SkyApi.getInstance().getSoundAPI().playSound(player, Settings.ERROR_SOUND.getString(), Settings.ERROR_SOUND_PITCH.getInt(), 1);
                    return;
                }

                player.closeInventory();
                Messages.PLEASE_WAIT.send(player, locale, "operation", "Purchasing your defence");
                SkyApi.getInstance().getDefenceAPI().addDefence(player, STRUCT, FACTION);
            });
        }


    }


}
