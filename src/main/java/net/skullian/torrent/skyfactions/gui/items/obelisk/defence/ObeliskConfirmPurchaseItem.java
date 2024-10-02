package net.skullian.torrent.skyfactions.gui.items.obelisk.defence;

import net.skullian.torrent.skyfactions.config.types.Messages;
import net.skullian.torrent.skyfactions.config.types.Settings;
import net.skullian.torrent.skyfactions.defence.struct.DefenceStruct;
import net.skullian.torrent.skyfactions.faction.Faction;
import net.skullian.torrent.skyfactions.gui.data.ItemData;
import net.skullian.torrent.skyfactions.util.ErrorHandler;
import net.skullian.torrent.skyfactions.util.SoundUtil;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.List;

public class ObeliskConfirmPurchaseItem extends AbstractItem {

    private String NAME;
    private String SOUND;
    private int PITCH;
    private List<String> LORE;
    private ItemStack STACK;
    private String TYPE;
    private DefenceStruct STRUCT;
    private Faction FACTION;
    private Player PLAYER;
    private boolean CAN_BE_PURCHASED;

    public ObeliskConfirmPurchaseItem(ItemData data, ItemStack stack, String type, DefenceStruct struct, Faction faction, Player player) {
        this.NAME = data.getNAME();
        this.SOUND = data.getSOUND();
        this.PITCH = data.getPITCH();
        this.LORE = data.getLORE();
        this.STACK = stack;
        this.TYPE = type;
        this.STRUCT = struct;
        this.FACTION = faction;
        this.PLAYER = player;
    }

    @Override
    public ItemProvider getItemProvider() {
        ItemBuilder builder = new ItemBuilder(STACK)
                .setDisplayName(TextUtility.color(NAME));

        for (String loreLine : LORE) {
            builder.addLoreLines(TextUtility.color(loreLine.replace("%defence_cost%", String.valueOf(STRUCT.getBUY_COST()))));

            if (FACTION.getRunes() < STRUCT.getBUY_COST()) {
                CAN_BE_PURCHASED = false;
                for (String line : Messages.DEFENCE_INSUFFICIENT_RUNES_LORE.getStringList()) {
                    builder.addLoreLines(TextUtility.color(line));
                }
            } else if (PLAYER.getInventory().firstEmpty() == -1) {
                CAN_BE_PURCHASED = false;
                for (String line : Messages.DEFENCE_INSUFFICIENT_INVENTORY_LORE.getStringList()) {
                    builder.addLoreLines(TextUtility.color(line));
                }
            } else CAN_BE_PURCHASED = true;
        }

        return builder;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.setCancelled(true);

        if (!SOUND.equalsIgnoreCase("none")) {
            SoundUtil.playSound(player, SOUND, PITCH, 1);
        }

        if (!CAN_BE_PURCHASED) {

        }
        if (TYPE.equals("faction")) {
            if (FACTION.getRunes() < STRUCT.getBUY_COST()) {
                SoundUtil.playSound(player, Settings.ERROR_SOUND.getString(), Settings.ERROR_SOUND_PITCH.getInt(), 1);
                return;
            }

            player.closeInventory();
            Messages.PLEASE_WAIT.send(player, "%operation%", "purchasing your defence");
            FACTION.subtractRunes(STRUCT.getBUY_COST()).whenComplete((ignored, ex) -> {
                if (ex != null) {
                    ErrorHandler.handleError(player, "purchase your defence", "SQL_RUNES_MODIFY", ex);
                    return;
                }

                SoundUtil.playSound(player, Settings.DEFENCE_PURCHASE_SUCCESS_SOUND.getString(), Settings.DEFENCE_PURCHASE_SUCCESS_SOUND_PITCH.getInt(), 1);
                System.out.println("todo");
            });
        }


    }


}
