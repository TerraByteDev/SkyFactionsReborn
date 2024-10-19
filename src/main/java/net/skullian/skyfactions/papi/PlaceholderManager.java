package net.skullian.skyfactions.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.GemsAPI;
import net.skullian.skyfactions.api.RunesAPI;
import net.skullian.skyfactions.util.text.TextUtility;
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
        if (params.equalsIgnoreCase("player_runes")) {
            if (player == null) return "-1";
            if (RunesAPI.playerRunes.containsKey(player.getUniqueId())) return String.valueOf(RunesAPI.playerRunes.get(player.getUniqueId()));
                else RunesAPI.getRunes(player.getUniqueId());

            return "-1";
        } else if (params.equalsIgnoreCase("player_gems")) {
            if (player == null) return "-1";
            if (GemsAPI.playerGems.containsKey(player.getUniqueId())) return String.valueOf(GemsAPI.playerGems.get(player.getUniqueId()));
                else GemsAPI.getGems(player.getUniqueId());

            return "-1";
        } else if (params.equalsIgnoreCase("player_faction")) {
            if (player == null || !player.hasPlayedBefore()) return "UNKNOWN PLAYER";
            if (FactionAPI.factionCache.containsKey(player.getUniqueId())) return String.valueOf(FactionAPI.factionCache.get(player.getUniqueId()).getName());
                else return TextUtility.color("&cN/A");

        } else if (params.equalsIgnoreCase("faction_runes")) {
            if (player == null || !player.hasPlayedBefore()) return "UNKNOWN PLAYER";
            if (FactionAPI.factionCache.containsKey(player.getUniqueId())) return String.valueOf(FactionAPI.factionCache.get(player.getUniqueId()).getRunes());
                else return TextUtility.color("&cN/A");

        } else if (params.toLowerCase().startsWith("faction_runes_")) {
            String factionName = params.toLowerCase().replace("faction_runes_", "");
            if (FactionAPI.factionNameCache.containsKey(factionName)) return String.valueOf(FactionAPI.factionNameCache.get(factionName).getRunes());
                else FactionAPI.getFaction(factionName);

            return TextUtility.color("&eLoading...");
        } else if (params.equalsIgnoreCase("faction_gems")) {
            if (player == null) return "UNKNOWN PLAYER";
            if (FactionAPI.factionCache.containsKey(player.getUniqueId())) return String.valueOf(FactionAPI.factionCache.get(player.getUniqueId()).getGems());
                else return TextUtility.color("&cN/A");
        } else if (params.toLowerCase().startsWith("faction_gems_")) {
            String factionName = params.toLowerCase().replace("faction_gems_", "");
            if (FactionAPI.factionNameCache.containsKey(factionName)) return String.valueOf(FactionAPI.factionNameCache.get(factionName).getGems());
                else FactionAPI.getFaction(factionName);

            return TextUtility.color("&eLoading...");
        } else if (params.equalsIgnoreCase("faction_rank")) {
            if (player == null) return "UNKNOWN PLAYER";
            if (FactionAPI.factionCache.containsKey(player.getUniqueId())) return TextUtility.color(FactionAPI.factionCache.get(player.getUniqueId()).getRank(player.getUniqueId()));
                else return TextUtility.color("&cN/A");
        }
        return null;
    }
}
