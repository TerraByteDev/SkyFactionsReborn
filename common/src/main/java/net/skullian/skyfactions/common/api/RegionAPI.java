package net.skullian.skyfactions.common.api;

import com.sk89q.worldedit.regions.Region;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyLocation;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public abstract class RegionAPI {

    /**
     * Cut a region. Used in island deletion.
     *
     * @param region WorldEdit (WorldGuard) region to delete.
     *
     * @return {@link CompletableFuture<Void>}
     */
    public abstract CompletableFuture<Void> cutRegion(Region region);

    /**
     * Check if a location is within a WE/WG Region.
     * Typically used to check if a player tries to teleport to their island while they are already in it.
     *
     * @param loc Location to check.
     * @param regionName Region to check if the location is in.
     *
     * @return true if the location is in the specified WE/WG Region.
     */
    public abstract boolean isLocationInRegion(SkyLocation loc, String regionName);

    /**
     * Create a new region. Typically used for island creation.
     *
     * @param player Player who will own the region.
     * @param corner1 Bottom corner of the region.
     * @param corner2 Opposite top corner of the region.
     * @param world World to create the region in.
     * @param regionName Name of the region.
     *
     */
    public abstract void createRegion(SkyUser player, SkyLocation corner1, SkyLocation corner2, String world, String regionName);

    /**
     * Get the location of the hub (configured in config.yml).
     *
     * @return {@link SkyLocation} location of the Hub.
     */
    @NotNull public abstract SkyLocation getHubLocation();

    /**
     * Modify a player's world border.
     *
     * @param player Player who's WorldBorder should be modified.
     * @param center New center of the worldborder.
     * @param size New size of the worldborder.
     */
    public abstract void modifyWorldBorder(SkyUser player, SkyLocation center, int size);

    /**
     * Paste an island schematic.
     * Used for both player and faction islands.
     *
     * @param player Owning player of the island.
     * @param location Location that the schematic should be pasted at.
     * @param worldName Name of the world the schematic should be pasted in -> todo just use location.getWorld() once verified all provide locs don't provide a null world.
     * @param type Type of the schematic ("player" / "faction")
     *
     * @return {@link CompletableFuture<Boolean>} - true if the schematic was pasted successfully.
     */
    public abstract CompletableFuture<Boolean> pasteIslandSchematic(SkyUser player, SkyLocation location, String worldName, String type);

    /**
     * Remove a region.
     *
     * @param regionName Name of the region to remove.
     * @param world World that the region belongs to.
     *
     * @return {@link CompletableFuture<Void>}
     */
    public abstract CompletableFuture<Void> removeRegion(String regionName, String world);

}
