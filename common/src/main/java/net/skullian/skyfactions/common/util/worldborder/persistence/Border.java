package net.skullian.skyfactions.common.util.worldborder.persistence;

import net.skullian.skyfactions.common.api.WorldBorderAPI;
import net.skullian.skyfactions.common.util.nms.NMSProvider;

public class Border extends WorldBorderAPI {
    public Border() {
        super(NMSProvider::getBorder, NMSProvider::getBorder); // Adjust constructor as needed
    }
}
