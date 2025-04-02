package net.skullian.skyfactions.api.event.bus

import net.skullian.skyfactions.api.event.EventPriority
import net.skullian.skyfactions.api.event.Listener

/**
 * An annotation used in [Listener] classes to mark methods that should be called when an event is fired.
 * @property ignoreCancelled Whether the method should be triggered if a previous listener has cancelled the event. Defaults to false.
 * @property priority The priority of the event, determined by [EventPriority]. This determines the order that event listeners receive the event. Defaults to [EventPriority.NORMAL]
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Subscribe(

    /**
     * Whether the event should be ignored if it is cancelled.
     * If this is set to true, the listener method will not be called.
     */
    val ignoreCancelled: Boolean = false,

    /**
     * The priority of the event, determined by [EventPriority]
     * This determines the order that event listeners receive the event.
     *
     * [EventPriority.MONITOR] should be used for monitoring data in events, and not for modifying them.
     */
    val priority: EventPriority = EventPriority.NORMAL

)