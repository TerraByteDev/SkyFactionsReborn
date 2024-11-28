package net.skullian.skyfactions.core.gui.items.obelisk.defence;

import java.util.List;

import net.skullian.skyfactions.core.SkyFactionsReborn;
import net.skullian.skyfactions.core.util.ErrorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.core.api.SpigotRunesAPI;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.config.types.Settings;
import net.skullian.skyfactions.core.defence.DefencesFactory;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.impl.old.AsyncSkyItem;
import net.skullian.skyfactions.core.util.SoundUtil;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public class ObeliskConfirmPurchaseItem extends AsyncSkyItem {

    private String TYPE;
    private DefenceStruct STRUCT;
    private Faction FACTION;

    public ObeliskConfirmPurchaseItem(ItemData data, ItemStack stack, String type, DefenceStruct struct, Faction faction, Player player) {
        super(data, stack, player, List.of(type, faction != null ? faction : "", struct).toArray());

        this.TYPE = type;
        this.STRUCT = struct;
        this.FACTION = faction;
    }

    @Override
    public ItemBuilder process(ItemBuilder builder) {
        String type = (String) getOptionals()[0];
        DefenceStruct struct = (DefenceStruct) getOptionals()[2];
        String locale = SpigotPlayerAPI.getLocale(getPLAYER().getUniqueId());

        if (type.equals("faction")) {
            Faction faction = (Faction) getOptionals()[1];
            if (faction.getRunes() < struct.getBUY_COST()) {
                builder.addLoreLines(toList(Messages.DEFENCE_INSUFFICIENT_RUNES_LORE.getStringList(locale)));
            }
        } else if (type.equals("player")) {
            int runes = SpigotRunesAPI.getRunes(getPLAYER().getUniqueId()).join();
            if (runes < struct.getBUY_COST()) {
                builder.addLoreLines(toList(Messages.DEFENCE_INSUFFICIENT_RUNES_LORE.getStringList(locale)));
            }
        }

        if (getPLAYER().getInventory().firstEmpty() == -1) {
            builder.addLoreLines(toList(Messages.DEFENCE_INSUFFICIENT_INVENTORY_LORE.getStringList(locale)));
        }

        return builder;
    }

    @Override
    public Object[] replacements() {
        DefenceStruct struct = (DefenceStruct) getOptionals()[2];
        return List.of(
                "defence_cost", struct.getBUY_COST()
        ).toArray();
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
            Messages.PLEASE_WAIT.send(player, getPLAYER().locale().getLanguage(), "operation", "purchasing your defence");

            FACTION.subtractRunes(STRUCT.getBUY_COST());
            Messages.PLEASE_WAIT.send(player, getPLAYER().locale().getLanguage(), "operation", "Purchasing your defence");
            DefencesFactory.addDefence(player, STRUCT, FACTION);;
        } else if (TYPE.equals("player")) {

            SpigotRunesAPI.getRunes(player.getUniqueId()).whenComplete((runes, ex) -> {
                Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> {
                    if (ex != null) {
                        ErrorUtil.handleError(player, "purchase your defence", "SQL_RUNES_GET", ex);
                        return;
                    } else if (runes < STRUCT.getBUY_COST()) {
                        SoundUtil.playSound(player, Settings.ERROR_SOUND.getString(), Settings.ERROR_SOUND_PITCH.getInt(), 1);
                        return;
                    }

                    player.closeInventory();
                    Messages.PLEASE_WAIT.send(player, getPLAYER().locale().getLanguage(), "operation", "Purchasing your defence");
                    DefencesFactory.addDefence(player, STRUCT, FACTION);
                });
            });
        }


    }


}
