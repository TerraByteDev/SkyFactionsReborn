package net.skullian.skyfactions.paper.api.adapter;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.sk89q.worldedit.math.BlockVector3;
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
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import java.util.UUID;

@SuppressWarnings("rawtypes")
public class SpigotAdapter {

    public static Location adapt(SkyLocation location) {
        return new Location(
                location.getWorldName() != null ? Bukkit.getWorld(location.getWorldName()) : null,
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );
    }

    public static SkyLocation adapt(Location location) {
        return new SkyLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
    }

    public static OfflinePlayer adapt(SkyUser user) {
        return Bukkit.getOfflinePlayer(user.getUniqueId());
    }

    @SuppressWarnings("deprecation, unchecked")
    public static ItemStack adapt(SkyItemStack skyStack, SkyUser user, boolean legacy) {
        String locale = user != null ? SkyApi.getInstance().getPlayerAPI().getLocale(user.getUniqueId()) : Messages.getDefaulLocale();

        ItemStack stack;
        if (skyStack.getSerializedBytes() != null) stack = ItemStack.deserializeBytes((byte[]) skyStack.getSerializedBytes());
            else stack = new ItemStack(Material.valueOf(skyStack.getMaterial() != null ? skyStack.getMaterial() : "AIR"));

        stack.setAmount(skyStack.getAmount());

        if (!skyStack.getOwningPlayerUUID().equals("none")) {
            SkullMeta skullMeta = (SkullMeta) stack.getItemMeta();
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(skyStack.getOwningPlayerUUID())));

            stack.setItemMeta(skullMeta);
        } else if (skyStack.getTextures() != null && !skyStack.getTextures().equalsIgnoreCase("none")) {
            stack.editMeta(SkullMeta.class, skullMeta -> {
                UUID uuid = UUID.randomUUID();
                PlayerProfile playerProfile = Bukkit.createProfile(uuid, uuid.toString().substring(0, 16));
                playerProfile.setProperty(new ProfileProperty("textures", skyStack.getTextures()));

                skullMeta.setPlayerProfile(playerProfile);
            });
        }

        ItemMeta meta = stack.getItemMeta();
        if (!legacy) meta.lore(TextUtility.color(skyStack.getLore(), locale, user));
            else meta.setLore(TextUtility.legacyColor(skyStack.getLore(), locale, user));
            
        if (!legacy) meta.displayName(TextUtility.color(skyStack.getDisplayName(), locale, user));
            else meta.setDisplayName(TextUtility.legacyColor(skyStack.getDisplayName(), locale, user));
            
        if (skyStack.getCustomModelData() != -1) meta.setCustomModelData(skyStack.getCustomModelData());

        for (SkyItemStack.EnchantData enchantData : skyStack.getEnchants()) {
            Enchantment enchantment = Enchantment.getByName(enchantData.getEnchant());
            if (enchantment != null) {
                meta.addEnchant(enchantment, enchantData.getLevel(), enchantData.isIgnoreLevelRestriction());
            }
        }

        for (SkyItemStack.PersistentData pdcEntry : skyStack.getPersistentData()) {
            NamespacedKey key = new NamespacedKey(SkyFactionsReborn.getInstance(), pdcEntry.getKey());
            meta.getPersistentDataContainer().set(key, getPersistentDataType(pdcEntry.getType()), pdcEntry.getData());
        }
        
        stack.setItemMeta(meta);

        return stack;
    }

    public static SkyItemStack adapt(ItemStack stack) {
        return SkyItemStack.builder()
                .serializedBytes(stack.serializeAsBytes())
                .build();
    }


    public static SkyLocation adapt(BlockVector3 vector3, String world) {
        return new SkyLocation(
                world,
                vector3.x(),
                vector3.y(),
                vector3.z()
        );
    }

    public static ItemProvider adapt(SkyItemStack skyStack, SkyUser user) {
        return new ItemProvider() {
            @NotNull
            @Override
            public ItemStack get(@Nullable String lang) {
                return new ItemBuilder(adapt(skyStack, user, true)).get();
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
