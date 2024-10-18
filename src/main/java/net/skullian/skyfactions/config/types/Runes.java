package net.skullian.skyfactions.config.types;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.Getter;
import lombok.Setter;

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
    private static YamlDocument config;
    private final String path;

    Runes(String path) {
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

    public Map<String, Integer> getMap() {
        Map<String, Integer> map = new HashMap<>();
        if (config.isSection(this.path)) {
            Section section = config.getSection(this.path);

            for (String key : section.getRoutesAsStrings(false)) {
                int count = section.getInt(key);
                map.put(key, count);
            }
        }


        return map;
    }
}
