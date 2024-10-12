package net.skullian.skyfactions.util.text;

import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataUtil {


    public static boolean supports(int version) {
        return Data.VERSION >= version;
    }

    private static final class Data {
        private static final int VERSION;

        static {
            String version = Bukkit.getVersion();
            Matcher matcher = Pattern.compile("MC: \\d\\.(\\d+)").matcher(version);

            if (matcher.find()) VERSION = Integer.parseInt(matcher.group(1));
            else throw new IllegalArgumentException("Failed to parse server version from: " + version);
        }

        private static final boolean ISFLAT = supports(13);
    }

    public static int getVersion() {
        return Data.VERSION;
    }
}