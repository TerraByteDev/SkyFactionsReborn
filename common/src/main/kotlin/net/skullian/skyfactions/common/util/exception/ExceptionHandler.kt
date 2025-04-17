package net.skullian.skyfactions.common.util.exception

import io.sentry.Sentry
import net.skullian.skyfactions.common.config.Config

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

    @JvmStatic
    fun handle(exception: Exception, errorCode: String) {

    }

    private fun logSentry(exception: Exception) {
        Sentry.captureException(exception)
    }
}