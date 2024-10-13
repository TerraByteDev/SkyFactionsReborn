package net.skullian.skyfactions.defence.struct;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DefenceData {

    private int LEVEL;
    private String TYPE;
    private int AMMO;
    private String WORLD_LOC;
    private int X;
    private int Y;
    private int Z;
    private String UUIDFactionName; // either uuid or faction name
    private boolean IS_FACTION;

}
