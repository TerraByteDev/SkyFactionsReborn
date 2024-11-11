package net.skullian.skyfactions.gui.items.obelisk.defence;

import java.util.List;

import net.skullian.skyfactions.api.DefenceAPI;
import net.skullian.skyfactions.config.types.DefencesConfig;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.defence.DefencesFactory;
import net.skullian.skyfactions.defence.struct.DefenceStruct;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.AsyncSkyItem;
import net.skullian.skyfactions.gui.screens.obelisk.defence.ObeliskPurchaseDefenceUI;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public class ObeliskPaginatedDefenceItem extends AsyncSkyItem {

    private DefenceStruct STRUCT;
    private boolean SHOULD_REDIRECT;
    private String TYPE;
    private Faction FACTION;
    private boolean HAS_PERMISSIONS = false;

    public ObeliskPaginatedDefenceItem(ItemData data, ItemStack stack, DefenceStruct struct, boolean shouldRedirect, String type, Faction faction, Player player) {
        super(data, stack, player, List.of(struct, (faction != null ? faction : "")).toArray());

        this.STRUCT = struct;
        this.SHOULD_REDIRECT = shouldRedirect;
        this.TYPE = type;
        this.FACTION = faction;
    }

    @Override
    public Object[] replacements() {
        DefenceStruct struct = (DefenceStruct) getOptionals()[0];

        String maxLevel = String.valueOf(struct.getMAX_LEVEL());
        String range = DefencesFactory.solveFormula(struct.getATTRIBUTES().getRANGE(), 1);
        String ammo = DefencesFactory.solveFormula(struct.getATTRIBUTES().getMAX_AMMO(), 1);
        String targetMax = DefencesFactory.solveFormula(struct.getATTRIBUTES().getMAX_TARGETS(), 1);
        String damage = DefencesFactory.solveFormula(struct.getATTRIBUTES().getDAMAGE(), 1);
        String cooldown = DefencesFactory.solveFormula(struct.getATTRIBUTES().getCOOLDOWN(), 1);
        String healing = DefencesFactory.solveFormula(struct.getATTRIBUTES().getHEALING(), 1);
        String distance = DefencesFactory.solveFormula(struct.getATTRIBUTES().getDISTANCE(), 1);
        String repairCost = DefencesFactory.solveFormula(struct.getREPAIR_COST(), 1);   
        
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
            "cost", String.valueOf(struct.getBUY_COST())
        ).toArray();
    }

    @Override
    public ItemBuilder process(ItemBuilder builder) {
        if (getPLAYER().getInventory().firstEmpty() == -1) {
            for (String line : Messages.DEFENCE_INSUFFICIENT_INVENTORY_LORE.getStringList(getPLAYER().locale().getLanguage())) {
                builder.addLoreLines(TextUtility.legacyColor(line, PlayerHandler.getLocale(getPLAYER().getUniqueId()), getPLAYER()));
            }
        }

        if (getOptionals()[1] != "") {
            Faction faction  = (Faction) getOptionals()[1];

            if (DefenceAPI.hasPermissions(DefencesConfig.PERMISSION_PURCHASE_DEFENCE.getList(), getPLAYER(), faction)) this.HAS_PERMISSIONS = true;
                else return DefenceAPI.processPermissions(builder, getPLAYER());
        }


        return builder;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (SHOULD_REDIRECT && HAS_PERMISSIONS) {
            ObeliskPurchaseDefenceUI.promptPlayer(player, TYPE, STRUCT, FACTION);
        } else {
            SoundUtil.playSound(player, Settings.ERROR_SOUND.getString(), Settings.ERROR_SOUND_PITCH.getInt(), 1f);
            Messages.DEFENCE_INSUFFICIENT_PERMISSIONS.send(player, PlayerHandler.getLocale(player.getUniqueId()));
        }
    }
}
