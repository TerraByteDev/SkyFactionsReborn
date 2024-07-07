package net.skullian.torrent.skyfactions.faction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.island.FactionIsland;
import org.bukkit.OfflinePlayer;

import java.util.List;

@AllArgsConstructor
@Getter
public class Faction {

    private FactionIsland island;
    private String name;
    private int last_raid;
    private int level;

    /**
        Get the Faction's island.

        @return {@link FactionIsland}
    **/
    public FactionIsland getIsland() {
        return SkyFactionsReborn.db.getFactionIsland(name).join();
    }

    /**
        Update the name of the faction.

        @param newName New name of the faction.
     **/
    public void updateName(String newName) {
        SkyFactionsReborn.db.updateFactionName(newName, name).join();
    }

    /**
     * Get the owner of the faction.
     *
     * @return {@link OfflinePlayer}
     */
    public OfflinePlayer getOwner() {
        return SkyFactionsReborn.db.getFactionOwner(name).join();
    }

    /**
     * Get the admins of the faction.
     *
     * @return {@link List<OfflinePlayer>}
     */
    public List<OfflinePlayer> getAdmins() {
        return SkyFactionsReborn.db.getMembersByRank(name, "admin").join();
    }

    /**
     * Change the rank of a player.
     *
     * @param player Player in question.
     * @param newRank {@link RankType} New Rank of the player.
     */
    public void modifyPlayerRank(OfflinePlayer player, RankType newRank) {
        SkyFactionsReborn.db.updateMemberRank(name, player.getPlayer(), newRank.getRankValue()).join();
    }

    /**
     * Get the moderators of the faction.
     *
     * @return {@link List<OfflinePlayer>}
     */
    public List<OfflinePlayer> getModerators() {
        return SkyFactionsReborn.db.getMembersByRank(name, "moderator").join();
    }

    /**
     * Get the fighters of the faction.
     *
     * @return {@link List<OfflinePlayer>}
     */
    public List<OfflinePlayer> getFighters() {
        return SkyFactionsReborn.db.getMembersByRank(name, "fighter").join();
    }

    /**
     * Get the members of the faction. Does not include moderators & owner.
     *
     * @return {@link List<OfflinePlayer>}
     */
    public List<OfflinePlayer> getMembers() {
        return SkyFactionsReborn.db.getMembersByRank(name, "member").join();
    }

    /**
     * Get the total member count of the faction. Used for the Obelisk overview UI.
     *
     * @return {@link Integer}
     */
    public int getTotalMemberCount() {
        int total = getMembers().size() + getModerators().size() + 1;
        return total;
    }

    /**
     * Get the Faction's MOTD.
     *
     * @return {@link String}
     */
    public String getMOTD() {
        return SkyFactionsReborn.db.getMOTD(name).join();
    }

    /**
     * Set the Faction's MOTD.
     *
     * @param MOTD New MOTD.
     * @return {@link String}
     */
    public void updateMOTD(String MOTD) {
        SkyFactionsReborn.db.setMOTD(name, MOTD).join();
    }

    /**
     * Get the Faction's rune count.
     *
     * @return {@link Integer}
     */
    public int getRunes() { return SkyFactionsReborn.db.getRunes(getName()).join(); }

    /**
     * Add to the Faction's rune count.
     *
     * @param addition Amount of runes to add.
     */
    public void addRunes(int addition) { SkyFactionsReborn.db.addRunes(name, addition).join(); }

    /**
     * Get the Faction's gem count.
     *
     * @return {@link Integer}
     */
    public int getGems() { return SkyFactionsReborn.db.getGems(name).join(); }

    /**
     * Add gems to the Faction's gem balance.
     *
     * @param addition Gems to add.
     */
    public void addGems(int addition) { SkyFactionsReborn.db.addGems(name, addition).join(); }
}
