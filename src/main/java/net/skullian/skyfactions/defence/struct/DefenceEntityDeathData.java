package net.skullian.skyfactions.defence.struct;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@AllArgsConstructor
@Getter
@Setter
public class DefenceEntityDeathData {

    private String DEATH_MESSAGE;
    private String OWNER;
    private Location DEFENCE_LOCATION;

}
