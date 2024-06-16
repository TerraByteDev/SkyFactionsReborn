package net.skullian.torrent.skyfactions.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderManager extends PlaceholderExpansion {

    private final SkyFactionsReborn plugin;

    public PlaceholderManager(SkyFactionsReborn plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "skyfactions";
    }

    @Override
    @NotNull
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("testplaceholder")) {
            return "TEST";
        }

        return null;
    }
}
