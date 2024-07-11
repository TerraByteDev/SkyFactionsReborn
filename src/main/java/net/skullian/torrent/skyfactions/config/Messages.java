package net.skullian.torrent.skyfactions.config;

import lombok.Getter;
import lombok.Setter;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

@Getter
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
    VISIT_NO_ISLAND("Islands.VISIT_NO_ISLAND"),
    PLAYER_ALREADY_TRUSTED("Islands.ALREADY_TRUSTED"),
    VISIT_PROCESSING("Islands.VISIT_PROCESSING"),
    VISIT_IN_RAID("Islands.VISIT_IN_RAID"),

    FACTION_CREATION_PROCESSING("Factions.FACTION_CREATION.FACTION_CREATE_PROCESSING"),
    FACTION_ACTION_DENY("Factions.ACTION_DENY"),
    FACTION_NAME_LENGTH_LIMIT("Factions.FACTION_CREATION.NAME_LENGTH_LIMIT"),
    FACTION_NO_NUMBERS("Factions.FACTION_CREATION.NAME_NO_NUMBERS"),
    FACTION_NON_ENGLISH("Factions.FACTION_CREATION.NAME_NON_ENGLISH"),
    FACTION_NO_SYMBOLS("Factions.FACTION_CREATION.NAME_NO_SYMBOLS"),
    FACTION_NAME_PROHIBITED("Factions.FACTION_CREATION.NAME_PROHIBITED"),
    FACTION_INSUFFICIENT_FUNDS("Factions.FACTION_CREATION.INSUFFICIENT_FUNDS"),
    FACTION_CREATION_SUCCESS("Factions.FACTION_CREATION.FACTION_CREATE_SUCCESS"),

    FACTION_LEAVE_SUCCESS("Factions.FACTION_LEAVE.FACTION_LEAVE_SUCCESS"),
    FACTION_OWNER_LEAVE_DENY("Factions.FACTION_LEAVE.FACTION_OWNER_LEAVE_DENY"),

    FACTION_BROADCAST_MODEL("Factions.FACTION_BROADCAST.BROADCAST_MODEL"),

    FACTION_OWNER_TITLE("Factions.FACTION_TITLES.OWNER"),
    FACTION_ADMIN_TITLE("Factions.FACTION_TITLES.ADMIN"),
    FACTION_MODERATOR_TITLE("Factions.FACTION_TITLES.MODERATOR"),
    FACTION_FIGHTER_TITLE("Factions.FACTION_TITLES.FIGHTER"),
    FACTION_MEMBER_TITLE("Factions.FACTION_TITLES.MEMBER"),

    FACTION_MANAGE_SELF_DENY("Factions.FACTION_MANAGE.MANAGE_SELF_DENY"),
    FACTION_MANAGE_SELF_DENY_LORE("Factios.FACTION_MANAGE.MANAGE_SELF_DENY_LORE"),
    FACTION_MANAGE_KICK_SUCCESS("Factions.FACTION_MANAGE.KICK_SUCCESS"),
    FACTION_MANAGE_KICK_BROADCAST("Factions.FACTION_MANAGE.KICK_BROADCAST"),
    FACTION_MANAGE_BAN_SUCCESS("Factions.FACTION_MANAGE.BAN_SUCCESS"),
    FACTION_MANAGE_BAN_BROADCAST("Factions.FACTION_MANAGE.BAN_BROADCAST"),

    ALREADY_IN_FACTION("Factions.ALREADY_IN_FACTION"),
    NOT_IN_FACTION("Factions.NOT_IN_FACTION"),
    FACTION_NOT_FOUND("Factions.FACTION_NOT_FOUND"),

    FACTION_INFO_LIST("Factions.FACTION_INFO.INFORMATION_MESSAGE"),
    MOTD_CHANGE_PROCESSING("Factions.CHANGE_MOTD.MOTD_PROCESSING"),
    MOTD_CHANGE_SUCCESS("Factions.CHANGE_MOTD.MOTD_CHANGE_SUCCESS"),

    OBELISK_ACCESS_DENY("Obelisk.ACCESS_DENY"),
    OBELISK_DESTROY_DENY("Obelisk.DESTROY_DENY"),
    OBELISK_GUI_DENY("Obelisk.OBELISK_GUI_DENY"),
    OBELISK_ITEM_NAME("Obelisk.OBELISK_BLOCK.ITEM_NAME"),
    OBELISK_ITEM_LORE("Obelisk.OBELISK_BLOCK.ITEM_LORE"),

    RUNE_ENCHANT_DENY("Runes.ENCHANTS_DENY"),
    RUNE_GENERAL_DENY("Runes.GENERAL_DENY"),
    RUNE_CONVERSION_SUCCESS("Runes.CONVERSION_SUCCESS"),
    RUNE_INSUFFICIENT_ITEMS("Runes.INSUFFICIENT_ITEMS"),

    AUDIT_FACTION_CREATE_TITLE("Audit_Logs.FACTION_CREATE_TITLE"),
    AUDIT_FACTION_CREATE_DESCRIPTION("Audit_Logs.FACTION_CREATE_DESCRIPTION"),
    AUDIT_FACTION_JOIN_TITLE("Audit_Logs.JOIN_TITLE"),
    AUDIT_FACTION_JOIN_DESCRIPTION("Audit_Logs.JOIN_DESCRIPTION"),
    AUDIT_FACTION_LEAVE_TITLE("Audit_Logs.LEAVE_TITLE"),
    AUDIT_FACTION_LEAVE_DESCRIPTION("Audit_Logs.LEAVE_DESCRIPTION"),
    AUDIT_FACTION_MOTD_TITLE("Audit_Logs.MOTD_TITLE"),
    AUDIT_FACTION_MOTD_DESCRIPTION("Audit_Logs.MOTD_DESCRIPTION"),
    AUDIT_FACTION_TIMESTAMP_FORMAT("Audit_Logs.AUDIT_TIMESTAMP"),
    AUDIT_FACTION_BROADCAST_TITLE("Audit_Logs.BROADCAST_TITLE"),
    AUDIT_FACTION_BROADCAST_DESCRIPTION("Audit_Logs.BROADCAST_DESCRIPTION"),
    AUDIT_FACTION_KICK_TITLE("Audit_Logs.KICK_TITLE"),
    AUDIT_FACTION_KICK_DESCRIPTION("Audit_Logs.KICK_DESCRIPTION"),
    AUDIT_FACTION_BAN_TITLE("Audit_Logs.BAN_TITLE"),
    AUDIT_FACTION_BAN_DESCRIPTION("Audit_Logs.BAN_DESCRIPTION");

    @Setter
    private static FileConfiguration config;
    private final String path;

    Messages(String path) { this.path = path; }

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

    public String getString() {
        return config.getString("Messages." + this.path);
    }

    public void send(CommandSender receiver, Object... replacements) {
        if (receiver == null) return;

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

}
