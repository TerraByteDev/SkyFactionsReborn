package net.skullian.skyfactions.util.worldborder.persistence;

import net.skullian.skyfactions.api.WorldBorderAPI;
import net.skullian.skyfactions.util.nms.NMSProvider;

public class Border extends WorldBorderAPI {
    public Border() {
        super(player -> NMSProvider.getBorder(), world -> NMSProvider.getBorder()); // Adjust constructor as needed
    }
}
