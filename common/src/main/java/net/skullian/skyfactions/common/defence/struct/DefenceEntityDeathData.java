package net.skullian.skyfactions.common.defence.struct;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.skullian.skyfactions.common.util.SkyLocation;

@AllArgsConstructor
@Getter
@Setter
public class DefenceEntityDeathData {

    private String DEATH_MESSAGE;
    private String OWNER;
    private SkyLocation DEFENCE_LOCATION;

}
