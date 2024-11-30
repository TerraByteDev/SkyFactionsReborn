package net.skullian.skyfactions.paper.api.adapter;

import io.th0rgal.oraxen.items.ItemBuilder;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;
import net.skullian.skyfactions.common.util.SkyLocation;
import net.skullian.skyfactions.common.util.text.TextUtility;
import net.skullian.skyfactions.paper.SkyFactionsReborn;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.ItemProvider;

public class SpigotAdapter {

    public static Location adapt(SkyLocation location) {
        return new Location(
                Bukkit.getWorld(location.getWorldName()),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );
    }

    public static OfflinePlayer adapt(SkyUser user) {
        return Bukkit.getOfflinePlayer(user.getUniqueId());
    }

    public static ItemStack adapt(SkyItemStack skyStack, SkyUser user, boolean legacy) {
        String locale = user != null ? SkyApi.getInstance().getPlayerAPI().getLocale(user.getUniqueId()) : Messages.getDefaulLocale();
        
        ItemStack stack = new ItemStack(Material.valueOf(skyStack.getMaterial()));
        stack.setAmount(skyStack.getAmount());

        ItemMeta meta = stack.getItemMeta();
        if (!legacy) meta.lore(TextUtility.color(skyStack.getLore(), locale, user));
            else meta.setLore(TextUtility.legacyColor(skyStack.getLore(), locale, user));
            
        if (!legacy) meta.displayName(TextUtility.color(skyStack.getDisplayName(), locale, user));
            else meta.setDisplayName(TextUtility.legacyColor(skyStack.getDisplayName(), locale, user));
            
        if (skyStack.getCustomModelData() != -1) meta.setCustomModelData(skyStack.getCustomModelData());

        for (SkyItemStack.EnchantData enchantData : skyStack.getEnchants()) {
            meta.addEnchant(Enchantment.getByName(enchantData.getEnchant()), enchantData.getLevel(), enchantData.isIgnoreLevelRestriction());
        }

        for (SkyItemStack.PersistentData pdcEntry : skyStack.getPersistentData()) {
            NamespacedKey key = new NamespacedKey(SkyFactionsReborn.getInstance(), pdcEntry.getKey());
            meta.getPersistentDataContainer().set(key, getPersistentDataType(pdcEntry.getType()), pdcEntry.getData());
        }
        
        stack.setItemMeta(meta);

        return stack;
    }

    public static ItemProvider adapt(SkyItemStack skyStack, SkyUser user) {
        return new ItemProvider() {
            @NotNull
            @Override
            public ItemStack get(@Nullable String lang) {
                return new ItemBuilder(adapt(skyStack, user, true)).build();
            }
        };
    }

    private static PersistentDataType getPersistentDataType(String type) {
        return switch (type) {
            case "BYTE" -> PersistentDataType.BYTE;
            case "SHORT" -> PersistentDataType.SHORT;
            case "INT" -> PersistentDataType.INTEGER;
            case "LONG" -> PersistentDataType.LONG;
            case "FLOAT" -> PersistentDataType.FLOAT;
            case "DOUBLE" -> PersistentDataType.DOUBLE;
            case "BYTE_ARRAY" -> PersistentDataType.BYTE_ARRAY;
            case "INTEGER_ARRAY" -> PersistentDataType.INTEGER_ARRAY;
            case "LONG_ARRAY" -> PersistentDataType.LONG_ARRAY;
            default -> PersistentDataType.STRING;
        };
    }

}