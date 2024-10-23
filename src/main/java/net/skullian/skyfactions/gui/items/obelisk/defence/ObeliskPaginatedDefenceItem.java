package net.skullian.skyfactions.gui.items.obelisk.defence;

import net.skullian.skyfactions.config.types.ObeliskConfig;
import net.skullian.skyfactions.defence.DefencesFactory;
import net.skullian.skyfactions.defence.struct.DefenceStruct;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.obelisk.defence.ObeliskPurchaseDefenceUI;
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

public class ObeliskPaginatedDefenceItem extends AsyncItem {

    private String SOUND;
    private int PITCH;
    private DefenceStruct STRUCT;
    private boolean SHOULD_REDIRECT;
    private String TYPE;
    private Faction FACTION;

    public ObeliskPaginatedDefenceItem(ItemData data, ItemStack stack, DefenceStruct struct, boolean shouldRedirect, String type, Faction faction) {
        super(
                ObeliskConfig.getLoadingItem(),
                () -> {
                    return new ItemProvider() {
                        @Override
                        public @NotNull ItemStack get(@Nullable String s) {
                            ItemBuilder builder = new ItemBuilder(stack)
                                    .setDisplayName(TextUtility.color(struct.getNAME()));

                            String maxLevel = String.valueOf(struct.getMAX_LEVEL());
                            String range = DefencesFactory.solveFormula(struct.getATTRIBUTES().getRANGE(), 1);
                            String ammo = DefencesFactory.solveFormula(struct.getATTRIBUTES().getMAX_AMMO(), 1);
                            String targetMax = DefencesFactory.solveFormula(struct.getATTRIBUTES().getMAX_TARGETS(), 1);
                            String damage = DefencesFactory.solveFormula(struct.getATTRIBUTES().getDAMAGE(), 1);
                            String cooldown = DefencesFactory.solveFormula(struct.getATTRIBUTES().getCOOLDOWN(), 1);
                            String healing = DefencesFactory.solveFormula(struct.getATTRIBUTES().getHEALING(), 1);
                            String distance = DefencesFactory.solveFormula(struct.getATTRIBUTES().getDISTANCE(), 1);
                            String repairCost = DefencesFactory.solveFormula(struct.getREPAIR_COST(), 1);

                            for (String loreLine : struct.getITEM_LORE()) {
                                builder.addLoreLines(TextUtility.color(loreLine
                                        .replace("%max_level%", maxLevel)
                                        .replace("%range%", range)
                                        .replace("%ammo%", ammo)
                                        .replace("%target_max%", targetMax)
                                        .replace("%damage%", damage)
                                        .replace("%cooldown%", cooldown)
                                        .replace("%healing%", healing)
                                        .replace("%distance%", distance)
                                        .replace("%repair_cost%", repairCost)
                                        .replace("%cost%", String.valueOf(struct.getBUY_COST()))));
                            }

                            return builder.get();
                        }
                    };
                }
        );

        this.SOUND = data.getSOUND();
        this.PITCH = data.getPITCH();
        this.STRUCT = struct;
        this.SHOULD_REDIRECT = shouldRedirect;
        this.TYPE = type;
        this.FACTION = faction;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.setCancelled(true);

        if (!SOUND.equalsIgnoreCase("none")) {
            SoundUtil.playSound(player, SOUND, PITCH, 1);
        }

        if (SHOULD_REDIRECT) {
            ObeliskPurchaseDefenceUI.promptPlayer(player, TYPE, STRUCT, FACTION);
        }
    }
}
