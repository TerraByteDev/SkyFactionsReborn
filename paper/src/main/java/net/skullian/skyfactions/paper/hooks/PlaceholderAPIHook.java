package net.skullian.skyfactions.paper.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.paper.SkyFactionsReborn;
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
        SkyUser user = SkyApi.getInstance().getUserManager().getUser(player.getUniqueId());

        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());
        if (params.equalsIgnoreCase("player_runes")) {
            if (player == null) return "-1";
            if (!user.runes.isEmpty()) return String.valueOf(user.runes.get());
                else user.getRunes();

            return "-1";
        } else if (params.equalsIgnoreCase("player_gems")) {
            if (player == null) return "-1";
            if (!user.gems.isEmpty()) return String.valueOf(user.gems.get());
                else user.getGems();

            return "-1";
        } else if (params.equalsIgnoreCase("player_faction")) {
            if (player == null || !player.hasPlayedBefore()) return "N/A";
            if (SkyApi.getInstance().getFactionAPI().getFactionUserCache().containsKey(player.getUniqueId()))
                return String.valueOf(SkyApi.getInstance().getFactionAPI().getCachedFaction(player.getUniqueId()).getName());
            else return TextUtility.legacyColor("&cN/A", locale, user);

        } else if (params.equalsIgnoreCase("faction_runes")) {
            if (player == null || !player.hasPlayedBefore()) return "N/A";
            if (SkyApi.getInstance().getFactionAPI().getFactionUserCache().containsKey(player.getUniqueId()))
                return String.valueOf(SkyApi.getInstance().getFactionAPI().getCachedFaction(player.getUniqueId()).getRunes());
            else return TextUtility.legacyColor("&cN/A", locale, user);

        } else if (params.toLowerCase().startsWith("faction_runes_")) {
            String factionName = params.toLowerCase().replace("faction_runes_", "");
            if (SkyApi.getInstance().getFactionAPI().getFactionCache().containsKey(factionName))
                return String.valueOf(SkyApi.getInstance().getFactionAPI().getCachedFaction(factionName).getRunes());
            else SkyApi.getInstance().getFactionAPI().getFaction(factionName);

            return TextUtility.legacyColor("&eLoading...", locale, user);
        } else if (params.equalsIgnoreCase("faction_gems")) {
            if (player == null) return "UNKNOWN PLAYER";
            if (SkyApi.getInstance().getFactionAPI().getFactionUserCache().containsKey(player.getUniqueId()))
                return String.valueOf(SkyApi.getInstance().getFactionAPI().getCachedFaction(player.getUniqueId()).getGems());

            else return TextUtility.legacyColor("&cN/A", locale, user);
        } else if (params.toLowerCase().startsWith("faction_gems_")) {
            String factionName = params.toLowerCase().replace("faction_gems_", "");
            if (SkyApi.getInstance().getFactionAPI().getFactionCache().containsKey(factionName))
                return String.valueOf(SkyApi.getInstance().getFactionAPI().getCachedFaction(factionName).getGems());
            else SkyApi.getInstance().getFactionAPI().getFaction(factionName);

            return TextUtility.legacyColor("&eLoading...", locale, user);
        } else if (params.equalsIgnoreCase("faction_rank")) {
            if (player == null) return "UNKNOWN PLAYER";
            if (SkyApi.getInstance().getFactionAPI().getFactionUserCache().containsKey(player.getUniqueId()))
                return TextUtility.legacyColor(SkyApi.getInstance().getFactionAPI().getCachedFaction(player.getUniqueId()).getRank(player.getUniqueId()), locale, user);

            else return TextUtility.legacyColor("&cN/A", locale, user);
        }
        return null;
    }
}
