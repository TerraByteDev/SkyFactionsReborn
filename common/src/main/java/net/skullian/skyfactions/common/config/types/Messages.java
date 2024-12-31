package net.skullian.skyfactions.common.config.types;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.common.util.text.TextUtility;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Getter
public enum Messages {

    LANGUAGE_NAME("language-name"),

    SERVER_NAME("server-name"),
    PERMISSION_DENY("permission-deny"),
    RELOADING("reloading"),
    RELOADED("reloaded"),
    COOLDOWN("cooldown"),
    ERROR("error"),
    INCORRECT_USAGE("incorrect-usage"),
    UNKNOWN_PLAYER("unknown-player"),
    PLEASE_WAIT("please-wait"),
    BLACKLISTED_PHRASE("blacklisted-phrase"),

    SYNC_RUNNING("sync-running"),
    SYNC_SUCCESS("sync-success"),

    COMMAND_HEAD("commands.command-head"),
    COMMAND_INFO("commands.command-info"),
    NO_COMMANDS_FOUND("commands.no-commands-found"),

    INVALID_LANGUAGE("language.invalid-language"),
    MODIFYING_LANGUAGE("language.modifying-language"),
    LANGUAGE_MODIFIED("language.language-modified"),

    ISLAND_CREATION_DENY("islands.island-creation-deny"),
    ISLAND_CREATING("islands.creating-message"),
    ISLAND_CREATED("islands.created-message"),
    NO_ISLAND("islands.no-island"),
    PLAYER_HAS_NO_ISLAND("islands.player-has-no-island"),
    DELETION_CONFIRM("islands.confirm-deletion"),
    ISLAND_DELETION_SUCCESS("islands.deletion-success"),
    DELETION_BLOCK("islands.deletion-block"),
    DELETION_PROCESSING("islands.deletion-processing"),
    NETHER_PORTALS_BLOCKED("islands.nether-portals-blocked"),
    ALREADY_ON_ISLAND("islands.already-on-island"),
    VISIT_ALREADY_ON_ISLAND("islands.visit-already-on-island"),

    DISCORD_RAID_MESSAGE("discord.discord-raid-message"),
    DISCORD_LINK_SUCCESS("discord.link-success-message"),
    DISCORD_ALREADY_LINKED("discord.already-linked"),
    DISCORD_NOT_LINKED("discord.not-linked"),
    DISCORD_LINK_PROMPT("discord.link-prompt"),
    DISCORD_UNLINK_SUCCESS("discord.unlink-success"),
    DISCORD_APP_LINK_SUCCESS("discord.discord-link-success-message"),
    DISCORD_APP_LINK_FAILED("discord.link-failed"),

    NPC_ACCESS_DENY("npc.npc-access-deny"),
    NPC_RELOADING("npc.npc-reloading"),
    NPC_RELOADED("npc.npc-reloaded"),
    NPC_DISABLING("npc.npc-disabling"),
    NPC_DISABLED("npc.npc-disabled"),
    NPC_PLAYER_ISLANDS_ACTIONS("npc.actions.player-islands"),
    NPC_FACTION_ISLANDS_ACTIONS("npc.actions.faction-islands"),

    RAID_CONFIRMATION_NAME("raiding.raid-confirmation-name"),
    RAID_CANCEL_NAME("raiding.raid-cancel-name"),
    RAID_ON_COOLDOWN("raiding.raid-on-cooldown"),
    RAID_INSUFFICIENT_GEMS("raiding.raid-insufficient-gems"),
    RAIDING_COUNTDOWN_SUBTITLE("raiding.countdown-subtitle"),
    RAID_NO_PLAYERS("raiding.no-players"),
    RAID_PROCESSING("raiding.raid-processing"),
    RAID_IMMINENT_MESSAGE("raiding.raided-notification"),

    DEFENCE_INSUFFICIENT_RUNES_LORE("defences.insufficient-runes-lore"),
    DEFENCE_INSUFFICIENT_INVENTORY_LORE("defences.insufficient-inventory-lore"),
    DEFENCE_INSUFFICIENT_PERMISSIONS_LORE("defences.insufficient-permissions-lore"),
    DEFENCE_INSUFFICIENT_PERMISSIONS("defences.defence-insufficient-permissions"),
    DEFENCE_PURCHASE_SUCCESS("defences.defence-purchase-success"),
    DEFENCE_DESTROY_DENY("defences.defence-destroy-deny"),
    DEFENCE_PLACE_SUCCESS("defences.defence-place-success"),
    TOO_MANY_DEFENCES_MESSAGE("defences.too-many-defences"),
    DEFENCE_ENABLE_PLACEHOLDER("defences.enable-placeholder"),
    DEFENCE_DISABLE_PLACEHOLDER("defences.disable-placeholder"),
    DEFENCE_REMOVE_SUCCESS("defences.defence-remove-success"),

