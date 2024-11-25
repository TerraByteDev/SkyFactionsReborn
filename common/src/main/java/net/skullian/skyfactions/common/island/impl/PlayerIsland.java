package net.skullian.skyfactions.common.island.impl;

import net.skullian.skyfactions.core.config.types.Settings;
import net.skullian.skyfactions.core.island.SkyIsland;

public class PlayerIsland extends SkyIsland {

    public PlayerIsland(int id) {
        super(
                id,
                Settings.GEN_PLAYER_REGION_SIZE.getInt(),
                Settings.GEN_PLAYER_REGION_PADDING.getInt(),
                Settings.GEN_PLAYER_GRID_ORIGIN.getIntegerList()
        );
    }
}
