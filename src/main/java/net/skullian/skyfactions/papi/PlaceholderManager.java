package net.skullian.skyfactions.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.GemsAPI;
import net.skullian.skyfactions.api.RunesAPI;
import net.skullian.skyfactions.event.PlayerHandler;
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
        String locale = PlayerHandler.getLocale(player.getUniqueId());
        if (params.equalsIgnoreCase("player_runes")) {
            if (player == null) return "-1";
            if (RunesAPI.playerRunes.containsKey(player.getUniqueId())) return String.valueOf(RunesAPI.getRunes(player.getUniqueId()));
                else RunesAPI.cachePlayer(player.getUniqueId());

            return "-1";
        } else if (params.equalsIgnoreCase("player_gems")) {
            if (player == null) return "-1";
            if (GemsAPI.playerGems.containsKey(player.getUniqueId())) return String.valueOf(GemsAPI.getGems(player.getUniqueId()));
                else GemsAPI.cachePlayer(player.getUniqueId());

            return "-1";
        } else if (params.equalsIgnoreCase("player_faction")) {
            if (player == null || !player.hasPlayedBefore()) return "UNKNOWN PLAYER";
            if (FactionAPI.factionCache.containsKey(player.getUniqueId())) return String.valueOf(FactionAPI.factionCache.get(player.getUniqueId()).getName());
                else return TextUtility.legacyColor("&cN/A", locale, player);

        } else if (params.equalsIgnoreCase("faction_runes")) {
            if (player == null || !player.hasPlayedBefore()) return "UNKNOWN PLAYER";
            if (FactionAPI.factionCache.containsKey(player.getUniqueId())) return String.valueOf(FactionAPI.factionCache.get(player.getUniqueId()).getRunes());
                else return TextUtility.legacyColor("&cN/A", locale, player);

        } else if (params.toLowerCase().startsWith("faction_runes_")) {
            String factionName = params.toLowerCase().replace("faction_runes_", "");
            if (FactionAPI.factionNameCache.containsKey(factionName)) return String.valueOf(FactionAPI.factionNameCache.get(factionName).getRunes());
                else FactionAPI.getFaction(factionName);

            return TextUtility.legacyColor("&eLoading...", locale, player);
        } else if (params.equalsIgnoreCase("faction_gems")) {
            if (player == null) return "UNKNOWN PLAYER";
            if (FactionAPI.factionCache.containsKey(player.getUniqueId())) return String.valueOf(FactionAPI.factionCache.get(player.getUniqueId()).getGems());
                else return TextUtility.legacyColor("&cN/A", locale, player);
        } else if (params.toLowerCase().startsWith("faction_gems_")) {
            String factionName = params.toLowerCase().replace("faction_gems_", "");
            if (FactionAPI.factionNameCache.containsKey(factionName)) return String.valueOf(FactionAPI.factionNameCache.get(factionName).getGems());
                else FactionAPI.getFaction(factionName);

            return TextUtility.legacyColor("&eLoading...", locale, player);
        } else if (params.equalsIgnoreCase("faction_rank")) {
            if (player == null) return "UNKNOWN PLAYER";
            if (FactionAPI.factionCache.containsKey(player.getUniqueId())) return LegacyComponentSerializer.legacySection().serialize(FactionAPI.factionCache.get(player.getUniqueId()).getRank(player.getUniqueId()));
                else return TextUtility.legacyColor("&cN/A", locale, player);
        }
        return null;
    }
}
