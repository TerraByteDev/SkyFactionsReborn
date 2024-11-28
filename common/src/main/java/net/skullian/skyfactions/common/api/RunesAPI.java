package net.skullian.skyfactions.common.api;

import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public abstract class RunesAPI {

    /**
     * See whether an item is allowed for rune conversion.
     *
     * @param stack  ItemStack in question/
     * @param player Player related to the ItemStack.
     * @return {@link Boolean}
     */
    public abstract boolean isStackProhibited(SkyItemStack stack, SkyUser player);

    /**
     * Used in the runes conversion UI. This is used through Player obelisks.
     * @param stacks List of ItemStacks from the VirtualInventory to submit.
     * @param player PLayer who is converting the items -> runes.
     */
    public abstract void handlePlayerRuneConversion(List<SkyItemStack> stacks, SkyUser player);

    /**
     * Used in the runes conversion UI. This is used through Faction obelisks.
     *
     * @param stacks List of ItemStacks from the VirtualInventory to submit.
     * @param player Member of the Faction who is converting the items -> runes.
     */
    public abstract void handleFactionRuneConversion(List<SkyItemStack> stacks, SkyUser player);

    /**
     * Internally referenced by {@link #handlePlayerRuneConversion(List, SkyUser)} and {@link #handleFactionRuneConversion(List, SkyUser)}.
     *
     * @param stacks List of ItemStacks from the VirtualInventory to submit.
     * @param player PLayer who is converting the items -> runes.
     */
    public abstract void handleRuneConversion(List<SkyItemStack> stacks, SkyUser player, Faction faction);

    /**
     * Remove runes from a player.
     *
     * @param playerUUID UUID of the Player {@link UUID}
     * @param amount     Amount of runes to remove {@link Integer}
     */
    public abstract void removeRunes(UUID playerUUID, int amount);

    /**
     * Add runes to a player's rune balance.
     *
     * @param playerUUID UUID of the Player {@link UUID}
     * @param amount     Amount of runes to add {@link Integer}
     */
    public abstract void addRunes(UUID playerUUID, int amount);

    /**
     * This is a very simple method to check if an ItemStack has any enchantments.
     * Used within the runes conversion UI.
     *
     * @param stack Stack to check.
     *
     * @return true if the ItemStack has enchantments.
     */
    public abstract boolean hasEnchants(SkyItemStack stack);

    /**
     * Checks for any lore present in the ItemStack.
     * Used within the runes conversion UI.
     *
     * @param stack Stack to check.
     *
     * @return true if the ItemStack has lore present.
     */
    public abstract boolean hasLore(SkyItemStack stack);

    /**
     * Checks with the runes conversion configuration whether the ItemStack is allowed, primary to check it against the blacklist config.
     * @param stack Stack to check.
     *
     * @return true if the ItemStack is allowed to be converted.
     */
    public abstract boolean isAllowed(SkyItemStack stack);

    /**
     * Used to get the item ID of an ItemStack in content plugins such as Oraxen and ItemsAdder.
     *
     * @param stack Stack to get the ID of.
     *
     * @return {@link String} of the corresponding custom item from the content plugin. Will just return the name of the material if it's not a custom item.
     */
    @NotNull public abstract String getItemID(SkyItemStack stack);

    /**
     * Used after items have been converted into runes. ItemStacks that did not meet the requirements for conversion (e.g. too little amounts of said ItemStack) will be sent here.
     *
     * @param stacks Stacks to return to the player.
     * @param player Player to return the items to.
     */
    public abstract void returnItems(List<SkyItemStack> stacks, SkyUser player);
}
