package net.skullian.skyfactions.gui.items.obelisk.defence;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.api.RunesAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.defence.DefencesFactory;
import net.skullian.skyfactions.defence.struct.DefenceStruct;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.AsyncSkyItem;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public class ObeliskConfirmPurchaseItem extends AsyncSkyItem {

    private String TYPE;
    private DefenceStruct STRUCT;
    private Faction FACTION;

    public ObeliskConfirmPurchaseItem(ItemData data, ItemStack stack, String type, DefenceStruct struct, Faction faction, Player player) {
        super(data, stack, player, List.of(type, faction, struct).toArray());

        this.TYPE = type;
        this.STRUCT = struct;
        this.FACTION = faction;
    }

    @Override
    public ItemBuilder process(ItemBuilder builder) {
        String type = (String) getOptionals()[0];
        Faction faction = (Faction) getOptionals()[1];
        DefenceStruct struct = (DefenceStruct) getOptionals()[2];

        if (type.equals("faction")) {
            if (faction.getRunes() < struct.getBUY_COST()) {
                for (String line : Messages.DEFENCE_INSUFFICIENT_RUNES_LORE.getStringList()) {
                    builder.addLoreLines(TextUtility.color(line, getPLAYER()));
                }
            }
        } else if (type.equals("player")) {

            int runes = RunesAPI.getRunes(getPLAYER().getUniqueId());
            if (runes < struct.getBUY_COST()) {
                for (String line : Messages.DEFENCE_INSUFFICIENT_RUNES_LORE.getStringList()) {
                    builder.addLoreLines(TextUtility.color(line, getPLAYER()));
                }
            }
        }

        if (getPLAYER().getInventory().firstEmpty() == -1) {
            for (String line : Messages.DEFENCE_INSUFFICIENT_INVENTORY_LORE.getStringList()) {
                builder.addLoreLines(TextUtility.color(line, getPLAYER()));
            }
        }

        return builder;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (player.getInventory().firstEmpty() == -1) {
            SoundUtil.playSound(player, Settings.ERROR_SOUND.getString(), Settings.ERROR_SOUND_PITCH.getInt(), 1);
            return;
        }

        if (TYPE.equals("faction")) {
            if (FACTION.getRunes() < STRUCT.getBUY_COST()) {
                SoundUtil.playSound(player, Settings.ERROR_SOUND.getString(), Settings.ERROR_SOUND_PITCH.getInt(), 1);
                return;
            }

            player.closeInventory();
            Messages.PLEASE_WAIT.send(player, "%operation%", "purchasing your defence");

            FACTION.subtractRunes(STRUCT.getBUY_COST());
            Messages.PLEASE_WAIT.send(player, "%operation%", "Purchasing your defence");
            DefencesFactory.addDefence(player, STRUCT, FACTION);;
        } else if (TYPE.equals("player")) {

            int runes = RunesAPI.getRunes(player.getUniqueId());
            if (runes < STRUCT.getBUY_COST()) {
                SoundUtil.playSound(player, Settings.ERROR_SOUND.getString(), Settings.ERROR_SOUND.getInt(), 1);
                return;
            }

            player.closeInventory();
            Messages.PLEASE_WAIT.send(player, "%operation%", "Purchasing your defence");
            DefencesFactory.addDefence(player, STRUCT, FACTION);
        }


    }


}
