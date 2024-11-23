package net.skullian.skyfactions.util.nms;

import lombok.Getter;
import net.skullian.skyfactions.util.worldborder.AWorldBorder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NMSProvider {

    private static final Pattern COMPILED = Pattern.compile("(?i)\\(MC: (\\d)\\.(\\d++)\\.?(\\d++)?(?: (Pre-Release|Release Candidate) )?(\\d)?\\)");
    private static final String PACKAGE_PATTERN = "v1_%s_R%s";
    @Getter private static String NMS_VERSION;

    private static final String NMS_CLASSPATH = "net.skullian.skyfactions.nms.%s.NMSHandlerImpl";
    @Getter private static final NMSHandler NMS_HANDLER;

    static {
        NMSHandler nmsHandler;

        try {
            String ver = getNMSVersion();
            String path = NMS_CLASSPATH.formatted(ver);

            Class<?> nmsClass = Class.forName(path);
            MethodHandles.Lookup handleLookup = MethodHandles.lookup();
            MethodType methodType = MethodType.methodType(Void.TYPE);
            final MethodHandle handle = handleLookup.findConstructor(nmsClass, methodType);
            nmsHandler = (NMSHandler) handle.invoke();
        } catch (Throwable error) {
            error.printStackTrace();
            nmsHandler = null;
        }

        NMS_HANDLER = nmsHandler;
    }

    public static AWorldBorder getBorder(Player player) {
        try {
            Class<?> clazz = Class.forName("net.skullian.skyfactions.nms." + NMS_VERSION + ".WorldBorder");
            return (AWorldBorder) clazz.getDeclaredConstructor().newInstance(player);
        } catch (Throwable error) {
            error.printStackTrace();
        }

        return null;
    }

    public static AWorldBorder getBorder(World world) {
        try {
            Class<?> clazz = Class.forName("net.skullian.skyfactions.nms." + NMS_VERSION + ".WorldBorder");
            return (AWorldBorder) clazz.getDeclaredConstructor().newInstance(world);
        } catch (Throwable error) {
            error.printStackTrace();
        }

        return null;
    }

    private AWorldBorder getNMSWorldBorder() {
        String version = NMSProvider.getNMS_VERSION();

        try {
            Class<?> clazz = Class.forName("net.skullian.skyfactions.nms." + version + ".WorldBorder");
            return (AWorldBorder) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unsupported NMS version: " + version, e);
        }
    }

    public static void init() {
        // nothing here, only to initialise the static block
    }

    private static String getNMSVersion() {
        String internalVer = Bukkit.getVersion();
        Matcher matcher = COMPILED.matcher(internalVer);

        if (matcher.find()) {
            MatchResult result = matcher.toMatchResult();
            String ver = result.group(2);
            String minorVer = result.group(3);
            NMS_VERSION = PACKAGE_PATTERN.formatted(ver, minorVer);

            return NMS_VERSION;
        } else {
            throw new UnsupportedOperationException("This server version is unsupported!");
        }
    }


}
