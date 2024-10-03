package net.skullian.torrent.skyfactions.gui.items.obelisk.defence;

import net.skullian.torrent.skyfactions.api.RunesAPI;
import net.skullian.torrent.skyfactions.config.types.Messages;
import net.skullian.torrent.skyfactions.config.types.ObeliskConfig;
import net.skullian.torrent.skyfactions.config.types.Settings;
import net.skullian.torrent.skyfactions.defence.DefencesFactory;
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
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AsyncItem;

public class ObeliskConfirmPurchaseItem extends AsyncItem {

    private String SOUND;
    private int PITCH;
    private String TYPE;
    private DefenceStruct STRUCT;
    private Faction FACTION;

    public ObeliskConfirmPurchaseItem(ItemData data, ItemStack stack, String type, DefenceStruct struct, Faction faction, Player player) {
        super(
                ObeliskConfig.getLoadingItem(),
                () -> {
                    return new ItemProvider() {
                        @Override
                        public @NotNull ItemStack get(@Nullable String s) {
                            ItemBuilder builder = new ItemBuilder(stack)
                                    .setDisplayName(TextUtility.color(data.getNAME()));

                            for (String loreLine : data.getLORE()) {
                                builder.addLoreLines(TextUtility.color(loreLine.replace("%defence_cost%", String.valueOf(struct.getBUY_COST()))));
                            }

                            if (type.equals("faction")) {
                                if (faction.getRunes() < struct.getBUY_COST()) {
                                    for (String line : Messages.DEFENCE_INSUFFICIENT_RUNES_LORE.getStringList()) {
                                        builder.addLoreLines(TextUtility.color(line));
                                    }
                                }
                            } else if (type.equals("player")) {
                                int runes = RunesAPI.getRunes(player.getUniqueId()).join();
                                if (runes < struct.getBUY_COST()) {
                                    for (String line : Messages.DEFENCE_INSUFFICIENT_RUNES_LORE.getStringList()) {
                                        builder.addLoreLines(TextUtility.color(line));
                                    }
                                }
                            }

                            if (player.getInventory().firstEmpty() == -1) {
                                for (String line : Messages.DEFENCE_INSUFFICIENT_INVENTORY_LORE.getStringList()) {
                                    builder.addLoreLines(TextUtility.color(line));
                                }
                            }

                            return builder.get();
                        }
                    };
                }
        );

        this.SOUND = data.getSOUND();
        this.PITCH = data.getPITCH();
        this.TYPE = type;
        this.STRUCT = struct;
        this.FACTION = faction;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.setCancelled(true);

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
            FACTION.subtractRunes(STRUCT.getBUY_COST()).whenComplete((ignored, ex) -> {
                if (ex != null) {
                    ErrorHandler.handleError(player, "purchase your defence", "SQL_RUNES_MODIFY", ex);
                    return;
                }

                Messages.PLEASE_WAIT.send(player, "%operation%", "Purchasing your defence");
                DefencesFactory.addDefence(player, STRUCT, FACTION);
            });
        } else if (TYPE.equals("player")) {
            RunesAPI.getRunes(player.getUniqueId()).whenComplete((runes, ex) -> {
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                } else if (runes < STRUCT.getBUY_COST()) {
                    SoundUtil.playSound(player, Settings.ERROR_SOUND.getString(), Settings.ERROR_SOUND.getInt(), 1);
                    return;
                }

                Messages.PLEASE_WAIT.send(player, "%operation%", "Purchasing your defence");
                DefencesFactory.addDefence(player, STRUCT, FACTION);
            });
        }


    }


}
