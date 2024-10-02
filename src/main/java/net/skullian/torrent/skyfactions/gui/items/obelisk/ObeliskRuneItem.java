package net.skullian.torrent.skyfactions.gui.items.obelisk;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.api.FactionAPI;
import net.skullian.torrent.skyfactions.api.GUIAPI;
import net.skullian.torrent.skyfactions.config.types.Messages;
import net.skullian.torrent.skyfactions.faction.Faction;
import net.skullian.torrent.skyfactions.gui.data.GUIData;
import net.skullian.torrent.skyfactions.gui.data.ItemData;
import net.skullian.torrent.skyfactions.gui.obelisk.RuneSubmitUI;
import net.skullian.torrent.skyfactions.util.SoundUtil;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AsyncItem;

public class ObeliskRuneItem extends AsyncItem {

    private String SOUND;
    private int PITCH;
    private String TYPE;

    public ObeliskRuneItem(ItemData data, ItemStack stack, String type, Player player) {
        super(
                new ItemBuilder(Material.COMPASS).setDisplayName(TextUtility.color("&eLoading...")),
                () -> {
                    return new ItemProvider() {
                        @Override
                        public @NotNull ItemStack get(@Nullable String s) {
                            ItemBuilder builder = new ItemBuilder(stack)
                                    .setDisplayName(TextUtility.color(data.getNAME()));

                            int runes = 0;
                            if (type.equals("player")) {
                                runes = SkyFactionsReborn.db.getRunes(player.getUniqueId()).join();
                            } else if (type.equals("faction")) {
                                Faction faction = FactionAPI.getFaction(player).join();
                                if (faction != null) {
                                    runes = faction.getRunes();
                                }
                            }

                            for (String loreLine : data.getLORE()) {
                                builder.addLoreLines(TextUtility.color(loreLine.replace("%runes%", String.valueOf(runes))));
                            }

                            return builder.get();
                        }
                    };
                }
        );

        this.SOUND = data.getSOUND();
        this.PITCH = data.getPITCH();
        this.TYPE = type;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        try {
            event.setCancelled(true);

            if (!SOUND.equalsIgnoreCase("none")) {
                SoundUtil.playSound(player, SOUND, PITCH, 1);
            }

            GUIData data = GUIAPI.getGUIData("runes_ui");
            new RuneSubmitUI(RuneSubmitUI.createStructure(data), data, player, TYPE).promptPlayer(player);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            event.getInventory().close();
            Messages.ERROR.send(player, "%operation%", "open the rune UI", "%debug%", "GUI_LOAD_EXCEPTION");
        }
    }

}