    GEMS_COUNT_MESSAGE("gems.gem-count"),
    GEMS_PAY_SUCCESS("gems.gems-pay-success"),
    GEMS_PAY_NOTIFY("gems.gems-pay-notify"),
    GEM_GIVE_SUCCESS("gems.gem-give-success"),
    INSUFFICIENT_GEMS_COUNT("gems.insufficient-gem-count"),
    GEMS_INSUFFICIENT_INVENTORY_SPACE("gems.insufficient-inventory-space"),
    GEMS_WITHDRAW_SUCCESS("gems.gems-withdraw-success"),
    GEMS_DEPOSIT_SUCCESS("gems.gems-deposit-success"),
    NO_GEMS_PRESENT("gems.no-gems-present"),
    GEMS_ITEM_NAME("gems.item-name"),
    GEMS_ITEM_LORE("gems.item-lore"),

    TRUST_SUCCESS("islands.trust-success"),
    UNTRUST_SUCCESS("islands.untrust-success"),
    UNTRUST_FAILURE("islands.untrust-failure"),
    PLAYER_NOT_TRUSTED("islands.visit-not-trusted"),
    VISIT_NO_ISLAND("islands.visit-no-island"),
    PLAYER_ALREADY_TRUSTED("islands.already-trusted"),
    VISIT_PROCESSING("islands.visit-processing"),
    VISIT_IN_RAID("islands.visit-in-raid"),

    FACTION_GEMS_DONATION_SUCCESS("factions.gems-donation-success"),
    FACTION_CREATION_PROCESSING("factions.faction-creation.faction-create-processing"),
    FACTION_ACTION_DENY("factions.action-deny"),
    FACTION_NAME_LENGTH_LIMIT("factions.faction-creation.name-length-limit"),
    FACTION_NO_NUMBERS("factions.faction-creation.name-no-numbers"),
    FACTION_NON_ENGLISH("factions.faction-creation.name-non-english"),
    FACTION_NO_SYMBOLS("factions.faction-creation.name-no-symbols"),
    FACTION_PLAYER_ISLAND_REQUIRED("factions.faction-creation.player-island-required"),
    FACTION_INSUFFICIENT_FUNDS("factions.faction-creation.insufficient-funds"),
    FACTION_CREATION_SUCCESS("factions.faction-creation.faction-create-success"),
    FACTION_CREATION_NAME_DUPLICATE("factions.faction-creation.duplicate-name"),

    FACTION_LEAVE_SUCCESS("factions.faction-leave.faction-leave-success"),
    FACTION_LEAVE_OWNER_CONFIRMATION_LORE("factions.faction-leave.owner-confirmation-lore"),

    FACTION_BROADCAST_MODEL("factions.faction-broadcast.broadcast-model"),

    FACTION_OWNER_TITLE("factions.faction-titles.owner"),
    FACTION_ADMIN_TITLE("factions.faction-titles.admin"),
    FACTION_MODERATOR_TITLE("factions.faction-titles.moderator"),
    FACTION_FIGHTER_TITLE("factions.faction-titles.fighter"),
    FACTION_MEMBER_TITLE("factions.faction-titles.member"),

    FACTION_MANAGE_SELF_DENY("factions.faction-manage.manage-self-deny"),
    FACTION_MANAGE_SELF_DENY_LORE("factions.faction-manage.manage-self-deny-lore"),
    FACTION_MANAGE_KICK_SUCCESS("factions.faction-manage.kick-success"),
    FACTION_MANAGE_KICK_BROADCAST("factions.faction-manage.kick-broadcast"),
    FACTION_MANAGE_BAN_SUCCESS("factions.faction-manage.ban-success"),
    FACTION_MANAGE_BAN_BROADCAST("factions.faction-manage.ban-broadcast"),
    FACTION_MANAGE_RANK_SELECTED("factions.faction-manage.selected"),
    RANK_CHANGE_SUCCESS("factions.faction-manage.rank-change-success"),
    FACTION_MANAGE_HIGHER_RANKS_DENY("factions.faction-manage.manage-higher-ranks-deny"),
    FACTION_MANAGE_HIGHER_RANKS_DENY_LORE("factions.faction-manage.manage-higher-ranks-deny-lore"),
    FACTION_MANAGE_NO_PERMISSIONS("factions.faction-manage.manage-no-permissions"),
    FACTION_MANAGE_NO_PERMISSIONS_LORE("factions.faction-manage.manage-no-permissions-lore"),

