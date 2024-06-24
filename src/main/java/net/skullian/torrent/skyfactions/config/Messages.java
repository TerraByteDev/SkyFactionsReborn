package net.skullian.torrent.skyfactions.config;

import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public enum Messages {

    SERVER_NAME("SERVER_NAME"),
    PERMISSION_DENY("PERMISSION_DENY"),
    RELOADING("RELOADING"),
    RELOADED("RELOADED"),
    COOLDOWN("COOLDOWN"),
    ERROR("ERROR"),
    INCORRECT_USAGE("INCORRECT_USAGE"),
    UNKNOWN_PLAYER("UNKNOWN_PLAYER"),

    COMMAND_HEAD("Commands.COMMAND_HEAD"),
    COMMAND_INFO("Commands.COMMAND_INFO"),
    NO_COMMANDS_FOUND("Commands.NO_COMMANDS_FOUND"),

    ISLAND_CREATION_DENY("Islands.ISLAND_CREATION_DENY"),
    ISLAND_CREATING("Islands.CREATING_MESSAGE"),
    ISLAND_CREATED("Islands.CREATED_MESSAGE"),
    NO_ISLAND("Islands.NO_ISLAND"),
    DELETION_CONFIRM("Islands.CONFIRM_DELETION"),
    ISLAND_DELETION_SUCCESS("Islands.DELETION_SUCCESS"),
    DELETION_BLOCK("Islands.DELETION_BLOCK"),
    DELETION_PROCESSING("Islands.DELETION_PROCESSING"),

    DISCORD_LINK_SUCCESS("Discord.LINK_SUCCESS_MESSAGE"),
    DISCORD_ALREADY_LINKED("Discord.ALREADY_LINKED"),
    DISCORD_NOT_LINKED("Discord.NOT_LINKED"),
    DISCORD_LINK_PROMPT("Discord.LINK_PROMPT"),
    DISCORD_UNLINK_SUCCESS("Discord.UNLINK_SUCCESS"),

    RAID_CONFIRMATION_NAME("Raiding.RAID_CONFIRMATION_NAME"),
    RAID_CANCEL_NAME("Raiding.RAID_CANCEL_NAME"),
    RAID_ON_COOLDOWN("Raiding.RAID_ON_COOLDOWN"),
    RAID_INSUFFICIENT_GEMS("Raiding.RAID_INSUFFICIENT_GEMS"),
    RAID_NO_PLAYERS("Raiding.NO_PLAYERS"),
    RAID_PROCESSING("Raiding.RAID_PROCESSING"),

    GEMS_COUNT_MESSAGE("Gems.GEM_COUNT"),
    GEM_ADD_SUCCESS("Gems.GEM_ADD_SUCCESS"),
    INSUFFICIENT_GEMS_COUNT("Gems.INSUFFICIENT_GEM_COUNT"),

    TRUST_SUCCESS("Islands.TRUST_SUCCESS"),
    UNTRUST_SUCCESS("Islands.UNTRUST_SUCCESS"),
    UNTRUST_FAILURE("Islands.UNTRUST_FAILURE"),
    PLAYER_NOT_TRUSTED("Islands.VISIT_NOT_TRUSTED"),
    VISIT_NO_ISLAND("Islands.VISIT_NO_ISLAND");

    private static FileConfiguration config;
    private final String path;

    Messages(String path) { this.path = path; }

    public static void setConfig(FileConfiguration conf) { config = conf; }

    public String get(Object... replacements) {
        Object value = config.get("Messages." + this.path);

        String message;
        if (value == null) {
            message = TextUtility.color(SERVER_NAME.get() + "&r&7 Message not found: " + this.path);
        } else {
            message = value instanceof List ? TextUtility.fromList((List<?>) value) : value.toString();
        }
        return TextUtility.color(replace(message, replacements));
    }

    public void send(CommandSender receiver, Object... replacements) {
        Object value = config.get("Messages." + this.path);

        String message;
        if (value == null) {
            message = TextUtility.color(SERVER_NAME.get() + "&r&7 Message not found: " + this.path);
        } else {
            message = value instanceof List ? TextUtility.fromList((List<?>) value) : value.toString();
        }

        if (!message.isEmpty()) {
            receiver.sendMessage(TextUtility.color(replace(message, replacements)));
        }
    }

    private String replace(String message, Object... replacements) {
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 >= replacements.length) break;
            message = message.replace(String.valueOf(replacements[i]), String.valueOf(replacements[i + 1]));
        }

        String prefix = config.getString("Messages." + SERVER_NAME.getPath());
        return message.replace("%server_name%", prefix != null && !prefix.isEmpty() ? prefix : "");
    }

    public String getPath() { return this.path; }
}
