package net.skullian.skyfactions.config.types;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.defence.DefencesFactory;
import net.skullian.skyfactions.util.DependencyHandler;
import net.skullian.skyfactions.util.SLogger;
import net.skullian.skyfactions.util.text.TextUtility;

public enum Messages {

    LANGUAGE_NAME("LANGUAGE_NAME"),

    SERVER_NAME("SERVER_NAME"),
    PERMISSION_DENY("PERMISSION_DENY"),
    RELOADING("RELOADING"),
    RELOADED("RELOADED"),
    COOLDOWN("COOLDOWN"),
    ERROR("ERROR"),
    INCORRECT_USAGE("INCORRECT_USAGE"),
    UNKNOWN_PLAYER("UNKNOWN_PLAYER"),
    PLEASE_WAIT("PLEASE_WAIT"),
    SYNC_SUCCESS("SYNC_SUCCESS"),

    COMMAND_HEAD("Commands.COMMAND_HEAD"),
    COMMAND_INFO("Commands.COMMAND_INFO"),
    NO_COMMANDS_FOUND("Commands.NO_COMMANDS_FOUND"),

    ISLAND_CREATION_DENY("Islands.ISLAND_CREATION_DENY"),
    ISLAND_CREATING("Islands.CREATING_MESSAGE"),
    ISLAND_CREATED("Islands.CREATED_MESSAGE"),
    NO_ISLAND("Islands.NO_ISLAND"),
    PLAYER_HAS_NO_ISLAND("Islands.PLAYER_HAS_NO_ISLAND"),
    DELETION_CONFIRM("Islands.CONFIRM_DELETION"),
    ISLAND_DELETION_SUCCESS("Islands.DELETION_SUCCESS"),
    DELETION_BLOCK("Islands.DELETION_BLOCK"),
    DELETION_PROCESSING("Islands.DELETION_PROCESSING"),
    NETHER_PORTALS_BLOCKED("Islands.NETHER_PORTALS_BLOCKED"),
    ALREADY_ON_ISLAND("Islands.ALREADY_ON_ISLAND"),
    VISIT_ALREADY_ON_ISLAND("Islands.VISIT_ALREADY_ON_ISLAND"),

    DISCORD_RAID_MESSAGE("Discord.DISCORD_RAID_MESSAGE"),
    DISCORD_LINK_SUCCESS("Discord.LINK_SUCCESS_MESSAGE"),
    DISCORD_ALREADY_LINKED("Discord.ALREADY_LINKED"),
    DISCORD_NOT_LINKED("Discord.NOT_LINKED"),
    DISCORD_LINK_PROMPT("Discord.LINK_PROMPT"),
    DISCORD_UNLINK_SUCCESS("Discord.UNLINK_SUCCESS"),
    DISCORD_APP_LINK_SUCCESS("Discord.DISCORD_LINK_SUCCESS_MESSAGE"),
    DISCORD_APP_LINK_FAILED("Discord.LINK_FAILED"),

    NPC_ACCESS_DENY("NPC.NPC_ACCESS_DENY"),
    NPC_RELOADING("NPC.NPC_RELOADING"),
    NPC_RELOADED("NPC.NPC_RELOADED"),
    NPC_DISABLING("NPC.NPC_DISABLING"),
    NPC_DISABLED("NPC.NPC_DISABLED"),
    NPC_PLAYER_ISLANDS_ACTIONS("NPC.ACTIONS.PLAYER_ISLANDS"),
    NPC_FACTION_ISLANDS_ACTIONS("NPC.ACTIONS.FACTION_ISLANDS"),

