package net.skullian.skyfactions.common.util.exception

import io.sentry.Sentry
import net.skullian.skyfactions.api.model.user.OnlineSkyUser
import net.skullian.skyfactions.common.config.Config
import net.skullian.skyfactions.common.util.SLogger
import java.lang.Exception

/**
 * A simple utility class that handles exceptions.
 */
object ExceptionHandler {

    private val sentryEnabled: Boolean = Config.i().global.sentry.dsnUrl.isNotEmpty()

    init {
        if (sentryEnabled) {
            Sentry.init { options ->
                options.dsn = Config.i().global.sentry.dsnUrl
                options.environment = Config.i().global.sentry.environment
                options.serverName = Config.i().global.sentry.serverName
            }
        }
    }

    /**
     * Handles an exception by logging it and optionally sending it to Sentry.
     *
     * @param exception The exception to handle.
     * @param during A description of the operation during which the exception occurred.
     * @param errorCode A unique error code for identifying the issue.
     * @param player The online player in question - This can be nullable.
     */
    fun handleError(exception: Exception, during: String, errorCode: String, player: OnlineSkyUser? = null) {
        handleSentry(exception)
        SLogger.fatal("An error occurred while $during [Error Code: $errorCode] - $exception $player")
    }

    /**
     * Sends the exception to Sentry if Sentry is enabled.
     *
     * @param exception The exception to send.
     */
    private fun handleSentry(exception: Exception) {
        if (sentryEnabled) {
            Sentry.captureException(exception)
        }
    }


}