    FACTION_INVITE_SELF_DENY("factions.faction-invite.invite-self-deny"),
    FACTION_INVITE_IN_SAME_FACTION("factions.faction-invite.invite-in-same-faction"),
    FACTION_INVITE_CREATE_SUCCESS("factions.faction-invite.invite-create-success"),
    JOIN_REQUEST_NOTIFICATION("factions.faction-invite.join-request-notification"),
    FACTION_INVITE_NOTIFICATION("factions.faction-invite.invite-notification"),
    JOIN_REQUEST_CREATE_SUCCESS("factions.faction-invite.join-request-create-success"),
    JOIN_REQUEST_ALREADY_EXISTS("factions.faction-invite.join-request-already-exists"),
    JOIN_REQUEST_SAME_FACTION("factions.faction-invite.join-request-same-faction"),
    FACTION_INVITE_DUPLICATE("factions.faction-invite.invite-duplicate"),
    FACTION_INVITE_PLAYER_BANNED("factions.faction-invite.invite-player-banned"),
    FACTION_INVITE_REQUEST_JOIN_BANNED("factions.faction-invite.request-join-banned"),
    FACTION_INVITE_REVOKE_SUCCESS("factions.faction-invite.invite-revoke-success"),
    FACTION_JOIN_REQUEST_ACCEPT_SUCCESS("factions.faction-invite.join-request-accept-success"),
    FACTION_JOIN_REQUEST_ACCEPT_NOTIFICATION("factions.faction-invite.join-request-accept-notification"),
    FACTION_JOIN_REQUEST_REJECT_SUCCESS("factions.faction-invite.join-request-reject-success"),
    FACTION_JOIN_REQUEST_REJECT_NOTIFICATION("factions.faction-invite.join-request-reject-notification"),
    FACTION_JOIN_REQUEST_NOT_EXIST("factions.faction-invite.join-request-not-exist"),
    FACTION_JOIN_REQUEST_REVOKE_SUCCESS("factions.faction-invite.join-request-revoke-success"),
    FACTION_JOIN_REQUEST_DENY_SUCCESS("factions.faction-invite.join-request-deny-success"),
    PLAYER_FACTION_JOIN_SUCCESS("factions.faction-invite.player-faction-join-success"),
    FACTION_INVITE_DENY_SUCCESS("factions.faction-invite.faction-invite-deny-success"),
    FACTION_JOIN_BROADCAST("factions.faction-invite.faction-join-broadcast"),

    FACTION_DISBAND_DELETION_PROCESSING("factions.faction-disband.deletion-processing"),
    FACTION_DISBAND_OWNER_REQUIRED("factions.faction-disband.faction-disband-owner-required"),
    FACTION_DISBAND_COMMAND_CONFIRM("factions.faction-disband.faction-disband-command-confirm"),
    FACTION_DISBAND_SUCCESS("factions.faction-disband.faction-disband-success"),
    FACTION_DISBAND_BROADCAST("factions.faction-disband.faction-disband-broadcast"),
    FACTION_DISBAND_COMMAND_BLOCK("factions.faction-disband.faction-disband-command-block"),

    ALREADY_IN_FACTION("factions.already-in-faction"),
    NOT_IN_FACTION("factions.not-in-faction"),
    FACTION_NOT_FOUND("factions.faction-not-found"),

    FACTION_INFO_LIST("factions.faction-info.information-message"),
    MOTD_CHANGE_PROCESSING("factions.change-motd.motd-processing"),
    MOTD_CHANGE_SUCCESS("factions.change-motd.motd-change-success"),

    FACTION_RENAME_ON_COOLDOWN("factions.faction-rename.rename-on-cooldown"),
    FACTION_RENAME_NO_PERMISSIONS("factions.faction-rename.rename-no-permissions"),
    FACTION_RENAME_INSUFFICIENT_FUNDS("factions.faction-rename.rename-insufficient-funds"),

