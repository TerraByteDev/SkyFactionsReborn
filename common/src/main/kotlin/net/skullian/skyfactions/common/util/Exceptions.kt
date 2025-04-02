package net.skullian.skyfactions.common.util

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