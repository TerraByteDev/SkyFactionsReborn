package net.skullian.skyfactions.api.event

/**
 * This would be implemented alongside [Event] to allow for the event to be cancelled.
 */
interface Cancellable {

    /**
     * @return `true` if the event is cancelled, `false` otherwise.
     */
    fun isCancelled(): Boolean

    /**
     * Modify the cancellation state of the event.
     */
    fun setCancelled(cancelled: Boolean)

}