    NOTIFICATION_PENDING_FACTION_INVITATIONS("notifications.pending-faction-invitations"),
    NOTIFICATION_PENDING_JOIN_REQUESTS("notifications.pending-join-requests"),
    UNREAD_NOTIFICATIONS("notifications.unread-notifications"),

    OBELISK_ACCESS_DENY("obelisk.access-deny"),
    OBELISK_DESTROY_DENY("obelisk.destroy-deny"),
    OBELISK_GUI_DENY("obelisk.obelisk-gui-deny"),
    OBELISK_ITEM_NAME("obelisk.obelisk-block.item-name"),
    OBELISK_ITEM_LORE("obelisk.obelisk-block.item-lore"),
    LOADING_ITEM_MATERIAL("obelisk.loading.material"),
    LOADING_ITEM_TEXT("obelisk.loading.text"),
    LOADING_ITEM_LORE("obelisk.loading.lore"),

    RUNE_ENCHANT_DENY("runes.enchants-deny"),
    RUNE_GENERAL_DENY("runes.general-deny"),
    RUNE_CONVERSION_SUCCESS("runes.conversion-success"),
    RUNE_INSUFFICIENT_ITEMS("runes.insufficient-items"),
    RUNES_BALANCE_MESSAGE("runes.runes-balance"),
    RUNES_GIVE_SUCCESS("runes.runes-give-success"),
    RUNES_RESET_SUCCESS("runes.runes-reset-success"),

    AUDIT_FACTION_CREATE_TITLE("audit-logs.faction-create-title"),
    AUDIT_FACTION_CREATE_DESCRIPTION("audit-logs.faction-create-description"),
    AUDIT_FACTION_JOIN_TITLE("audit-logs.join-title"),
    AUDIT_FACTION_JOIN_DESCRIPTION("audit-logs.join-description"),
    AUDIT_FACTION_LEAVE_TITLE("audit-logs.leave-title"),
    AUDIT_FACTION_LEAVE_DESCRIPTION("audit-logs.leave-description"),
    AUDIT_FACTION_MOTD_TITLE("audit-logs.motd-title"),
    AUDIT_FACTION_MOTD_DESCRIPTION("audit-logs.motd-description"),
    AUDIT_FACTION_TIMESTAMP_FORMAT("audit-logs.audit-timestamp"),
    AUDIT_FACTION_BROADCAST_TITLE("audit-logs.broadcast-title"),
    AUDIT_FACTION_BROADCAST_DESCRIPTION("audit-logs.broadcast-description"),
    AUDIT_FACTION_KICK_TITLE("audit-logs.kick-title"),
    AUDIT_FACTION_KICK_DESCRIPTION("audit-logs.kick-description"),
    AUDIT_FACTION_BAN_TITLE("audit-logs.ban-title"),
    AUDIT_FACTION_BAN_DESCRIPTION("audit-logs.ban-description"),
    AUDIT_FACTION_INVITE_CREATE_TITLE("audit-logs.invite-create-title"),
    AUDIT_FACTION_INVITE_CREATE_DESCRIPTION("audit-logs.invite-create-description"),
    AUDIT_FACTION_JOIN_REQUEST_TITLE("audit-logs.join-request-title"),
    AUDIT_FACTION_JOIN_REQUEST_DESCRIPTION("audit-logs.join-request-description"),
    AUDIT_FACTION_INVITE_REVOKE_TITLE("audit-logs.invite-revoke-title"),
    AUDIT_FACTION_INVITE_REVOKE_DESCRIPTION("audit-logs.invite-revoke-description"),
    AUDIT_FACTION_JOIN_REQUEST_ACCEPT_TITLE("audit-logs.join-request-accept-title"),
    AUDIT_FACTION_JOIN_REQUEST_ACCEPT_DESCRIPTION("audit-logs.join-request-accept-description"),
    AUDIT_FACTION_JOIN_REQUEST_REJECT_TITLE("audit-logs.join-request-reject-title"),
    AUDIT_FACTION_JOIN_REQUEST_REJECT_DESCRIPTION("audit-logs.join-request-reject-description"),
    AUDIT_FACTION_PLAYER_JOIN_REQUEST_REVOKE_TITLE("audit-logs.player-join-request-revoke-title"),
    AUDIT_FACTION_PLAYER_JOIN_REQUEST_REVOKE_DESCRIPTION("audit-logs.player-join-request-revoke-description"),
    AUDIT_FACTION_PLAYER_JOIN_REQUEST_DENY_TITLE("audit-logs.player-join-request-deny-title"),
    AUDIT_FACTION_PLAYER_JOIN_REQUEST_DENY_DESCRIPTION("audit-logs.player-join-request-deny-description"),
    AUDIT_FACTION_PLAYER_INVITE_ACCEPT_TITLE("audit-logs.invite-accept-title"),
    AUDIT_FACTION_PLAYER_INVITE_ACCEPT_DESCRIPTION("audit-logs.invite-accept-description"),
    AUDIT_FACTION_PLAYER_INVITE_DENY_TITLE("audit-logs.invite-deny-title"),
    AUDIT_FACTION_PLAYER_INVITE_DENY_DESCRIPTION("audit-logs.invite-deny-description"),
    AUDIT_FACTION_DEFENCE_PURCHASE_TITLE("audit-logs.defence-purchase-title"),
    AUDIT_FACTION_DEFENCE_PURCHASE_DESCRIPTION("audit-logs.defence-purchase-description"),
    AUDIT_FACTION_DEFENCE_REMOVAL_TITLE("audit-logs.defence-removal-title"),
    AUDIT_FACTION_DEFENCE_REMOVAL_DESCRIPTION("audit-logs.defence-removal-description"),

