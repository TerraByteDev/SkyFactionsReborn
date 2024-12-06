package net.skullian.skyfactions.common.util.worldborder.persistence;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.api.WorldBorderAPI;

public class Border extends WorldBorderAPI {
    public Border() {
        super(SkyApi.getInstance().getNMSProvider()::getBorderFromPlayer, SkyApi.getInstance().getNMSProvider()::getBorderFromWorld); // Adjust constructor as needed
    }
}
