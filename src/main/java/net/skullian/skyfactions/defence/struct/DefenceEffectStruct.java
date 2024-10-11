package net.skullian.skyfactions.defence.struct;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DefenceEffectStruct {

    private String EFFECT;
    private int DEFENCE_LEVEL;
    private int EFFECT_LEVEL;
    private int DURATION;

}