    RAID_CONFIRMATION_NAME("Raiding.RAID_CONFIRMATION_NAME"),
    RAID_CANCEL_NAME("Raiding.RAID_CANCEL_NAME"),
    RAID_ON_COOLDOWN("Raiding.RAID_ON_COOLDOWN"),
    RAID_INSUFFICIENT_GEMS("Raiding.RAID_INSUFFICIENT_GEMS"),
    RAIDING_COUNTDOWN_SUBTITLE("Raiding.COUNTDOWN_SUBTITLE"),
    RAID_NO_PLAYERS("Raiding.NO_PLAYERS"),
    RAID_PROCESSING("Raiding.RAID_PROCESSING"),
    RAID_IMMINENT_MESSAGE("Raiding.RAIDED_NOTIFICATION"),

    DEFENCE_INSUFFICIENT_RUNES_LORE("Defences.INSUFFICIENT_RUNES_LORE"),
    DEFENCE_INSUFFICIENT_INVENTORY_LORE("Defences.INSUFFICIENT_INVENTORY_LORE"),
    DEFENCE_INSUFFICIENT_PERMISSIONS_LORE("Defences.INSUFFICIENT_PERMISSIONS_LORE"),
    DEFENCE_INSUFFICIENT_PERMISSIONS("Defences.DEFENCE_INSUFFICIENT_PERMISSIONS"),
    DEFENCE_PURCHASE_SUCCESS("Defences.DEFENCE_PURCHASE_SUCCESS"),
    DEFENCE_DESTROY_DENY("Defences.DEFENCE_DESTROY_DENY"),
    DEFENCE_PLACE_SUCCESS("Defences.DEFENCE_PLACE_SUCCESS"),
    TOO_MANY_DEFENCES_MESSAGE("Defences.TOO_MANY_DEFENCES"),
    DEFENCE_ENABLE_PLACEHOLDER("Defences.ENABLE_PLACEHOLDER"),
    DEFENCE_DISABLE_PLACEHOLDER("Defences.DISABLE_PLACEHOLDER"),
    DEFENCE_REMOVE_SUCCESS("Defences.DEFENCE_REMOVE_SUCCESS"),

    GEMS_COUNT_MESSAGE("Gems.GEM_COUNT"),
    GEM_ADD_SUCCESS("Gems.GEM_ADD_SUCCESS"),
    GEM_GIVE_SUCCESS("Gems.GEM_GIVE_SUCCESS"),
    INSUFFICIENT_GEMS_COUNT("Gems.INSUFFICIENT_GEM_COUNT"),
    GEMS_INSUFFICIENT_INVENTORY_SPACE("Gems.INSUFFICIENT_INVENTORY_SPACE"),
    GEMS_WITHDRAW_SUCCESS("Gems.GEMS_WITHDRAW_SUCCESS"),
    GEMS_DEPOSIT_SUCCESS("Gems.GEMS_DEPOSIT_SUCCESS"),
    NO_GEMS_PRESENT("Gems.NO_GEMS_PRESENT"),
    GEMS_ITEM_NAME("Gems.ITEM_NAME"),
    GEMS_ITEM_LORE("Gems.ITEM_LORE"),

    TRUST_SUCCESS("Islands.TRUST_SUCCESS"),
    UNTRUST_SUCCESS("Islands.UNTRUST_SUCCESS"),
    UNTRUST_FAILURE("Islands.UNTRUST_FAILURE"),
    PLAYER_NOT_TRUSTED("Islands.VISIT_NOT_TRUSTED"),
    VISIT_NO_ISLAND("Islands.VISIT_NO_ISLAND"),
    PLAYER_ALREADY_TRUSTED("Islands.ALREADY_TRUSTED"),
    VISIT_PROCESSING("Islands.VISIT_PROCESSING"),
    VISIT_IN_RAID("Islands.VISIT_IN_RAID"),

