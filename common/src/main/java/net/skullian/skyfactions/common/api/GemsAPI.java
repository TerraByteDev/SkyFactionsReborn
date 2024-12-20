package net.skullian.skyfactions.common.api;

import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

import java.util.UUID;

public abstract class GemsAPI {

    /**
     * Add gems to a player.
     *
     * @param playerUUID UUID of player to give gems to.
     * @param addition   Amount of gems to add.
     */
    public void addGems(UUID playerUUID, int addition) {
        if (!SkyApi.getInstance().getUserManager().isCached(playerUUID)) SkyApi.getInstance().getUserManager().getUser(playerUUID).getGems();

        SkyApi.getInstance().getCacheService().getEntry(playerUUID).addGems(addition);
    }

    /**
     * Subtract gems from a player.
     *
     * @param playerUUID  UUID of player to subtract gems from.
     * @param subtraction Amount of gems to remove.
     */
    public void subtractGems(UUID playerUUID, int subtraction) {
        if (!SkyApi.getInstance().getUserManager().isCached(playerUUID)) SkyApi.getInstance().getUserManager().getUser(playerUUID).getGems();

        SkyApi.getInstance().getCacheService().getEntry(playerUUID).removeGems(subtraction);
    }

    /**
     * Create a gems stack for a player.
     *
     * @param player Player to create the gems stack for.
     *
     * @return {@link SkyItemStack}
     */
    public SkyItemStack createGemsStack(SkyUser player) {
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

        return SkyItemStack.builder()
                .material(Settings.GEMS_MATERIAL.getString())
                .displayName(Messages.GEMS_ITEM_NAME.getString(locale))
                .lore(Messages.GEMS_ITEM_LORE.getStringList(locale))
                .persistentData(new SkyItemStack.PersistentData("gem", "BOOLEAN", true))
                .amount(1)
                .customModelData(Settings.GEMS_CUSTOM_MODEL_DATA.getInt())
                .build();
    }

    public abstract int depositAllItems(SkyUser player, SkyItemStack currencyItem);

    public abstract int depositSpecificAmount(SkyUser player, SkyItemStack currencyItem, int amount);

    public abstract int addItemToInventory(SkyUser user, SkyItemStack stack);

    /**
     * Check if a stack is a gems stack.
     *
     * @param stack Stack to check.
     *
     * @return {@code true} if the stack is a gems stack, otherwise {@code false}.
     */
    public boolean isGemsStack(SkyItemStack stack) {
        if (stack == null) return false;

        return stack.hasPersistentData("gem");
    }
}
