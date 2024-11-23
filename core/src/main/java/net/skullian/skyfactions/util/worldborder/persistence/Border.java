package net.skullian.skyfactions.util.worldborder.persistence;

import net.skullian.skyfactions.api.WorldBorderAPI;
import net.skullian.skyfactions.util.nms.NMSProvider;

public class Border extends WorldBorderAPI {
    public Border() {
        super(NMSProvider::getBorder, NMSProvider::getBorder); // Adjust constructor as needed
    }
}
