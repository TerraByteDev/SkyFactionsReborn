package net.skullian.skyfactions.common.event.bus

import net.skullian.skyfactions.api.event.Event
import net.skullian.skyfactions.api.event.EventPriority
import net.skullian.skyfactions.api.event.Listener
import net.skullian.skyfactions.api.event.bus.EventBus
import net.skullian.skyfactions.api.event.bus.Subscribe
import net.skullian.skyfactions.common.util.SLogger
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.concurrent.CopyOnWriteArrayList

/**
 * The implementation of the [EventBus] interface.
 * We only implement this in the common module, as the event bus should be accessible from the API module specifically.
 * The API module itself shouldn't contain any unnecessary code.
 */
class EventBusImpl : EventBus {

    private val listeners: MutableList<Listener> = CopyOnWriteArrayList()

    override fun subscribe(listener: Listener) {
        var subscribeAnnotations = 0

        for (method in listeners.javaClass.methods) {
            if (method.isAnnotationPresent(Subscribe::class.java)) subscribeAnnotations++
        }

        require(subscribeAnnotations > 0) { "${listener.javaClass.name} attempted to register a listener, but no public methods were found with the @Subscribe annotation." }
        SLogger.info("Register Listener: ${listener.javaClass.name} with $subscribeAnnotations methods.")

        listeners.add(listener)
    }

    override fun unsubscribe(listener: Listener) {
        SLogger.info("Unregistered Listener: ${listener.javaClass.name}")
        listeners.remove(listener)
    }

    @Suppress("LoopWithTooManyJumpStatements", "NestedBlockDepth")
    override fun emit(event: Event) {
        for (priority in EventPriority.entries) {
            for (listener in listeners) {
                for (method in listener.javaClass.methods) {
                    if (method.parameters.size != 1) continue
                    if (!method.parameters[0].type.isAssignableFrom(event.javaClass)) continue

                    val annotation: Subscribe = method.getAnnotation(Subscribe::class.java) ?: return
                    if (annotation.priority != priority) continue

                    invoke(method, listener, event)
                }
            }
        }
    }

    private fun invoke(method: Method, listener: Listener, vararg args: Any) {
        method.isAccessible = true

        try {
            method.invoke(
                listener,
                if (method.parameterCount == 0) null else args
            )
        } catch (e: InvocationTargetException) {
            SLogger.fatal("Failed to access method ${method.name} on listener: ${listener.javaClass.name} despite previous attempts, {}", e)
        } catch (e: IllegalAccessException) {
            SLogger.debug("Failed to invoke method: ${method.name} on listener: ${listener.javaClass.name} -  + ${e.cause}")
            SLogger.fatal(e)
        }
    }

}