package net.skullian.skyfactions.common.island.impl;

import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.island.SkyIsland;

public class FactionIsland extends SkyIsland {
    public FactionIsland(int id) {
        super(
                id,
                Settings.GEN_FACTION_REGION_SIZE.getInt(),
                Settings.GEN_FACTION_REGION_PADDING.getInt(),
                Settings.GEN_FACTION_GRID_ORIGIN.getIntegerList()
        );
    }
}
