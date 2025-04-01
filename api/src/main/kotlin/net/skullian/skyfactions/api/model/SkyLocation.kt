package net.skullian.skyfactions.api.model

/**
 * Represents a location in a specific world with coordinates and orientation.
 *
 * @property worldName The name of the world.
 * @property x The x-coordinate.
 * @property y The y-coordinate.
 * @property z The z-coordinate.
 * @property yaw The yaw orientation.
 * @property pitch The pitch orientation.
 */
class SkyLocation(
    var worldName: String,
    var x: Double,
    var y: Double,
    var z: Double,
    var yaw: Float,
    var pitch: Float
) : Cloneable {

    /**
     * Secondary constructor to create a SkyLocation with default yaw and pitch.
     *
     * @param worldName The name of the world.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param z The z-coordinate.
     */
    constructor(worldName: String, x: Double, y: Double, z: Double) : this(worldName, x, y, z, 0F, 0F)

    /**
     * Adds the coordinates of another SkyLocation to this location.
     *
     * @param location The other SkyLocation.
     * @return The updated SkyLocation.
     */
    fun add(location: SkyLocation): SkyLocation {
        this.x += location.x
        this.y += location.y
        this.z += location.z
        return this
    }

    /**
     * Subtracts the coordinates of another SkyLocation from this location.
     *
     * @param location The other SkyLocation.
     * @return The updated SkyLocation.
     */
    fun subtract(location: SkyLocation): SkyLocation {
        this.x -= location.x
        this.y -= location.y
        this.z -= location.z
        return this
    }

    /**
     * Multiplies the coordinates of this location by the coordinates of another SkyLocation.
     *
     * @param location The other SkyLocation.
     * @return The updated SkyLocation.
     */
    fun multiply(location: SkyLocation): SkyLocation {
        this.x *= location.x
        this.y *= location.y
        this.z *= location.z
        return this
    }

    /**
     * Creates a copy of this SkyLocation.
     *
     * @return A new SkyLocation with the same properties.
     */
    override fun clone(): SkyLocation {
        return super.clone() as SkyLocation
    }

    /**
     * Gets the block x-coordinate of this location.
     *
     * @return The block x-coordinate.
     */
    fun getBlockX(): Int {
        return toBlockLocation(this.x)
    }

    /**
     * Gets the block y-coordinate of this location.
     *
     * @return The block y-coordinate.
     */
    fun getBlockY(): Int {
        return toBlockLocation(this.y)
    }

    /**
     * Gets the block z-coordinate of this location.
     *
     * @return The block z-coordinate.
     */
    fun getBlockZ(): Int {
        return toBlockLocation(this.z)
    }

    /**
     * Converts a double coordinate to a block coordinate.
     *
     * @param loc The double coordinate.
     * @return The block coordinate.
     */
    private fun toBlockLocation(loc: Double): Int {
        val floor = loc.toInt()
        return if (floor.toDouble() == loc) floor else floor - (java.lang.Double.doubleToRawLongBits(loc) ushr 63).toInt()
    }
}