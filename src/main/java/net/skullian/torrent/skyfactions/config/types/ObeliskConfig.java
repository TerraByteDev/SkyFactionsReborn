package net.skullian.torrent.skyfactions.config.types;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

@Getter
public enum ObeliskConfig {

    OBELISK_MATERIAL("Block.MATERIAL"),
    OBELISK_SPAWN_OFFSET("Block.SPAWN_OFFSET"),
    OBELISK_CUSTOM_MODEL_DATA("Block.CUSTOM_MODEL_DATA");

    @Setter
    private static FileConfiguration config;
    private final String path;

    ObeliskConfig(String path) {
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
