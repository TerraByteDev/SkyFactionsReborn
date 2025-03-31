package net.skullian.skyfactions.api.event

import net.skullian.skyfactions.api.event.bus.Subscribe

/**
 * An enum representing the priority of an event.
 * All [Subscribe] annotated methods are called in the order of their priority, with LOWEST being the lowest priority and MONITOR being the highest.
 */
enum class EventPriority {
    LOWEST,
    LOW,

    /**
     * The default priority of an event.
     */
    NORMAL,
    HIGH,
    HIGHEST,

    /**
     * This priority is used for monitoring data in events, and not for modifying them.
     */
    MONITOR
}