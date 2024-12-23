package net.skullian.skyfactions.common.api;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.skullian.skyfactions.common.defence.Defence;
import net.skullian.skyfactions.common.defence.hologram.DefenceTextHologram;
import net.skullian.skyfactions.common.defence.struct.DefenceData;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.common.util.SkyItemStack;
import net.skullian.skyfactions.common.util.SkyLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public abstract class DefenceAPI {

    private final Map<String, List<Defence>> loadedFactionDefences = new HashMap<>();
    private final Map<UUID, List<Defence>> loadedPlayerDefences = new HashMap<>();

    private final Map<String, DefenceTextHologram> hologramsMap = new ConcurrentHashMap<>();

    /**
     * Create a defence item stack.
     * Ths primarily involves adding PDC to the defence stacks.
     *
     * @param defence DefenceStruct of the defence.
     * @param player  Player to give the item to -> The item is not given to the player here, but only used in fetching the player's locale for languages.
     * @return {@link SkyItemStack} of the defence.
     */
    @NotNull public abstract SkyItemStack createDefenceStack(DefenceStruct defence, SkyUser player);

    /**
     * Add a defence to the player's inventory.
     *
     * @param player  Player to give the item to.
     * @param defence DefenceStruct of the defence.
     * @param faction Faction the player belongs to (if applicable, handled internally).
     *
     */
    public abstract void addDefence(SkyUser player, DefenceStruct defence, Faction faction);

    /**
     * Check whether an item is a defence item.
     *
     * @param item SkyItemStack in question.
     * @return true if the item is a defence item.
     */
    public abstract boolean isDefence(SkyItemStack item);

    /**
     * Check whether a block is a defence.
     *
     * @param location Location of the block.
     * @return true if the block is a defence.
     */
    public abstract boolean isDefence(SkyLocation location);

    /**
     * Get the formatted lore of a defence item from its struct.
     *
     * @param struct DefenceStruct of the defence.
     * @param lore   Configured lore (without placeholders%).
     * @param player Player in question -> Used for the locale.
     *
     * @return {@link List<Component>} list of formatted components.
     */
    @NotNull public abstract List<String> getFormattedLore(DefenceStruct struct, List<String> lore, SkyUser player);

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
    @Nullable public abstract Defence getLoadedDefence(SkyLocation location);

    /**
     * A generally simple method to return a defence to a player.
     *
     * @param struct Defence struct to create the defence ItemStack from.
     * @param player Player to give the ItemStack to.
     */
    public abstract void returnDefence(DefenceStruct struct, SkyUser player);

    /**
     * Check if a player has permissions to access a defence / certain regions of it.
     *
     * @param permissions Allowed permissions.
     * @param player Player to check.
     * @param faction Faction the player belongs to.
     *
     * @return true if the player has permissions.
     */
    public abstract boolean hasPermissions(List<String> permissions, SkyUser player, Faction faction);

    /**
     * Called on if the player does not have sufficient permissions to manage x region of the defence (e.g. upgrade).
     *
     * @param builder Builder of the item.
     * @param player Player in question.
     *
     * @return {@link SkyItemStack.SkyItemStackBuilder} - The modified ItemBuilder with the insufficient permissions lore added.
     */
    @NotNull public abstract SkyItemStack.SkyItemStackBuilder processPermissions(SkyItemStack.SkyItemStackBuilder builder, SkyUser player);

    /**
     * Check if a block is a defence material. Useful for quick efficient checking rather than immediately checking the block's PDC.
     *
     * @param block Block to check.
     *
     * @return true if the block is a defence material.
     */
    public abstract boolean isDefenceMaterial(SkyLocation block);

    /**
     * Get the defence struct from an item.
     *
     * @param itemStack ItemStack to check.
     * @param player Used for locale.
     *
     * @return {@link DefenceStruct} - The defence struct of the item, if valid.
     */
    @Nullable public abstract DefenceStruct getDefenceFromItem(SkyItemStack itemStack, SkyUser player);

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

    public static String solveFormula(String formula, int level) {
        if (formula == null) return "N/A";
        try {
            Expression expression = new ExpressionBuilder(formula)
                    .variable("level")
                    .build()
                    .setVariable("level", level);

            if (!expression.validate().isValid()) return "N/A";

            return String.valueOf(Math.round(expression.evaluate()));
        } catch (Exception e) {
            SLogger.fatal("Encountered an error when trying to evaluate defence formulas - {}", e);
        }

        return "N/A";
    }

    public void refresh() {
        SLogger.info("Refreshing existing loaded defences...");

        SkyApi.getInstance().getDefenceAPI().getLoadedPlayerDefences().values().stream()
                .flatMap(List::stream)
                .forEach(Defence::refresh);

        SkyApi.getInstance().getDefenceAPI().getLoadedFactionDefences().values().stream()
                .flatMap(List::stream)
                .forEach(Defence::refresh);
    }
}
