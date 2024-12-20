package net.skullian.skyfactions.paper.api;

import net.skullian.skyfactions.common.api.FileAPI;
import net.skullian.skyfactions.paper.SkyFactionsReborn;

public class SpigotFileAPI extends FileAPI {
    @Override
    public String getConfigFolderPath() {
        return SkyFactionsReborn.getInstance().getDataFolder().getPath();
    }
}
