package net.skullian.skyfactions.paper.api;

import net.skullian.skyfactions.common.api.GemsAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.util.text.TextUtility;
import net.skullian.skyfactions.paper.SkyFactionsReborn;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class SpigotGemsAPI extends GemsAPI {

    /**
     * Add gems to a player.
     *
     * @param playerUUID UUID of player to give gems to.
     * @param addition   Amount of gems to add.
     */
    @Override
    public void addGems(UUID playerUUID, int addition) {
        if (!SkyApi.getInstance().getUserManager().isCached(playerUUID)) SkyApi.getInstance().getUserManager().getUser(playerUUID);

        SkyApi.getInstance().getCacheService().getEntry(playerUUID).addGems(addition);
    }

    /**
     * Subtract gems from a player.
     *
     * @param playerUUID  UUID of player to subtract gems from.
     * @param subtraction Amount of gems to remove.
     */
    @Override
    public void subtractGems(UUID playerUUID, int subtraction) {
        if (!SkyApi.getInstance().getUserManager().isCached(playerUUID)) SkyApi.getInstance().getUserManager().getUser(playerUUID);

        SkyApi.getInstance().getCacheService().getEntry(playerUUID).removeGems(subtraction);
    }

    @Override
    public ItemStack createGemsStack(Player player) {
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());
        ItemStack stack = new ItemStack(Material.valueOf(Settings.GEMS_MATERIAL.getString()));

        ItemMeta meta = stack.getItemMeta();
        if (Settings.GEMS_CUSTOM_MODEL_DATA.getInt() != -1) meta.setCustomModelData(Settings.GEMS_CUSTOM_MODEL_DATA.getInt());
        meta.displayName(TextUtility.color(Messages.GEMS_ITEM_NAME.getString(locale), locale, player));
        meta.lore(TextUtility.color(Messages.GEMS_ITEM_LORE.getStringList(locale), locale, player));

        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(SkyFactionsReborn.getInstance(), "gem");
        container.set(key, PersistentDataType.BOOLEAN, true);

        stack.setItemMeta(meta);

        return stack;
    }

    @Override
    public boolean isGemsStack(ItemStack stack) {
        if (stack == null) return false;
        NamespacedKey key = new NamespacedKey(SkyFactionsReborn.getInstance(), "gem");
        PersistentDataContainer container = stack.getItemMeta().getPersistentDataContainer();

        return container.has(key, PersistentDataType.BOOLEAN);
    }
}
