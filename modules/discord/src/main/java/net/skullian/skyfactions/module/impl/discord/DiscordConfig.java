package net.skullian.skyfactions.module.impl.discord;

import dev.dejvokep.boostedyaml.YamlDocument;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public enum DiscordConfig {

    ENABLED("Discord.ENABLED"),
    TOKEN("Discord.TOKEN,"),
    RAID_CHANNEL("Discord.RAID_CHANNEL"),
    AVATAR_API("Discord.AVATAR_API"),

    COMMAND_NAME("Discord.COMMANDS.COMMAND_NAME"),
    COMMAND_DESCRIPTION("Discord.COMMANDS.COMMAND_DESCRIPTION"),
    COMMAND_OPTION_NAME("Discord.COMMANDS.CODE_OPTION_NAME"),
    COMMAND_OPTION_DESCRIPTION("Discord.COMMANDS.CODE_OPTION_DESCRIPTION"),

    SUCCESS_COLOR("Discord.SUCCESS_COLOR"),
    ERROR_COLOR("Discord.ERROR_COLOR"),

    PRESENCE_INTERVAL("Discord.PRESENCE_INTERVAL");

    @Setter
    @Getter
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
