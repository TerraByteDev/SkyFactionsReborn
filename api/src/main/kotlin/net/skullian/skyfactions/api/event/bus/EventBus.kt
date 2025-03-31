package net.skullian.skyfactions.api.event.bus

import net.skullian.skyfactions.api.event.Event
import net.skullian.skyfactions.api.event.Listener

/**
 * The event bus for SkyFactions.
 * This will handle all [Event]s, and will be where you register your listeners.
 */
interface EventBus {

    /**
     * Registers a listener to the event bus.
     */
    fun subscribe(listener: Listener)

    /**
     * Unregisters a listener from the event bus.
     */
    fun unsubscribe(listener: Listener)

    /**
     * Emit an [Event] - registered listeners will subsequently be called.
     */
    fun emit(event: Event)

}