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
     * @return {@link OfflinePlayer}
     */
    public OfflinePlayer getOwner() {
        return SkyFactionsReborn.db.getFactionOwner(name).join();
    }

    /**
     * Get the moderators of the faction.
     * @return {@link List<OfflinePlayer>}
     */
    public List<OfflinePlayer> getFactionModerators() {
        return SkyFactionsReborn.db.getModerators(name).join();
    }

    /**
     * Get the members of the faction. Does not include moderators & owner.
     * @return {@link List<OfflinePlayer>}
     */
    public List<OfflinePlayer> getMembers() {
        return SkyFactionsReborn.db.getMembers(name).join();
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
}