    NOTIFICATION_TIMESTAMP_FORMAT("notifications.notification-types.notification-timestamp"),
    NOTIFICATION_FACTION_INVITE_TITLE("notifications.notification-types.faction-invite-title"),
    NOTIFICATION_FACTION_INVITE_DESCRIPTION("notifications.notification-types.faction-invite-description"),
    NOTIFICATION_JOIN_REQUEST_REJECT_TITLE("notifications.notification-types.join-request-reject-title"),
    NOTIFICATION_JOIN_REQUEST_REJECT_DESCRIPTION("notifications.notification-types.join-request-reject-description"),
    NOTIFICATION_JOIN_REQUEST_ACCEPT_TITLE("notifications.notification-types.join-request-accept-title"),
    NOTIFICATION_JOIN_REQUEST_ACCEPT_DESCRIPTION("notifications.notification-types.join-request-accept-description"),
    NOTIFICATION_FACTION_KICKED_TITLE("notifications.notification-types.faction-kicked-title"),
    NOTIFICATION_FACTION_KICKED_DESCRIPTION("notifications.notification-types.faction-kicked-description"),
    NOTIFICATION_DISMISS_SUCCESS("notifications.notification-dismiss-success"),
    NOTIFICATION_FACTION_DISBANDED_TITLE("notifications.notification-types.faction-disbanded-title"),
    NOTIFICATION_FACTION_DISBANDED_DESCRIPTION("notifications.notification-types.faction-disbanded-description"),
    NOTIFICATION_FACTION_RANK_UPDATED_TITLE("notifications.notification-types.faction-rank-updated-title"),
    NOTIFICATION_FACTION_RANK_UPDATED_DESCRIPTION("notifications.notification-types.faction-rank-updated-description");

    public static final Map<String, YamlDocument> configs = new HashMap<>();
    private final String path;