    FACTION_GEMS_DONATION_SUCCESS("Factions.GEMS_DONATION_SUCCESS"),
    FACTION_CREATION_PROCESSING("Factions.FACTION_CREATION.FACTION_CREATE_PROCESSING"),
    FACTION_ACTION_DENY("Factions.ACTION_DENY"),
    FACTION_NAME_LENGTH_LIMIT("Factions.FACTION_CREATION.NAME_LENGTH_LIMIT"),
    FACTION_NO_NUMBERS("Factions.FACTION_CREATION.NAME_NO_NUMBERS"),
    FACTION_NON_ENGLISH("Factions.FACTION_CREATION.NAME_NON_ENGLISH"),
    FACTION_NO_SYMBOLS("Factions.FACTION_CREATION.NAME_NO_SYMBOLS"),
    FACTION_NAME_PROHIBITED("Factions.FACTION_CREATION.NAME_PROHIBITED"),
    FACTION_INSUFFICIENT_FUNDS("Factions.FACTION_CREATION.INSUFFICIENT_FUNDS"),
    FACTION_CREATION_SUCCESS("Factions.FACTION_CREATION.FACTION_CREATE_SUCCESS"),
    FACTION_CREATION_NAME_DUPLICATE("Factions.FACTION_CREATION.DUPLICATE_NAME"),

    FACTION_LEAVE_SUCCESS("Factions.FACTION_LEAVE.FACTION_LEAVE_SUCCESS"),
    FACTION_OWNER_LEAVE_DENY("Factions.FACTION_LEAVE.FACTION_OWNER_LEAVE_DENY"),

    FACTION_BROADCAST_MODEL("Factions.FACTION_BROADCAST.BROADCAST_MODEL"),

    FACTION_OWNER_TITLE("Factions.FACTION_TITLES.OWNER"),
    FACTION_ADMIN_TITLE("Factions.FACTION_TITLES.ADMIN"),
    FACTION_MODERATOR_TITLE("Factions.FACTION_TITLES.MODERATOR"),
    FACTION_FIGHTER_TITLE("Factions.FACTION_TITLES.FIGHTER"),
    FACTION_MEMBER_TITLE("Factions.FACTION_TITLES.MEMBER"),

    FACTION_MANAGE_SELF_DENY("Factions.FACTION_MANAGE.MANAGE_SELF_DENY"),
    FACTION_MANAGE_SELF_DENY_LORE("Factions.FACTION_MANAGE.MANAGE_SELF_DENY_LORE"),
    FACTION_MANAGE_KICK_SUCCESS("Factions.FACTION_MANAGE.KICK_SUCCESS"),
    FACTION_MANAGE_KICK_BROADCAST("Factions.FACTION_MANAGE.KICK_BROADCAST"),
    FACTION_MANAGE_BAN_SUCCESS("Factions.FACTION_MANAGE.BAN_SUCCESS"),
    FACTION_MANAGE_BAN_BROADCAST("Factions.FACTION_MANAGE.BAN_BROADCAST"),

