package net.skullian.torrent.skyfactions.defence.struct;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DefenceData {

    private int LEVEL;
    private String TYPE;
    private int AMMO;
    private Location LOCATION;
    private String UUIDFactionName; // either uuid or faction name
    private boolean IS_FACTION;

}
