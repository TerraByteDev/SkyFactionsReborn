package net.skullian.skyfactions.common.config.types;

import dev.dejvokep.boostedyaml.YamlDocument;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public enum DefencesConfig {

    THREAD_LIMIT("limits.thread-limit"),
    MAX_PLAYER_DEFENCES("limits.max-player-defences"),
    MAX_FACTION_DEFENCES("limits.max-faction-defences"),
    ALLOW_PLACEMENTS_IN_OTHER_WORLDS("limits.allow-placements-in-other-worlds"),
    ALLOWED_PLACEMENT_WORLDS("limits.allowed-worlds"),

    GLOBAL_PASSIVE_ENTITIES("global.entities.passive-entities"),
    GLOBAL_HOSTILE_ENTITIES("global.entities.hostile-entities"),
    GLOBAL_ENTITIES_ENTITY_LIST("global.entities.entity-list"),

    PERMISSION_PURCHASE_DEFENCE("permissions.purchase-defence"),
    PERMISSION_PLACE_DEFENCE("permissions.place-defence"),
    PERMISSION_ACCESS_DEFENCE("permissions.access-defence"),
    PERMISSION_REPLENISH_AMMO("permissions.replenish-ammo"),
    PERMISSION_UPGRADE_DEFENCE("permissions.upgrade-defence"),
    PERMISSION_REMOVE_DEFENCE("permissions.remove-defence"),
    PERMISSION_TOGGLE_ENTITY_TARGETING("permissions.toggle-entity-targeting");

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
