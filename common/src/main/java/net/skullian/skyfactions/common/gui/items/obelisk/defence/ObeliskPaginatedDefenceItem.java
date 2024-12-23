package net.skullian.skyfactions.common.gui.items.obelisk.defence;

import net.skullian.skyfactions.common.api.DefenceAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.DefencesConfig;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.AsyncSkyItem;
import net.skullian.skyfactions.common.gui.screens.obelisk.defence.ObeliskPurchaseDefenceUI;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

import java.util.List;

public class ObeliskPaginatedDefenceItem extends AsyncSkyItem {

    private final DefenceStruct STRUCT;
    private final boolean SHOULD_REDIRECT;
    private final String TYPE;
    private final Faction FACTION;
    private boolean HAS_PERMISSIONS = false;

    public ObeliskPaginatedDefenceItem(ItemData data, SkyItemStack stack, DefenceStruct struct, boolean shouldRedirect, String type, Faction faction, SkyUser player) {
        super(data, stack, player, List.of(struct, faction != null ? faction : "", type).toArray());

        this.STRUCT = struct;
        this.SHOULD_REDIRECT = shouldRedirect;
        this.TYPE = type;
        this.FACTION = faction;
    }

    @Override
    public Object[] replacements() {
        DefenceStruct struct = (DefenceStruct) getOPTIONALS()[0];

        String maxLevel = String.valueOf(struct.getMAX_LEVEL());
        String range = DefenceAPI.solveFormula(struct.getATTRIBUTES().getRANGE(), 1);
        String ammo = DefenceAPI.solveFormula(struct.getATTRIBUTES().getMAX_AMMO(), 1);
        String targetMax = DefenceAPI.solveFormula(struct.getATTRIBUTES().getMAX_TARGETS(), 1);
        String damage = DefenceAPI.solveFormula(struct.getATTRIBUTES().getDAMAGE(), 1);
        String cooldown = DefenceAPI.solveFormula(struct.getATTRIBUTES().getCOOLDOWN(), 1);
        String healing = DefenceAPI.solveFormula(struct.getATTRIBUTES().getHEALING(), 1);
        String distance = DefenceAPI.solveFormula(struct.getATTRIBUTES().getDISTANCE(), 1);
        String repairCost = DefenceAPI.solveFormula(struct.getREPAIR_COST(), 1);
        
        return List.of(
            "max_level", maxLevel,
            "range", range,
            "ammo", ammo,
            "target_max", targetMax,
            "damage", damage,
            "cooldown", cooldown,
            "healing", healing,
            "distance", distance,
            "repair_cost", repairCost,
            "cost", String.valueOf(struct.getBUY_COST()),
            "defence_name", struct.getNAME()
        ).toArray();
    }

    @Override
    public SkyItemStack.SkyItemStackBuilder process(SkyItemStack.SkyItemStackBuilder builder) {
        if (!SkyApi.getInstance().getPlayerAPI().hasInventorySpace(getPLAYER())) {
            Messages.DEFENCE_INSUFFICIENT_INVENTORY_LORE.getStringList(SkyApi.getInstance().getPlayerAPI().getLocale(getPLAYER().getUniqueId()));
        }

        String type = (String) getOPTIONALS()[2];
        if (type.equalsIgnoreCase("faction")) {
            Faction faction  = (Faction) getOPTIONALS()[1];

            if (SkyApi.getInstance().getDefenceAPI().hasPermissions(DefencesConfig.PERMISSION_PURCHASE_DEFENCE.getList(), getPLAYER(), faction)) this.HAS_PERMISSIONS = true;
                else return SkyApi.getInstance().getDefenceAPI().processPermissions(builder, getPLAYER());
        } else HAS_PERMISSIONS = true;


        return builder;
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        if (SHOULD_REDIRECT && HAS_PERMISSIONS) {
            ObeliskPurchaseDefenceUI.promptPlayer(player, TYPE, STRUCT, FACTION);
        } else if (!HAS_PERMISSIONS) {
            SkyApi.getInstance().getSoundAPI().playSound(player, Settings.ERROR_SOUND.getString(), Settings.ERROR_SOUND_PITCH.getInt(), 1f);
            Messages.DEFENCE_INSUFFICIENT_PERMISSIONS.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()));
        }
    }
}
