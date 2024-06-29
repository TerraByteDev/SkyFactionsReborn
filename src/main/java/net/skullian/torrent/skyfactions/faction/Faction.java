package net.skullian.torrent.skyfactions.faction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.island.FactionIsland;

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
        SkyFactionsReborn.db.updateFactionName(newName, name);
    }
}
