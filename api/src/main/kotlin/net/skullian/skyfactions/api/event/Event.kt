package net.skullian.skyfactions.api.event

/**
 * The superclass of all events, implemented only [Cancellable] (for now).
 * New events will either implement this class or a subclass (e.g. [Cancellable])
 */
interface Event {}