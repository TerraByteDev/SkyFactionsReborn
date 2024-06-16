package net.skullian.torrent.skyfactions.util.text;

import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataUtil {


    public static boolean supports(int version) {
        return Data.VERSION >= version;
    }

    private static final class Data {
        /**
         * The current version of the server in the form of a major version.
         * If the static initialization for this fails, you know something's wrong with the server software.
         *
         * @since 1.0.0
         */
        private static final int VERSION;

        static { // This needs to be right below VERSION because of initialization order.
            String version = Bukkit.getVersion();
            Matcher matcher = Pattern.compile("MC: \\d\\.(\\d+)").matcher(version);

            if (matcher.find()) VERSION = Integer.parseInt(matcher.group(1));
            else throw new IllegalArgumentException("Failed to parse server version from: " + version);
        }

        /**
         * Cached result if the server version is after the v1.13 flattening update.
         *
         * @since 3.0.0
         */
        private static final boolean ISFLAT = supports(13);
    }

    /**
     * The current version of the server.
     *
     * @return the current server version minor number.
     * @see #supports(int)
     * @since 2.0.0
     */
    public static int getVersion() {
        return Data.VERSION;
    }
}