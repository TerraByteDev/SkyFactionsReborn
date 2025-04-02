package net.skullian.skyfactions.common.libraries.flavor.binder

import net.skullian.skyfactions.common.libraries.flavor.FlavorBinder

/**
 * @author GrowlyX
 * @since 9/6/2022
 */
@Suppress("FunctionParameterNaming")
abstract class FlavorBinderContainer
{
    internal val binders = mutableListOf<FlavorBinder<*>>()

    /**
     * Populates the container with flavor binders.
     */
    abstract fun populate()

    /**
     * Binds the specified object to a multi-type flavor binder.
     *
     * @param `object` the object to bind
     * @return a multi-type flavor binder
     */
    fun bind(`object`: Any) = FlavorBinderMultiType(this, `object`)
}