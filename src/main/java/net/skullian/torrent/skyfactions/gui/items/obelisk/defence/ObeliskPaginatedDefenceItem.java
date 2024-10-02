package net.skullian.torrent.skyfactions.gui.items.obelisk.defence;

import net.skullian.torrent.skyfactions.defence.DefencesRegistry;
import net.skullian.torrent.skyfactions.defence.struct.DefenceStruct;
import net.skullian.torrent.skyfactions.faction.Faction;
import net.skullian.torrent.skyfactions.gui.data.ItemData;
import net.skullian.torrent.skyfactions.gui.obelisk.defence.ObeliskPurchaseDefenceUI;
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

public class ObeliskPaginatedDefenceItem extends AbstractItem {

    private String NAME;
    private String SOUND;
    private int PITCH;
    private List<String> LORE;
    private ItemStack STACK;
    private DefenceStruct STRUCT;
    private boolean SHOULD_REDIRECT;
    private String TYPE;
    private Faction FACTION;

    public ObeliskPaginatedDefenceItem(ItemData data, ItemStack stack, DefenceStruct struct, boolean shouldRedirect, String type, Faction faction) {
        this.NAME = data.getNAME();
        this.SOUND = data.getSOUND();
        this.PITCH = data.getPITCH();
        this.LORE = data.getLORE();
        this.STACK = stack;
        this.STRUCT = struct;
        this.SHOULD_REDIRECT = shouldRedirect;
        this.TYPE = type;
        this.FACTION = faction;
    }

    @Override
    public ItemProvider getItemProvider() {
        ItemBuilder builder = new ItemBuilder(STACK)
                .setDisplayName(TextUtility.color(NAME));

        for (String loreLine : LORE) {
            builder.addLoreLines(TextUtility.color(loreLine
                    .replace("%max_level%", String.valueOf(STRUCT.getMAX_LEVEL()))
                    .replace("%range%", DefencesRegistry.solveFormula(STRUCT.getATTRIBUTES().getRANGE(), 1))
                    .replace("%ammo%", DefencesRegistry.solveFormula(STRUCT.getATTRIBUTES().getMAX_AMMO(), 1))
                    .replace("%target_max%", DefencesRegistry.solveFormula(STRUCT.getATTRIBUTES().getMAX_TARGETS(), 1))
                    .replace("%damage%", DefencesRegistry.solveFormula(STRUCT.getATTRIBUTES().getDAMAGE(), 1))
                    .replace("%cooldown%", DefencesRegistry.solveFormula(STRUCT.getATTRIBUTES().getDAMAGE(), 1))
                    .replace("%healing%", DefencesRegistry.solveFormula(STRUCT.getATTRIBUTES().getHEALING(), 1))
                    .replace("%distance%", DefencesRegistry.solveFormula(STRUCT.getATTRIBUTES().getDISTANCE(), 1))));
        }

        return builder;
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
