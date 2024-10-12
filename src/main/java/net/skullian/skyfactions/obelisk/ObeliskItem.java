package net.skullian.skyfactions.obelisk;

import lombok.Getter;
import net.skullian.skyfactions.config.types.ObeliskConfig;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@Getter
public class ObeliskItem {

    private String displayName;
    private List<String> lore;
    private int customModelData;

    public ObeliskItem(ItemStack itemStack) {
        this.displayName = itemStack.getItemMeta().getDisplayName();
        this.lore = itemStack.getItemMeta().getLore();
        if (itemStack.getItemMeta().hasCustomModelData()) {
            this.customModelData = itemStack.getItemMeta().getCustomModelData();
        } else {
            this.customModelData = ObeliskConfig.OBELISK_CUSTOM_MODEL_DATA.getInt();
        }
    }

    public ItemStack getItem(int amount) {
        ItemStack item = new ItemStack(Material.BARRIER, amount);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(this.displayName);
        itemMeta.setLore(this.lore);
        if (this.customModelData != 0) {
            itemMeta.setCustomModelData(this.customModelData);
        }

        item.setItemMeta(itemMeta);

        return item;
    }


}