    FACTION_INVITE_SELF_DENY("Factions.FACTION_INVITE.INVITE_SELF_DENY"),
    FACTION_INVITE_IN_SAME_FACTION("Factions.FACTION_INVITE.INVITE_IN_SAME_FACTION"),
    FACTION_INVITE_CREATE_SUCCESS("Factions.FACTION_INVITE.INVITE_CREATE_SUCCESS"),
    JOIN_REQUEST_NOTIFICATION("Factions.FACTION_INVITE.JOIN_REQUEST_NOTIFICATION"),
    FACTION_INVITE_NOTIFICATION("Factions.FACTION_INVITE.INVITE_NOTIFICATION"),
    JOIN_REQUEST_CREATE_SUCCESS("Factions.FACTION_INVITE.JOIN_REQUEST_CREATE_SUCCESS"),
    JOIN_REQUEST_ALREADY_EXISTS("Factions.FACTION_INVITE.JOIN_REQUEST_ALREADY_EXISTS"),
    JOIN_REQUEST_SAME_FACTION("Factions.FACTION_INVITE.JOIN_REQUEST_SAME_FACTION"),
    FACTION_INVITE_DUPLICATE("Factions.FACTION_INVITE.INVITE_DUPLICATE"),
    FACTION_INVITE_REVOKE_SUCCESS("Factions.FACTION_INVITE.INVITE_REVOKE_SUCCESS"),
    FACTION_JOIN_REQUEST_ACCEPT_SUCCESS("Factions.FACTION_INVITE.JOIN_REQUEST_ACCEPT_SUCCESS"),
    FACTION_JOIN_REQUEST_ACCEPT_NOTIFICATION("Factions.FACTION_INVITE.JOIN_REQUEST_ACCEPT_NOTIFICATION"),
    FACTION_JOIN_REQUEST_REJECT_SUCCESS("Factions.FACTION_INVITE.JOIN_REQUEST_REJECT_SUCCESS"),
    FACTION_JOIN_REQUEST_REJECT_NOTIFICATION("Factions.FACTION_INVITE.JOIN_REQUEST_REJECT_NOTIFICATION"),
    FACTION_JOIN_REQUEST_NOT_EXIST("Factions.FACTION_INVITE.JOIN_REQUEST_NOT_EXIST"),
    FACTION_JOIN_REQUEST_REVOKE_SUCCESS("Factions.FACTION_INVITE.JOIN_REQUEST_REVOKE_SUCCESS"),
    FACTION_JOIN_REQUEST_DENY_SUCCESS("Factions.FACTION_INVITE.JOIN_REQUEST_DENY_SUCCESS"),
    PLAYER_FACTION_JOIN_SUCCESS("Factions.FACTION_INVITE.PLAYER_FACTION_JOIN_SUCCESS"),
    FACTION_INVITE_DENY_SUCCESS("Factions.FACTION_INVITE.FACTION_INVITE_DENY_SUCCESS"),
    FACTION_JOIN_BROADCAST("Factions.FACTION_INVITE.FACTION_JOIN_BROADCAST"),

    FACTION_DISBAND_DELETION_PROCESSING("Factions.FACTION_DISBAND.DELETION_PROCESSING"),
    FACTION_DISBAND_OWNER_REQUIRED("Factions.FACTION_DISBAND.FACTION_DISBAND_OWNER_REQUIRED"),
    FACTION_DISBAND_COMMAND_CONFIRM("Factions.FACTION_DISBAND.FACTION_DISBAND_COMMAND_CONFIRM"),
    FACTION_DISBAND_SUCCESS("Factions.FACTION_DISBAND.FACTION_DISBAND_SUCCESS"),
    FACTION_DISBAND_BROADCAST("Factions.FACTION_DISBAND.FACTION_DISBAND_BROADCAST"),
    FACTION_DISBAND_COMMAND_BLOCK("Factions.FACTION_DISBAND.FACTION_DISBAND_COMMAND_BLOCK"),

    ALREADY_IN_FACTION("Factions.ALREADY_IN_FACTION"),
    NOT_IN_FACTION("Factions.NOT_IN_FACTION"),
    FACTION_NOT_FOUND("Factions.FACTION_NOT_FOUND"),

    FACTION_INFO_LIST("Factions.FACTION_INFO.INFORMATION_MESSAGE"),
    MOTD_CHANGE_PROCESSING("Factions.CHANGE_MOTD.MOTD_PROCESSING"),
    MOTD_CHANGE_SUCCESS("Factions.CHANGE_MOTD.MOTD_CHANGE_SUCCESS"),

    NOTIFICATION_PENDING_FACTION_INVITATIONS("Notifications.PENDING_FACTION_INVITATIONS"),
    NOTIFICATION_PENDING_JOIN_REQUESTS("Notifications.PENDING_JOIN_REQUESTS"),
    UNREAD_NOTIFICATIONS("Notifications.UNREAD_NOTIFICATIONS"),

