package net.skullian.skyfactions.common.util.nms;

import lombok.Getter;
import lombok.Setter;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.common.util.worldborder.AWorldBorder;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public abstract class NMSProvider {

    @Getter
    @Setter
    private String NMS_VERSION;

    private final String NMS_CLASSPATH = "net.skullian.skyfactions.nms.%s.NMSHandlerImpl";
    private final NMSHandler NMS_HANDLER;

    public NMSProvider() {
        NMSHandler nmsHandler;

        try {
            String ver = fetchNMSVersion();
            String path = NMS_CLASSPATH.formatted(ver);

            Class<?> nmsClass = Class.forName(path);
            MethodHandles.Lookup handleLookup = MethodHandles.lookup();
            MethodType methodType = MethodType.methodType(Void.TYPE);
            final MethodHandle handle = handleLookup.findConstructor(nmsClass, methodType);
            nmsHandler = (NMSHandler) handle.invoke();
        } catch (Throwable error) {
            SLogger.setup("Failed to load NMSHandler!", true);
            error.printStackTrace();
            nmsHandler = null;
        }

        NMS_HANDLER = nmsHandler;
    }

    public abstract AWorldBorder getBorderFromPlayer(SkyUser player);

    public abstract AWorldBorder getBorderFromWorld(String worldName);

    public NMSHandler getInstance() {
        return this.NMS_HANDLER;
    }

    public abstract String fetchNMSVersion();

}
