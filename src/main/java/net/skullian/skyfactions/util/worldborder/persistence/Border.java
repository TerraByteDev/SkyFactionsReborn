package net.skullian.skyfactions.util.worldborder.persistence;

import net.skullian.skyfactions.api.WorldBorderAPI;
import net.skullian.skyfactions.util.worldborder.WorldBorder;

public class Border extends WorldBorderAPI {

    public Border() {
        super(WorldBorder::new, WorldBorder::new);
    }
}
