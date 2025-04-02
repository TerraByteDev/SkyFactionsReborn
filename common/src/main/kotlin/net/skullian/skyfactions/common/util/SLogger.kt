package net.skullian.skyfactions.common.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.skullian.skyfactions.api.SkyApi.Companion.getInstance

/**
 * The logger class.
 * Yeah, you're welcome.
 */
object SLogger {

    private var PREFIX = "<gray>[<reset><gradient:#0083FF:#00FFC7><bold>SkyFactions</gradient><reset><gray>]<reset>"
    private var DEBUG_PREFIX = "<gray>[<reset><bold>SkyFactions|DEBUG<reset><gray>]<reset>"

    /**
     * Print a warn log to console.
     */
    @JvmStatic
    fun info(message: Any?, vararg args: Any?) {
        val infoLog: Component = MiniMessage.miniMessage()
            .deserialize(PREFIX + " <#4294ed>" + format(message, args) + "<reset>")

        getInstance().getConsoleAudience().sendMessage(infoLog)
    }

    /**
     * Print a warn log to console.
     */
    @JvmStatic
    fun warn(message: Any?, vararg args: Any?) {
        val warnLog = MiniMessage.miniMessage()
            .deserialize(PREFIX + " <#f28f24>" + format(message, args) + "<reset>")
        getInstance().getConsoleAudience().sendMessage(warnLog)
    }

    /**
     * Print a fatal (error) log to console.
     */
    @JvmStatic
    fun fatal(message: Any?, vararg args: Any?) {
        val fatalLog = MiniMessage.miniMessage()
            .deserialize(PREFIX + " <#e73f38>" + format(message, args) + "<reset>")
        getInstance().getConsoleAudience().sendMessage(fatalLog)
    }

    /**
     * Print a debug log to console.
     * This will only successfully send if the console option is enabled.
     */
    @JvmStatic
    fun debug(message: Any?, vararg args: Any?) {
        // todo debug check

        val debugLog = MiniMessage.miniMessage()
            .deserialize(DEBUG_PREFIX + " <gray>" + format(message, args) + "<reset>")
        getInstance().getConsoleAudience().sendMessage(debugLog)
    }

    private fun format(message: Any?, vararg args: Any): String? {
        if (message == null) {
            return null
        }

        var formattedMessage = message.toString()
        for (arg in args) {
            formattedMessage = formattedMessage.replaceFirst("\\{}".toRegex(), arg.toString())
        }
        return formattedMessage
    }
}