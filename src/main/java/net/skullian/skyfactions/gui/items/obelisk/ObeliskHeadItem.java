package net.skullian.skyfactions.gui.items.obelisk;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.GemsAPI;
import net.skullian.skyfactions.api.IslandAPI;
import net.skullian.skyfactions.api.RunesAPI;
import net.skullian.skyfactions.config.types.ObeliskConfig;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.island.PlayerIsland;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AsyncItem;

public class ObeliskHeadItem extends AsyncItem {

    private String SOUND;
    private int PITCH;

    public ObeliskHeadItem(ItemData data, ItemStack stack, Player player) {
        super(
                ObeliskConfig.getLoadingItem(),
                () -> {
                    return new ItemProvider() {
                        @Override
                        public @NotNull ItemStack get(@Nullable String s) {

                            ItemBuilder builder = new ItemBuilder(stack)
                                    .setDisplayName(TextUtility.color(data.getNAME()));

                            PlayerIsland island = IslandAPI.getPlayerIsland(player.getUniqueId()).join();
                            if (island == null) return builder.get();

                            for (String loreLine : data.getLORE()) {
                                builder.addLoreLines(TextUtility.color(loreLine
                                        .replace("%level%", String.valueOf(SkyFactionsReborn.databaseHandler.getIslandLevel(island).join()))
                                        .replace("%rune_count%", String.valueOf(RunesAPI.getRunes(player.getUniqueId())))
                                        .replace("%gem_count%", String.valueOf(GemsAPI.getGems(player.getUniqueId())))
                                ));
                            }

                            return builder.get();
                        }
                    };
                }
        );

        this.SOUND = data.getSOUND();
        this.PITCH = data.getPITCH();
    }


    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.setCancelled(true);

        if (!SOUND.equalsIgnoreCase("none")) {
            SoundUtil.playSound(player, SOUND, PITCH, 1);
        }
    }
}
