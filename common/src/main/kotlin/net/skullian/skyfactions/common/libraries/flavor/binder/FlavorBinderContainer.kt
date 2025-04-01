package net.skullian.skyfactions.common.libraries.flavor.binder

import net.skullian.skyfactions.common.libraries.flavor.FlavorBinder

/**
 * @author GrowlyX
 * @since 9/6/2022
 */
abstract class FlavorBinderContainer
{
    internal val binders = mutableListOf<FlavorBinder<*>>()

    abstract fun populate()

    fun bind(`object`: Any) = FlavorBinderMultiType(this, `object`)
}