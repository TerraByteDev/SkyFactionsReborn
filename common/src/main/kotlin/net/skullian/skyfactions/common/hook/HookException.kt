package net.skullian.skyfactions.common.hook

import net.skullian.skyfactions.common.util.LoadException

/**
 * An exception thrown during hook initialisation.
 */
class HookException(cause: Throwable) : LoadException() {
    init {
        initCause(cause)
    }
}