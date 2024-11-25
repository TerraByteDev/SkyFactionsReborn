package net.skullian.skyfactions.common.api;

import net.kyori.adventure.text.Component;
import net.skullian.skyfactions.common.defence.Defence;
import net.skullian.skyfactions.common.defence.struct.DefenceData;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;
import net.skullian.skyfactions.common.faction.Faction;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import java.util.List;

public abstract class DefenceAPI {

    /**
     * Create a defence item stack.
     * Ths primarily involves adding PDC to the defence stacks.
     *
     * @param defence DefenceStruct of the defence.
     * @param player  Player to give the item to -> The item is not given to the player here, but only used in fetching the player's locale for languages.
     * @return {@link ItemStack} of the defence.
     */
    @NotNull public abstract ItemStack createDefenceStack(DefenceStruct defence, Player player);

    /**
     * Check whether an item is a defence item.
     *
     * @param item ItemStack in question.
     * @return true if the item is a defence item.
     */
    public abstract boolean isDefence(ItemStack item);

    /**
     * Check whether a block is a defence.
     *
     * @param location Location of the block.
     * @return true if the block is a defence.
     */
    public abstract boolean isDefence(Location location);

    /**
     * Get the formatted lore of a defence item from its struct.
     *
     * @param struct DefenceStruct of the defence.
     * @param lore   Configured lore (without placeholders%).
     * @param player Player in question -> Used for the locale.
     *
     * @return {@link List<Component>} list of formatted components.
     */
    @NotNull public abstract List<Component> getFormattedLore(DefenceStruct struct, List<String> lore, Player player);

    /**
     * Get a loaded defence instance from its data.
     *
     * @param data Data of the defence.
     *
     * @return {@link Defence} instance.
     */
    @Nullable public abstract Defence getDefenceFromData(DefenceData data);

    /**
     * Get a loaded defence from its location.
     *
     * @param location Location of the defence.
     *
     * @return {@link Defence} - Can be null.
     */
    @Nullable public abstract Defence getLoadedDefence(Location location);

    /**
     * A generally simple method to return a defence to a player.
     *
     * @param struct Defence struct to create the defence ItemStack from.
     * @param player Player to give the ItemStack to.
     */
    public abstract void returnDefence(DefenceStruct struct, Player player);

    /**
     * Check if a player has permissions to access a defence / certain regions of it.
     *
     * @param permissions Allowed permissions.
     * @param player Player to check.
     * @param faction Faction the player belongs to.
     *
     * @return true if the player has permissions.
     */
    public abstract boolean hasPermissions(List<String> permissions, Player player, Faction faction);

    /**
     * Called on if the player does not have sufficient permissions to manage x region of the defence (e.g. upgrade).
     *
     * @param builder Builder of the item.
     * @param player Player in question.
     *
     * @return {@link ItemBuilder} - The modified ItemBuilder with the insufficient permissions lore added.
     */
    @NotNull public abstract ItemBuilder processPermissions(ItemBuilder builder, Player player);

    /**
     * Check if a block is a defence material. Useful for quick efficient checking rather than immediately checking the block's PDC.
     *
     * @param block Block to check.
     *
     * @return true if the block is a defence material.
     */
    public abstract boolean isDefenceMaterial(Block block);

    /**
     * Get the defence struct from an item.
     *
     * @param itemStack ItemStack to check.
     * @param player Used for locale.
     *
     * @return {@link DefenceStruct} - The defence struct of the item, if valid.
     */
    @Nullable public abstract DefenceStruct getDefenceFromItem(ItemStack itemStack, Player player);

    /**
     * Very rudimentary method to check if the owner of a defence (fetched through PDC as a String) is a player defence or faction.
     * This takes advantage of the UUID.fromString method to check if the owner is a player.
     * It will throw an exception, specifically a {@link IllegalArgumentException} if the UUID is invalid (in this case a faction).
     * We have measures in place to ensure Factions cannot have UUIDs as their names.
     *
     * @param owner Owner of the defence.
     *
     * @return {@link Boolean} - true if the owner is a faction.
     */
    public abstract boolean isFaction(String owner);
}