    OBELISK_ACCESS_DENY("Obelisk.ACCESS_DENY"),
    OBELISK_DESTROY_DENY("Obelisk.DESTROY_DENY"),
    OBELISK_GUI_DENY("Obelisk.OBELISK_GUI_DENY"),
    OBELISK_ITEM_NAME("Obelisk.OBELISK_BLOCK.ITEM_NAME"),
    OBELISK_ITEM_LORE("Obelisk.OBELISK_BLOCK.ITEM_LORE"),
    LOADING_ITEM_MATERIAL("Obelisk.LOADING.MATERIAL"),
    LOADING_ITEM_TEXT("Obelisk.LOADING.TEXT"),
    LOADING_ITEM_LORE("Obelisk.LOADING.LORE"),

    RUNE_ENCHANT_DENY("Runes.ENCHANTS_DENY"),
    RUNE_GENERAL_DENY("Runes.GENERAL_DENY"),
    RUNE_CONVERSION_SUCCESS("Runes.CONVERSION_SUCCESS"),
    RUNE_INSUFFICIENT_ITEMS("Runes.INSUFFICIENT_ITEMS"),
    RUNES_BALANCE_MESSAGE("Runes.RUNES_BALANCE"),
    RUNES_GIVE_SUCCESS("Runes.RUNES_GIVE_SUCCESS"),

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
    AUDIT_FACTION_BAN_DESCRIPTION("Audit_Logs.BAN_DESCRIPTION"),
    AUDIT_FACTION_INVITE_CREATE_TITLE("Audit_Logs.INVITE_CREATE_TITLE"),
    AUDIT_FACTION_INVITE_CREATE_DESCRIPTION("Audit_Logs.INVITE_CREATE_DESCRIPTION"),
    AUDIT_FACTION_JOIN_REQUEST_TITLE("Audit_Logs.JOIN_REQUEST_TITLE"),
    AUDIT_FACTION_JOIN_REQUEST_DESCRIPTION("Audit_Logs.JOIN_REQUEST_DESCRIPTION"),
    AUDIT_FACTION_INVITE_REVOKE_TITLE("Audit_Logs.INVITE_REVOKE_TITLE"),
    AUDIT_FACTION_INVITE_REVOKE_DESCRIPTION("Audit_Logs.INVITE_REVOKE_DESCRIPTION"),
    AUDIT_FACTION_JOIN_REQUEST_ACCEPT_TITLE("Audit_Logs.JOIN_REQUEST_ACCEPT_TITLE"),
    AUDIT_FACTION_JOIN_REQUEST_ACCEPT_DESCRIPTION("Audit_Logs.JOIN_REQUEST_ACCEPT_DESCRIPTION"),
    AUDIT_FACTION_JOIN_REQUEST_REJECT_TITLE("Audit_Logs.JOIN_REQUEST_REJECT_TITLE"),
    AUDIT_FACTION_JOIN_REQUEST_REJECT_DESCRIPTION("Audit_Logs.JOIN_REQUEST_REJECT_DESCRIPTION"),
    AUDIT_FACTION_PLAYER_JOIN_REQUEST_REVOKE_TITLE("Audit_Logs.PLAYER_JOIN_REQUEST_REVOKE_TITLE"),
    AUDIT_FACTION_PLAYER_JOIN_REQUEST_REVOKE_DESCRIPTION("Audit_Logs.PLAYER_JOIN_REQUEST_REVOKE_DESCRIPTION"),
    AUDIT_FACTION_PLAYER_JOIN_REQUEST_DENY_TITLE("Audit_Logs.PLAYER_JOIN_REQUEST_DENY_TITLE"),
    AUDIT_FACTION_PLAYER_JOIN_REQUEST_DENY_DESCRIPTION("Audit_Logs.PLAYER_JOIN_REQUEST_DENY_DESCRIPTION"),
    AUDIT_FACTION_PLAYER_INVITE_ACCEPT_TITLE("Audit_Logs.INVITE_ACCEPT_TITLE"),
    AUDIT_FACTION_PLAYER_INVITE_ACCEPT_DESCRIPTION("Audit_Logs.INVITE_ACCEPT_TITLE"),
    AUDIT_FACTION_PLAYER_INVITE_DENY_TITLE("Audit_Logs.INVITE_DENY_TITLE"),
    AUDIT_FACTION_PLAYER_INVITE_DENY_DESCRIPTION("Audit_Logs.INVITE_DENY_DESCRIPTION"),
    AUDIT_FACTION_DEFENCE_PURCHASE_TITLE("Audit_Logs.DEFENCE_PURCHASE_TITLE"),
    AUDIT_FACTION_DEFENCE_PURCHASE_DESCRIPTION("Audit_Logs.DEFENCE_PURCHASE_DESCRIPTION"),
    AUDIT_FACTION_DEFENCE_REMOVAL_TITLE("Audit_Logs.DEFENCE_REMOVAL_TITLE"),
    AUDIT_FACTION_DEFENCE_REMOVAL_DESCRIPTION("Audit_Logs.DEFENCE_REMOVAL_TITLE"),

