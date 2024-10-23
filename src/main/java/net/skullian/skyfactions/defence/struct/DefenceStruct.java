package net.skullian.skyfactions.defence.struct;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Setter
@Getter
public class DefenceStruct {

    private String FILE_NAME;
    private String NAME;
    private String TYPE;
    private String IDENTIFIER;

    private int BUY_COST;
    private int SELL_COST;
    private String AMMO_COST;
    private String REPAIR_STEP;
    private String REPAIR_COST;

    private int MAX_LEVEL;

    private String PLACE_SOUND;
    private int PLACE_PITCH;
    private String BREAK_SOUND;
    private int BREAK_PITCH;
    private String ACTIVATE_SOUND;
    private int ACTIVATE_PITCH;

    private List<DefenceEffectStruct> EFFECTS;

    private List<String> DEATH_MESSAGES;
    private List<String> EFFECT_MESSAGES;

    private DefenceAttributeStruct ATTRIBUTES;

    // Mob Name / Expression
    private Map<String, String> EXPERIENCE_DROPS;

    private String PROJECTILE;

    private String BLOCK_MATERIAL;
    private String BLOCK_SKULL;

    private String ITEM_MATERIAL;
    private String ITEM_SKULL;
    private List<String> ITEM_LORE;
    private List<String> UPGRADE_LORE;

    private String PLACEMENT_BLOCKED_MESSAGE;
    private boolean IS_WHITELIST;
    private List<String> PLACEMENT_LIST;

    private List<String> HOLOGRAM_LIST;
    private String OUT_OF_STOCK_HOLOGRAM;
    private boolean APPEND_TO_TOP;

    private DefenceEntityStruct ENTITY_CONFIG;
}
