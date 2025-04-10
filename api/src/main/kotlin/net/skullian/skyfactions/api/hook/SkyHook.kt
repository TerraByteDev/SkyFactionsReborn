package net.skullian.skyfactions.api.hook

import net.skullian.skyfactions.api.hook.annotation.HookData

/**
 * An interface representing a plugin / mod hook.
 * A platform hook would implement this as well as adding its own methods.
 *
 * Requires the [HookData] annotation.
 */
interface SkyHook {

    /**
     * Called when the hook is enabled, assuming it meets the criteria
     * outlined in the [HookData] annotation.
     */
    fun onEnable()

    /**
     * Called on platform shutdown.
     */
    fun onDisable()

}