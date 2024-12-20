package net.skullian.skyfactions.common.defence.struct;

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

    private String EXPLOSION_DAMAGE_PERCENT;

    private int HOSTILE_MOBS_TARGET_LEVEL;
    private int PASSIVE_MOBS_TARGET_LEVEL;

}
