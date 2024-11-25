package net.skullian.skyfactions.core.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.skullian.skyfactions.core.SkyFactionsReborn;
import net.skullian.skyfactions.core.api.FactionAPI;
import net.skullian.skyfactions.core.api.GemsAPI;
import net.skullian.skyfactions.core.api.SpigotRunesAPI;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
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
            if (GemsAPI.playerGems.containsKey(player.getUniqueId()))
                return String.valueOf(GemsAPI.getGemsIfCached(player.getUniqueId()));
            else GemsAPI.cachePlayer(player.getUniqueId());

            return "-1";
        } else if (params.equalsIgnoreCase("player_faction")) {
            if (player == null || !player.hasPlayedBefore()) return "UNKNOWN PLAYER";
            if (FactionAPI.factionCache.containsKey(player.getUniqueId()))
                return String.valueOf(FactionAPI.getCachedFaction(player.getUniqueId()).getName());
            else return TextUtility.legacyColor("&cN/A", locale, player);

        } else if (params.equalsIgnoreCase("faction_runes")) {
            if (player == null || !player.hasPlayedBefore()) return "UNKNOWN PLAYER";
            if (FactionAPI.factionCache.containsKey(player.getUniqueId()))
                return String.valueOf(FactionAPI.getCachedFaction(player.getUniqueId()).getRunes());
            else return TextUtility.legacyColor("&cN/A", locale, player);

        } else if (params.toLowerCase().startsWith("faction_runes_")) {
            String factionName = params.toLowerCase().replace("faction_runes_", "");
            if (FactionAPI.factionNameCache.containsKey(factionName))
                return String.valueOf(FactionAPI.factionNameCache.get(factionName).getRunes());
            else FactionAPI.getFaction(factionName);

            return TextUtility.legacyColor("&eLoading...", locale, player);
        } else if (params.equalsIgnoreCase("faction_gems")) {
            if (player == null) return "UNKNOWN PLAYER";
            if (FactionAPI.factionCache.containsKey(player.getUniqueId()))
                return String.valueOf(FactionAPI.getCachedFaction(player.getUniqueId()).getGems());

            else return TextUtility.legacyColor("&cN/A", locale, player);
        } else if (params.toLowerCase().startsWith("faction_gems_")) {
            String factionName = params.toLowerCase().replace("faction_gems_", "");
            if (FactionAPI.factionNameCache.containsKey(factionName))
                return String.valueOf(FactionAPI.factionNameCache.get(factionName).getGems());
            else FactionAPI.getFaction(factionName);

            return TextUtility.legacyColor("&eLoading...", locale, player);
        } else if (params.equalsIgnoreCase("faction_rank")) {
            if (player == null) return "UNKNOWN PLAYER";
            if (FactionAPI.factionCache.containsKey(player.getUniqueId()))
                return TextUtility.legacyColor(FactionAPI.getCachedFaction(player.getUniqueId()).getRank(player.getUniqueId()), SpigotPlayerAPI.getLocale(player.getUniqueId()), player);
            else return TextUtility.legacyColor("&cN/A", locale, player);
        }
        return null;
    }
}
