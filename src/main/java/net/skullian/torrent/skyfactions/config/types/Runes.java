package net.skullian.torrent.skyfactions.config.types;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public enum Runes {

    BASE_FOR_EACH("RUNES.FOR_EACH"),
    BASE_RUNE_RETURN("RUNES.GIVE"),

    ALLOW_LORE("FILTERS.ALLOW_LORE"),
    ALLOW_ENCHANTS("FILTERS.ALLOW_ENCHANTS"),
    MATERIALS_IS_BLACKLIST("FILTERS.MATERIALS.IS_BLACKLIST"),
    MATERIALS_LIST("FILTERS.MATERIALS.LIST"),

    RUNE_OVERRIDES("OVERRIDES");

    @Setter
    private static FileConfiguration config;
    private final String path;

    Runes(String path) {
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

    public Map<String, Integer> getMap() {
        Map<String, Integer> map = new HashMap<>();
        if (config.isSet(this.path)) {
            ConfigurationSection section = config.getConfigurationSection(this.path);

            for (String key : section.getKeys(false)) {
                int count = section.getInt(key);
                map.put(key, count);
            }
        }


        return map;
    }
}
