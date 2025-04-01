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
    var binderInternalPopulator = { _: FlavorBinder<*> -> }

    inline fun <reified T> to(): FlavorBinderMultiType
    {
        types += T::class
        return this
    }

    fun to(kClass: KClass<*>): FlavorBinderMultiType
    {
        types += kClass
        return this
    }

    fun populate(populator: FlavorBinder<*>.() -> Unit): FlavorBinderMultiType
    {
        binderInternalPopulator = populator
        return this
    }

    fun bind()
    {
        for (type in types)
        {
            container.binders += FlavorBinder(type)
                .apply(binderInternalPopulator) to instance
        }
    }
}