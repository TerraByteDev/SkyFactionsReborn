package net.skullian.torrent.skyfactions.defence.struct;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

@AllArgsConstructor
@Setter
@Getter
public class DefenceStruct {

    private String NAME;
    private String TYPE;

    private String BUY_COST;
    private String SELL_COST;
    private String AMMO_COST;

    private int MAX_LEVEL;

    private String PLACE_SOUND;
    private int PLACE_PITCH;
    private String BREAK_SOUND;
    private int BREAK_PITCH;
    private String ACTIVATE_SOUND;
    private int ACTIVATE_PITCH;

    private ConfigurationSection EFFECTS;

    private List<String> MESSAGES;

    private ConfigurationSection ATTRIBUTES;

    private String PROJECTILE;
    private String PARTICLE;

    private String BLOCK_MATERIAL;
    private String BLOCK_SKULL;

    private String ITEM_MATERIAL;
    private String ITEM_SKULL;
    private List<String> ITEM_LORE;
    private List<String> UPGRADE_LORE;
}
