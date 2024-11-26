package net.skullian.skyfactions.common.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public abstract class GemsAPI {

    public abstract void addGems(UUID playerUUID, int addition);

    public abstract void subtractGems(UUID playerUUID, int subtraction);

    public abstract ItemStack createGemsStack(Player player);

    public abstract boolean isGemsStack(ItemStack stack);

}
