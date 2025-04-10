package net.skullian.skyfactions.paper.hook

import net.skullian.skyfactions.api.SkyApi
import net.skullian.skyfactions.api.hook.HookLoadOrder
import net.skullian.skyfactions.api.hook.HookManager
import net.skullian.skyfactions.api.hook.SkyHook
import net.skullian.skyfactions.common.util.SLogger

/**
 * The paper implementation of [HookManager].
 * As discussed before, [getHookLocation] must be implemented for each platform,
 * as each platform will have different "hooks".
 */
class PaperHookManager : HookManager {

    override fun getHookLocation(): String {
        return "net.skullian.skyfactions.paper.hook.hooks"
    }

    /**
     * This will be called by the platform to initialize the hooks.
     * @param order The current order of hooks to be loaded. This method is called multiple times for each [HookLoadOrder] stage.
     */
    override fun initialize(order: HookLoadOrder) {
        val hooks = hooks[order] ?: return

        for (hook in hooks) {
            val pluginName = getHookPluginName(hook)
            if (SkyApi.getInstance().getPlatform().isPluginEnabled(pluginName)) {
                // todo check for hook config
                SLogger.info("Hooking into {} via SkyHook {}", "<yellow>$pluginName</yellow>", "<green>${hook.javaClass.name}</green>")
                hook.onEnable()

                loadedHooks.add(hook.javaClass)
            } else {
                SLogger.warn("Could not hook into {} as it is not enabled.", "<yellow>${hook.javaClass.name}</yellow>", "<red>$pluginName</red>")
            }
        }
    }

    /**
     * Called when the platform disables (typically server shutdown).
     */
    override fun onDisable() {
        SLogger.info("Disabling hooks...")

        for (hook in loadedHooks) {
            (hook as SkyHook).onDisable()
        }
    }

}