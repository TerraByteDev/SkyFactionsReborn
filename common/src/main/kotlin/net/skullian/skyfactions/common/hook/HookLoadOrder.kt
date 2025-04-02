package net.skullian.skyfactions.common.hook

/**
 * A simple enum that allows hook classes to specify when they should be loaded.
 */
enum class HookLoadOrder {

    /**
     * When all plugins are loaded (onLoad method in spigot).
     */
    LOAD,

    /**
     * When the plugin is enabled (onEnable method in spigot).
     */
    ENABLE,

    /**
     * When the server has finished loading (ServerLoadEvent in spigot).
     */
    START
}