    Messages(String path) {
        this.path = path;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void load(boolean setup) {
        try {
            new File(SkyApi.getInstance().getFileAPI().getConfigFolderPath(), "/language").mkdirs();

            String defaultLog = "Loading language <#05eb2f>[English]<#4294ed>.";
            if (setup) SLogger.setup(defaultLog, false);
                else SLogger.info(defaultLog);

            YamlDocument doc = YamlDocument.create(new File(SkyApi.getInstance().getFileAPI().getConfigFolderPath() + "/language/en_US/en_US.yml"), Objects.requireNonNull(Messages.class.getClassLoader().getResourceAsStream("language/en_US/en_US.yml")),
                    GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build());

            configs.put("en_US", doc);

            File folder = new File(SkyApi.getInstance().getFileAPI().getConfigFolderPath(), "/language");
            if (!folder.exists() || !folder.isDirectory()) throw new Exception("Could not find the language folder. Please report this error ASAP.");

            for (File dir : Objects.requireNonNull(folder.listFiles())) {
                
                if (dir.isDirectory()) {
                    String log = "Registering Language: <#05eb2f>[" + dir.getName() + "]<#4294ed>";
                    if (setup) SLogger.setup(log, false);
                        else SLogger.info(log);

                    if (!dir.getName().equalsIgnoreCase(Settings.DEFAULT_LANGUAGE.getString())) configs.put(
                        dir.getName(),
                        YamlDocument.create(new File(dir + "/" + dir.getName() + ".yml"), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(),
                                DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build())
                    );

                    registerGUIs(dir, dir.getName());
                    SkyApi.getInstance().getDefenceFactory().register(dir, dir.getName(), setup);
                }
            }
        } catch (Exception exception) {
            SLogger.setup("----------------- CONFIGURATION EXCEPTION -----------------", true);
            SLogger.setup("There was an error loading language configs.", true);
            SLogger.setup("Please check that config for any configuration mistakes.", true);
            SLogger.setup("Plugin will now disable.", true);
            SLogger.setup("----------------- CONFIGURATION EXCEPTION -----------------", true);
            SLogger.fatal(exception);
            SkyApi.disablePlugin();
        }
        
    }

    private static void registerGUIs(File dir, String locale) throws IOException {
        Map<String, YamlDocument> docs = new HashMap<>();
        for (GUIEnums enumEntry : GUIEnums.values()) {
            YamlDocument doc = YamlDocument.create(new File(dir,  "guis/" + enumEntry.getPath() + ".yml"), Objects.requireNonNull(Messages.class.getClassLoader().getResourceAsStream(String.format("language/%s/%s.yml", locale, "guis/" + enumEntry.getPath()))),
                    GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build());

            docs.put(enumEntry.getPath(), doc);
        }

        GUIEnums.configs.put(locale, docs);
    }

    public Component get(String locale, Object... replacements) {
        YamlDocument document = configs.getOrDefault(locale, getFallbackDocument());
        Object value = document.get(this.path);

        Component message;
        if (value == null) {
            message = TextUtility.color(SERVER_NAME.getString(locale) + "<reset><gray> Message not found: " + this.path, locale, null);
        } else {
            message = value instanceof List ? TextUtility.fromList((List<?>) value, locale, null, replacements) : MiniMessage.miniMessage().deserialize(value.toString());
        }
        return message;
    }

    public String getString(String locale) {
        YamlDocument config = configs.getOrDefault(locale, getFallbackDocument());
        return config.getString(this.path);
    }

    public List<String> getStringList(String locale) {
        YamlDocument config = configs.getOrDefault(locale, getFallbackDocument());
        List<String> val = config.getStringList(this.path);

        if (val == null) {
            // we don't auto color this as this is only used for item lore which is handled already
            val = List.of(SERVER_NAME.getString(locale) + "<reset><gray> Message not found: " + this.path);
        }

        return val;
    }

    public void send(Object receiver, String locale, Object... replacements) {
        if (receiver == null) return;
        YamlDocument config = configs.getOrDefault(locale, getFallbackDocument());

        Object value = config.get(this.path);

        Component message;
        if (value == null) {
            message = TextUtility.color(SERVER_NAME.getString(locale) + "<reset><gray> Message not found: " + this.path, locale, receiver instanceof SkyUser ? (SkyUser) receiver : null , replacements);
        } else {
            message = value instanceof List ? TextUtility.fromList((List<?>) value, locale, receiver instanceof SkyUser ? (SkyUser) receiver : null, replacements) : TextUtility.color(String.valueOf(value), locale, receiver instanceof SkyUser ? (SkyUser) receiver : null, replacements);
        }

        SkyApi.getInstance().getUserManager().of(receiver).sendMessage(message);
    }

    public static String replace(String value, SkyUser player, Object... replacements) {
        value = SkyApi.getInstance().getPlayerAPI().processText(player, value);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 >= replacements.length) break;
            value = value.replace("<" + replacements[i] + ">", String.valueOf(replacements[i + 1]));
        }

        return value;
    }

    public static List<String> replace(List<String> values, SkyUser player, Object... replacements) {
        List<String> formatted = new ArrayList<>();

        for (String val : values) {
            val = SkyApi.getInstance().getPlayerAPI().processText(player, val);
            for (int i = 0; i < replacements.length; i += 2) {
                if (i + 1 >= replacements.length) break;
                val = val.replace("<" + replacements[i] + ">", String.valueOf(replacements[i + 1]));
            }

            formatted.add(val);
        }

        return formatted;
    }

    public static YamlDocument getFallbackDocument() {
        return configs.get(Settings.DEFAULT_LANGUAGE.getString());
    }

    public static String getDefaulLocale() {
        return Settings.DEFAULT_LANGUAGE.getString();
    }

}
