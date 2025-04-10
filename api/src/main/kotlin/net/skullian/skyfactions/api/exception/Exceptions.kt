package net.skullian.skyfactions.api.exception

/**
 * An exception thrown during loading.
 */
open class LoadException: Exception("An unexpected error occurred while loading SkyFactionsReborn.")

/**
 * An exception thrown during service loading.
 */
open class FlavorException(cause: Throwable) : RuntimeException() {
    init {
        initCause(cause)
    }
}

/**
 * An exception thrown during hook initialisation.
 */
class HookException(cause: Throwable) : LoadException() {
    init {
        initCause(cause)
    }
}