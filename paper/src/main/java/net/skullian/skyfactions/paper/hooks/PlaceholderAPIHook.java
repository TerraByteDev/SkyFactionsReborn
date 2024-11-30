package net.skullian.skyfactions.paper.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.paper.api.SpigotFactionAPI;
import net.skullian.skyfactions.paper.api.SpigotGemsAPI;
import net.skullian.skyfactions.paper.api.SpigotRunesAPI;
import net.skullian.skyfactions.paper.api.SpigotPlayerAPI;
import net.skullian.skyfactions.common.util.text.TextUtility;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final SkyFactionsReborn plugin;

    public PlaceholderAPIHook(SkyFactionsReborn plugin) {
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
        String locale = SpigotPlayerAPI.getLocale(player.getUniqueId());
        if (params.equalsIgnoreCase("player_runes")) {
            if (player == null) return "-1";
            if (SpigotRunesAPI.playerRunes.containsKey(player.getUniqueId()))
                return String.valueOf(SpigotRunesAPI.getRunesIfCached(player.getUniqueId()));
            else SpigotRunesAPI.cachePlayer(player.getUniqueId());

            return "-1";
        } else if (params.equalsIgnoreCase("player_gems")) {
            if (player == null) return "-1";
            if (SpigotGemsAPI.playerGems.containsKey(player.getUniqueId()))
                return String.valueOf(SpigotGemsAPI.getGemsIfCached(player.getUniqueId()));
            else SpigotGemsAPI.cachePlayer(player.getUniqueId());

            return "-1";
        } else if (params.equalsIgnoreCase("player_faction")) {
            if (player == null || !player.hasPlayedBefore()) return "UNKNOWN PLAYER";
            if (SpigotFactionAPI.factionCache.containsKey(player.getUniqueId()))
                return String.valueOf(SpigotFactionAPI.getCachedFaction(player.getUniqueId()).getName());
            else return TextUtility.legacyColor("&cN/A", locale, player);

        } else if (params.equalsIgnoreCase("faction_runes")) {
            if (player == null || !player.hasPlayedBefore()) return "UNKNOWN PLAYER";
            if (SpigotFactionAPI.factionCache.containsKey(player.getUniqueId()))
                return String.valueOf(SpigotFactionAPI.getCachedFaction(player.getUniqueId()).getRunes());
            else return TextUtility.legacyColor("&cN/A", locale, player);

        } else if (params.toLowerCase().startsWith("faction_runes_")) {
            String factionName = params.toLowerCase().replace("faction_runes_", "");
            if (SpigotFactionAPI.factionNameCache.containsKey(factionName))
                return String.valueOf(SpigotFactionAPI.factionNameCache.get(factionName).getRunes());
            else SpigotFactionAPI.getFaction(factionName);

            return TextUtility.legacyColor("&eLoading...", locale, player);
        } else if (params.equalsIgnoreCase("faction_gems")) {
            if (player == null) return "UNKNOWN PLAYER";
            if (SpigotFactionAPI.factionCache.containsKey(player.getUniqueId()))
                return String.valueOf(SpigotFactionAPI.getCachedFaction(player.getUniqueId()).getGems());

            else return TextUtility.legacyColor("&cN/A", locale, player);
        } else if (params.toLowerCase().startsWith("faction_gems_")) {
            String factionName = params.toLowerCase().replace("faction_gems_", "");
            if (SpigotFactionAPI.factionNameCache.containsKey(factionName))
                return String.valueOf(SpigotFactionAPI.factionNameCache.get(factionName).getGems());
            else SpigotFactionAPI.getFaction(factionName);

            return TextUtility.legacyColor("&eLoading...", locale, player);
        } else if (params.equalsIgnoreCase("faction_rank")) {
            if (player == null) return "UNKNOWN PLAYER";
            if (SpigotFactionAPI.factionCache.containsKey(player.getUniqueId()))
                return TextUtility.legacyColor(SpigotFactionAPI.getCachedFaction(player.getUniqueId()).getRank(player.getUniqueId()), SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), player);
            else return TextUtility.legacyColor("&cN/A", locale, player);
        }
        return null;
    }
}