    NOTIFICATION_TIMESTAMP_FORMAT("Notifications.NOTIFICATION_TYPES.NOTIFICATION_TIMESTAMP"),
    NOTIFICATION_FACTION_INVITE_TITLE("Notifications.NOTIFICATION_TYPES.FACTION_INVITE_TITLE"),
    NOTIFICATION_FACTION_INVITE_DESCRIPTION("Notifications.NOTIFICATION_TYPES.FACTION_INVITE_DESCRIPTION"),
    NOTIFICATION_JOIN_REQUEST_REJECT_TITLE("Notifications.NOTIFICATION_TYPES.JOIN_REQUEST_REJECT_TITLE"),
    NOTIFICATION_JOIN_REQUEST_REJECT_DESCRIPTION("Notifications.NOTIFICATION_TYPES.JOIN_REQUEST_REJECT_DESCRIPTION"),
    NOTIFICATION_JOIN_REQUEST_ACCEPT_TITLE("Notifications.NOTIFICATION_TYPES.JOIN_REQUEST_ACCEPT_TITLE"),
    NOTIFICATION_JOIN_REQUEST_ACCEPT_DESCRIPTION("Notifications.NOTIFICATION_TYPES.JOIN_REQUEST_ACCEPT_DESCRIPTION"),
    NOTIFICATION_FACTION_KICKED_TITLE("Notifications.NOTIFICATION_TYPE.FACTION_KICKED_TITLE"),
    NOTIFICATION_FACTION_KICKED_DESCRIPTION("Notifications.NOTIFICATION_TYPES.FACTION_KICKED_DESCRIPTION"),
    NOTIFICATION_DISMISS_SUCCESS("Notifications.NOTIFICATION_DISMISS_SUCCESS"),
    NOTIFICATION_FACTION_DISBANDED_TITLE("Notifications.FACTION_DISBANDED_TITLE"),
    NOTIFICATION_FACTION_DISBANDED_DESCRIPTION("Notifications.FACTION_DISBANDED_DESCRIPTION");

    public static Map<String, YamlDocument> configs = new HashMap<>();
    @Getter
    private final String path;

    Messages(String path) {
        this.path = path;
    }

