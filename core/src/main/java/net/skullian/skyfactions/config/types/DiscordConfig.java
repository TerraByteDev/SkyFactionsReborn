package net.skullian.skyfactions.config.types;

import dev.dejvokep.boostedyaml.YamlDocument;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public enum DiscordConfig {

    ENABLED("Discord.ENABLED"),
    TOKEN("Discord.TOKEN,"),
    RAID_CHANNEL("Discord.RAID_CHANNEL"),
    AVATAR_API("Discord.AVATAR_API");

    @Setter
    private static YamlDocument config;
    private final String path;

    DiscordConfig(String path) {
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
