package net.skullian.skyfactions.api;

import com.jeff_media.customblockdata.CustomBlockData;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.defence.DefencesFactory;
import net.skullian.skyfactions.defence.struct.DefenceStruct;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class DefenceAPI {

    /**
     * Get the itemstack of a defence.
     *
     * @param defence Struct of the defence.
     * @return
     */
    public static ItemStack createDefenceStack(DefenceStruct defence) {
        ItemStack stack = SkullAPI.convertToSkull(new ItemStack(Material.getMaterial(defence.getITEM_MATERIAL())), defence.getITEM_SKULL());
        NamespacedKey nameKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-identifier");

        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(TextUtility.color(defence.getNAME()));
        meta.getPersistentDataContainer().set(nameKey, PersistentDataType.STRING, defence.getIDENTIFIER());

        meta.setLore(getFormattedLore(defence, defence.getITEM_LORE()));
        stack.setItemMeta(meta);

        return stack;
    }

    public static boolean isDefence(ItemStack item) {
        NamespacedKey defenceKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-identifier");
        return item.getItemMeta().getPersistentDataContainer().has(defenceKey, PersistentDataType.STRING);
    }

    public static boolean isDefence(Location location) {
        Block block = location.getBlock();
        NamespacedKey defenceKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-identifier");

        PersistentDataContainer container = new CustomBlockData(block, SkyFactionsReborn.getInstance());
        if (container.has(defenceKey, PersistentDataType.STRING)) {
            return true;
        }

        return false;
    }

    private static List<String> getFormattedLore(DefenceStruct struct, List<String> lore) {
        String maxLevel = String.valueOf(struct.getMAX_LEVEL());
        String range = DefencesFactory.solveFormula(struct.getATTRIBUTES().getRANGE(), 1);
        String ammo = DefencesFactory.solveFormula(struct.getATTRIBUTES().getMAX_AMMO(), 1);
        String targetMax = DefencesFactory.solveFormula(struct.getATTRIBUTES().getMAX_TARGETS(), 1);
        String damage = DefencesFactory.solveFormula(struct.getATTRIBUTES().getDAMAGE(), 1);
        String cooldown = DefencesFactory.solveFormula(struct.getATTRIBUTES().getCOOLDOWN(), 1);
        String healing = DefencesFactory.solveFormula(struct.getATTRIBUTES().getHEALING(), 1);
        String distance = DefencesFactory.solveFormula(struct.getATTRIBUTES().getDISTANCE(), 1);
        List<String> newLore = new ArrayList<>();

        for (String str : lore) {
            newLore.add(TextUtility.color(str
                    .replace("%max_level%", maxLevel)
                    .replace("%range%", range)
                    .replace("%ammo%", ammo)
                    .replace("%target_max%", targetMax)
                    .replace("%damage%", damage)
                    .replace("%cooldown%", cooldown)
                    .replace("%healing%", healing)
                    .replace("%distance%", distance)
                    .replace("%cost%", String.valueOf(struct.getBUY_COST()))));
        }

        return newLore;
    }
}
