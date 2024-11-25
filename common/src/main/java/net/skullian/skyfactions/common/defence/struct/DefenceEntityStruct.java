package net.skullian.skyfactions.common.defence.struct;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class DefenceEntityStruct {

    private boolean OVERRIDE_GLOBAL;

    private boolean ALLOW_HOSTILE_TARGETING;
    private boolean ALLOW_TOGGLE_HOSTILE_TARGETING;
    private boolean TARGET_HOSTILE_DEFAULT;

    private boolean ALLOW_PASSIVE_TARGETING;
    private boolean ALLOW_TOGGLE_PASSIVE_TARGETING;
    private boolean TARGET_PASSIVE_DEFAULT;

    private boolean ATTACK_PLAYERS;

    private boolean IS_WHITELIST;
    private List<String> PASSIVE_LIST;
    private List<String> HOSTILE_LIST;
    private List<String> ENTITY_LIST;
}
