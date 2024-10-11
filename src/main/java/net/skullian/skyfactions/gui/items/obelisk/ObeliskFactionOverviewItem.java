package net.skullian.skyfactions.gui.items.obelisk;

import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.config.types.ObeliskConfig;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.gui.data.ItemData;
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

public class ObeliskFactionOverviewItem extends AsyncItem {

    private String SOUND;
    private int PITCH;

    public ObeliskFactionOverviewItem(ItemData data, ItemStack stack, Player player) {
        super(
                ObeliskConfig.getLoadingItem(),
                () -> {
                    return new ItemProvider() {
                        @Override
                        public @NotNull ItemStack get(@Nullable String s) {
                            Faction faction = FactionAPI.getFaction(player).join();
                            ItemBuilder builder = new ItemBuilder(stack)
                                    .setDisplayName(TextUtility.color(data.getNAME().replace("%faction_name%", faction.getName())));

                            for (String loreLine : data.getLORE()) {
                                builder.addLoreLines(TextUtility.color(loreLine
                                        .replace("%level%", String.valueOf(faction.getLevel()))
                                        .replace("%member_count%", String.valueOf(faction.getTotalMemberCount()))
                                        .replace("%rune_count%", String.valueOf(faction.getRunes()))
                                        .replace("%gem_count%", String.valueOf(faction.getGems()))
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
