package net.skullian.skyfactions.island.impl;

import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.island.SkyIsland;
import org.bukkit.Location;
import org.bukkit.World;

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
