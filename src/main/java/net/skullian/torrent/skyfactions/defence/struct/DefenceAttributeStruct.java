package net.skullian.torrent.skyfactions.defence.struct;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DefenceAttributeStruct {

    private String RANGE;
    private String COOLDOWN;
    private String MAX_TARGETS;
    private String MAX_AMMO;
    private String UPGRADE_COST;
    private String DAMAGE;
    private String DISTANCE;
    private String HEALING;

}
