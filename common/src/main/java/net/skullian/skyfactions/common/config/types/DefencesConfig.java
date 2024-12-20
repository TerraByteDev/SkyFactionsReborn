package net.skullian.skyfactions.common.config.types;

import dev.dejvokep.boostedyaml.YamlDocument;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public enum DefencesConfig {

    MAX_PLAYER_DEFENCES("Limits.MAX_PLAYER_DEFENCES"),
    MAX_FACTION_DEFENCES("Limits.MAX_FACTION_DEFENCES"),
    ALLOW_PLACEMENTS_IN_OTHER_WORLDS("Limits.ALLOW_PLACEMENTS_IN_OTHER_WORLDS"),
    ALLOWED_PLACEMENT_WORLDS("Limits.ALLOWED_WORLDS"),

    GLOBAL_PASSIVE_ENTITIES("Global.ENTITIES.PASSIVE_ENTITIES"),
    GLOBAL_HOSTILE_ENTITIES("Global.ENTITIES.HOSTILE_ENTITIES"),
    GLOBAL_ENTITIES_ENTITY_LIST("Global.ENTITIES.ENTITY_LIST"),

    PERMISSION_PURCHASE_DEFENCE("Permissions.PURCHASE_DEFENCE"),
    PERMISSION_PLACE_DEFENCE("Permissions.PLACE_DEFENCE"),
    PERMISSION_ACCESS_DEFENCE("Permissions.ACCESS_DEFENCE"),
    PERMISSION_REPLENISH_AMMO("Permissions.REPLENISH_AMMO"),
    PERMISSION_UPGRADE_DEFENCE("Permissions.UPGRADE_DEFENCE"),
    PERMISSION_REMOVE_DEFENCE("Permissions.REMOVE_DEFENCE"),
    PERMISSION_TOGGLE_ENTITY_TARGETING("Permissions.TOGGLE_ENTITY_TARGETING");

    @Setter
    private static YamlDocument config;
    private final String path;

    DefencesConfig(String path) {
        this.path = path;
    }

    public List<String> getList() {
        return config.getStringList(this.path);
    }

    public List<Integer> getIntegerList() {
        return config.getIntList(this.path);
    }

    public String getString() {
        return config.getString(this.path);
    }

    public int getInt() {
        return config.getInt(this.path);
    }

    public boolean getBoolean() {
        return config.getBoolean(this.path);
    }
}
