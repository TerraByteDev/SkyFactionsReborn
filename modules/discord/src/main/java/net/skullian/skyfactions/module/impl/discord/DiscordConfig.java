package net.skullian.skyfactions.module.impl.discord;

import dev.dejvokep.boostedyaml.YamlDocument;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public enum DiscordConfig {

    ENABLED("discord.enabled"),
    TOKEN("discord.token"),
    RAID_CHANNEL("discord.raid_channel"),
    AVATAR_API("discord.avatar_api"),

    COMMAND_NAME("discord.commands.command_name"),
    COMMAND_DESCRIPTION("discord.commands.command_description"),
    COMMAND_OPTION_NAME("discord.commands.code_option_name"),
    COMMAND_OPTION_DESCRIPTION("discord.commands.code_option_description"),

    SUCCESS_COLOR("discord.success_color"),
    ERROR_COLOR("discord.error_color"),

    PRESENCE_INTERVAL("discord.presence_interval");

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
