package net.skullian.skyfactions.config.types;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

@Getter
public enum DefencesConfig {

    MAX_PLAYER_DEFENCES("Limits.MAX_PLAYER_DEFENCES"),
    MAX_FACTION_DEFENCES("Limits.MAX_FACTION_DEFENCES"),
    ALLOW_PLACEMENTS_IN_OTHER_WORLDS("Limits.ALLOW_PLACEMENTS_IN_OTHER_WORLDS"),
    ALLOWED_PLACEMENT_WORLDS("Limits.ALLOWED_WORLDS"),

    GLOBAL_PASSIVE_ENTITIES("Global.ENTITIES.PASSIVE_ENTITIES"),
    GLOBAL_HOSTILE_ENTITIES("Global.ENTITIES.HOSTILE_ENTITIES"),
    GLOBAL_ENTITIES_ENTITY_LIST("Global.ENTITIES.ENTITY_LIST");

    @Setter
    private static FileConfiguration config;
    private final String path;

    DefencesConfig(String path) {
        this.path = path;
    }

    public List<String> getList() {
        return config.getStringList(this.path);
    }

    public List<Integer> getIntegerList() {
        return config.getIntegerList(this.path);
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
