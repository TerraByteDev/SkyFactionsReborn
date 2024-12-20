package net.skullian.skyfactions.paper.api;

import net.skullian.skyfactions.common.api.PluginInfoAPI;
import net.skullian.skyfactions.paper.SkyFactionsReborn;

public class SpigotPluginInfoAPI extends PluginInfoAPI {
    @Override
    public String getVersion() {
        return SkyFactionsReborn.getInstance().getDescription().getVersion();
    }

    @Override
    public String getAuthors() {
        return SkyFactionsReborn.getInstance().getDescription().getAuthors().toString();
    }

    @Override
    public String getWebsite() {
        return SkyFactionsReborn.getInstance().getDescription().getWebsite();
    }

    @Override
    public String getContributors() {
        return SkyFactionsReborn.getInstance().getDescription().getContributors().toString();
    }
}
