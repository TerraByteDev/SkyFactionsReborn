package net.skullian.skyfactions.common.util.worldborder.persistence;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.api.WorldBorderAPI;
import net.skullian.skyfactions.common.util.nms.NMSProvider;

public class Border extends WorldBorderAPI {
    public Border() {
        super(SkyApi.getInstance().getNMSProvider()::getBorderFromPlayer, SkyApi.getInstance().getNMSProvider()::getBorderFromWorld); // Adjust constructor as needed
    }
}