    public static void load() {
        try {
            new File(SkyFactionsReborn.getInstance().getDataFolder(), "/language").mkdirs();
            SLogger.info("Saving default language [English].");
            YamlDocument doc = YamlDocument.create(new File(SkyFactionsReborn.getInstance().getDataFolder() + "/language/en/en.yml"), SkyFactionsReborn.getInstance().getResource("language/en/en.yml"),
                    GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("CONFIG_VERSION")).build());

            configs.put("en", doc);


            File folder = new File(SkyFactionsReborn.getInstance().getDataFolder(), "/language");
            if (!folder.exists() || !folder.isDirectory()) throw new Exception("Could not find the language folder. Please report this error ASAP.");

            for (File dir : folder.listFiles()) {
                
                if (dir.isDirectory()) {
                    SLogger.info("Registering Language: \u001B[32m{}", dir.getName());

                    if (!dir.getName().equalsIgnoreCase(Settings.DEFAULT_LANGUAGE.getString())) configs.put(
                        dir.getName(),
                        YamlDocument.create(new File(dir + "/" + dir.getName() + ".yml"), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(),
                                DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("CONFIG_VERSION")).build())
                    );

                    DefencesFactory.register(dir, dir.getName());
                    registerGUIs(dir, dir.getName());
                }
            }
        } catch (Exception exception) {
            SLogger.fatal("----------------------- CONFIGURATION EXCEPTION -----------------------");
            SLogger.fatal("There was an error loading language configs.");
            SLogger.fatal("Please check that config for any configuration mistakes.");
            SLogger.fatal("Plugin will now disable.");
            SLogger.fatal("----------------------- CONFIGURATION EXCEPTION -----------------------");
            exception.printStackTrace();
            Bukkit.getServer().getPluginManager().disablePlugin(SkyFactionsReborn.getInstance());
        }
        
    }

    private static void registerGUIs(File dir, String locale) throws IOException {
        Map<String, YamlDocument> docs = new HashMap<>();
        for (GUIEnums enumEntry : GUIEnums.values()) {
            YamlDocument doc = YamlDocument.create(new File(dir, enumEntry.getConfigPath() + ".yml"), SkyFactionsReborn.getInstance().getResource(String.format("language/%s/%s.yml", locale, enumEntry.getConfigPath())),
                    GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("CONFIG_VERSION")).build());

            docs.put(enumEntry.getConfigPath(), doc);
        }

        GUIEnums.configs.put(locale, docs);
    }

    public Component get(String locale, Object... replacements) {
        YamlDocument document = configs.getOrDefault(locale, getFallbackDocument());
        Object value = document.get("Messages." + this.path);

        Component message;
        if (value == null) {
            message = TextUtility.color("server_name&r&7 Message not found: " + this.path, locale, null);
        } else {
            message = value instanceof List ? TextUtility.fromList((List<?>) value, locale, null, replacements) : MiniMessage.miniMessage().deserialize(value.toString());
        }
        return message;
    }

    public String getString(String locale) {
        YamlDocument config = configs.getOrDefault(locale, getFallbackDocument());
        return config.getString("Messages." + this.path);
    }

    public List<String> getStringList(String locale) {
        YamlDocument config = configs.getOrDefault(locale, getFallbackDocument());
        List<String> val = config.getStringList("Messages." + this.path);

        if (val == null) {
            // we don't auto color this as this is only used for item lore which is handled already
            val = List.of(SERVER_NAME.get(locale) + "&r&7 Message not found: " + this.path);
        }

        return val;
    }

    public void send(CommandSender receiver, String locale, Object... replacements) {
        if (receiver == null) return;
        YamlDocument config = configs.getOrDefault(locale, getFallbackDocument());

        Object value = config.get("Messages." + this.path);

        Component message;
        if (value == null) {
            message = TextUtility.color(SERVER_NAME.get(locale) + "&r&7 Message not found: " + this.path, locale, receiver instanceof Player ? (Player) receiver : null , replacements);
        } else {
            message = value instanceof List ? TextUtility.fromList((List<?>) value, locale, receiver instanceof Player ? (Player) receiver : null, replacements) : TextUtility.color(String.valueOf(value), locale, receiver instanceof Player ? (Player) receiver : null, replacements);
        }

        receiver.sendMessage(message);
    }

    public static String replace(String value, String locale, Player player, Object... replacements) {
        if (DependencyHandler.isEnabled("PlaceholderAPI")) value = PlaceholderAPI.setPlaceholders(player, value);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 >= replacements.length) break;
            value = value.replace(String.valueOf(replacements[i]), String.valueOf(replacements[i + 1]));
        }

        return value;
    }

    public static YamlDocument getFallbackDocument() {
        return configs.get(Settings.DEFAULT_LANGUAGE.getString());
    }

    public static String getDefaulLocale() {
        return Settings.DEFAULT_LANGUAGE.getString();
    }

}
