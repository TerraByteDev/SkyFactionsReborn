package net.skullian.skyfactions.api.hook.annotation

import net.skullian.skyfactions.api.hook.HookLoadOrder

/**
 * An annotation used to mark classes that are hooks.
 * This annotation is used to provide metadata about the hook, such as the plugin name and load order.
 *
 * @property pluginName The plugin name that this hook is for. The hook will not load if the plugin is not present.
 * @property loadOrder The [HookLoadOrder] of the hook. This determines when the hook will be loaded.
 */
@Target(AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
annotation class HookData (
    val pluginName: String,
    val loadOrder: HookLoadOrder = HookLoadOrder.ENABLE
)