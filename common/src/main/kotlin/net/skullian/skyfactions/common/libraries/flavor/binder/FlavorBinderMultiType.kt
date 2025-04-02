package net.skullian.skyfactions.common.libraries.flavor.binder

import net.skullian.skyfactions.common.libraries.flavor.FlavorBinder
import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 9/6/2022
 */
class FlavorBinderMultiType(
    private val container: FlavorBinderContainer,
    private val instance: Any
)
{
    val types = mutableListOf<KClass<*>>()
    private var binderInternalPopulator = { _: FlavorBinder<*> -> }

    /**
     * Adds the specified type to the list of types to bind.
     *
     * @param T the type to add
     * @return the current [FlavorBinderMultiType] instance
     */
    inline fun <reified T> to(): FlavorBinderMultiType
    {
        types += T::class
        return this
    }

    /**
     * Adds the specified type to the list of types to bind.
     *
     * @param kClass the class of the type to add
     * @return the current [FlavorBinderMultiType] instance
     */
    fun to(kClass: KClass<*>): FlavorBinderMultiType
    {
        types += kClass
        return this
    }

    /**
     * Sets the internal populator function for the binder.
     *
     * @param populator the populator function
     * @return the current [FlavorBinderMultiType] instance
     */
    fun populate(populator: FlavorBinder<*>.() -> Unit): FlavorBinderMultiType
    {
        binderInternalPopulator = populator
        return this
    }

    /**
     * Binds the instance to the specified types.
     */
    fun bind()
    {
        for (type in types)
        {
            container.binders += FlavorBinder(type)
                .apply(binderInternalPopulator) to instance
        }
    }
}