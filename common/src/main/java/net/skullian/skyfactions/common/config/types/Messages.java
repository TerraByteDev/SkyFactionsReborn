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

    SERVER_NAME("server.name.message"),
    PERMISSION_DENY("permission.deny.message"),
    RELOADING("reloading.message"),
    RELOADED("reloaded.message"),
    COOLDOWN("cooldown.message"),
    ERROR("error.message"),
    INCORRECT_USAGE("incorrect.usage.message"),
    UNKNOWN_PLAYER("unknown.player.message"),
    PLEASE_WAIT("please.wait.message"),
    BLACKLISTED_PHRASE("blacklisted.phrase.message"),

    SYNC_RUNNING("sync.running.message"),
    SYNC_SUCCESS("sync.success.message"),

    INVALID_LANGUAGE("invalid.language.message"),
    MODIFYING_LANGUAGE("modifying.language.message"),
    LANGUAGE_MODIFIED("language.modified.message"),

    COMMAND_HEAD("commands.command.head.message"),
    COMMAND_INFO("commands.command.info.message"),
    NO_COMMANDS_FOUND("commands.no.commands.found.message"),

    ISLAND_CREATION_DENY("islands.creation.deny.message"),
    ISLAND_CREATING("islands.creating.message"),
    ISLAND_CREATED("islands.created.message"),
    NO_ISLAND("islands.no.island.message"),
    PLAYER_HAS_NO_ISLAND("islands.player.has.no.island.message"),
    DELETION_CONFIRM("islands.deletion.confirm.message"),
    ISLAND_DELETION_SUCCESS("islands.deletion.success.message"),
    DELETION_BLOCK("islands.deletion.block.message"),
    DELETION_PROCESSING("islands.deletion.processing.message"),
    NETHER_PORTALS_BLOCKED("islands.nether.portals.blocked.message"),
    ALREADY_ON_ISLAND("islands.already.on.message"),
    VISIT_ALREADY_ON_ISLAND("islands.visit.already.on.message"),

    DISCORD_RAID_MESSAGE("discord.raid.message"),
    DISCORD_LINK_SUCCESS("discord.link.success.message"),
    DISCORD_ALREADY_LINKED("discord.already.linked.message"),
    DISCORD_NOT_LINKED("discord.not.linked.message"),
    DISCORD_LINK_PROMPT("discord.link.prompt.message"),
    DISCORD_UNLINK_SUCCESS("discord.unlink.success.message"),
    DISCORD_APP_LINK_SUCCESS("discord.app.link.success.message"),
    DISCORD_APP_LINK_FAILED("discord.app.link.failed.message"),

    NPC_ACCESS_DENY("npc.access.deny.message"),
    NPC_RELOADING("npc.reloading.message"),
    NPC_RELOADED("npc.reloaded.message"),
    NPC_DISABLING("npc.disabling.message"),
    NPC_DISABLED("npc.disabled.message"),
    NPC_PLAYER_ISLANDS_ACTIONS("npc.actions.player.islands.message"),
    NPC_FACTION_ISLANDS_ACTIONS("npc.actions.faction.islands.message"),

    RAID_CONFIRMATION_NAME("raiding.confirmation.name.message"),
    RAID_CANCEL_NAME("raiding.cancel.name.message"),
    RAID_ON_COOLDOWN("raiding.on.cooldown.message"),
    RAID_INSUFFICIENT_GEMS("raiding.insufficient.gems.message"),
    RAIDING_COUNTDOWN_SUBTITLE("raiding.countdown.subtitle.message"),
    RAID_NO_PLAYERS("raiding.no.players.message"),
    RAID_PROCESSING("raiding.processing.message"),
    RAID_IMMINENT_MESSAGE("raiding.imminent.message"),

    DEFENCE_INSUFFICIENT_RUNES_LORE("defences.insufficient.runes.lore"),
    DEFENCE_INSUFFICIENT_INVENTORY_LORE("defences.insufficient.inventory.lore"),
    DEFENCE_INSUFFICIENT_PERMISSIONS_LORE("defences.insufficient.permissions.lore"),
    DEFENCE_INSUFFICIENT_PERMISSIONS("defences.insufficient.permissions.message"),
    DEFENCE_PURCHASE_SUCCESS("defences.purchase.success.message"),
    DEFENCE_DESTROY_DENY("defences.destroy.deny.message"),
    DEFENCE_PLACE_SUCCESS("defences.place.success.message"),
    TOO_MANY_DEFENCES_MESSAGE("defences.too.many.message"),
    DEFENCE_ENABLE_PLACEHOLDER("defences.enable.placeholder.message"),
    DEFENCE_DISABLE_PLACEHOLDER("defences.disable.placeholder.message"),
    DEFENCE_REMOVE_SUCCESS("defences.remove.success.message"),

    GEMS_COUNT_MESSAGE("gems.count.message"),
    GEMS_PAY_SUCCESS("gems.pay.success.message"),
    GEMS_PAY_NOTIFY("gems.pay.notify.message"),
    GEM_GIVE_SUCCESS("gems.give.success.message"),
    INSUFFICIENT_GEMS_COUNT("gems.insufficient.count.message"),
    GEMS_INSUFFICIENT_INVENTORY_SPACE("gems.insufficient.inventory.space.message"),
    GEMS_WITHDRAW_SUCCESS("gems.withdraw.success.message"),
    GEMS_DEPOSIT_SUCCESS("gems.deposit.success.message"),
    NO_GEMS_PRESENT("gems.no.present.message"),
    GEMS_ITEM_NAME("gems.item.name"),
    GEMS_ITEM_LORE("gems.item.lore"),

    TRUST_SUCCESS("islands.trust.success.message"),
    UNTRUST_SUCCESS("islands.untrust.success.message"),
    UNTRUST_FAILURE("islands.untrust.failure.message"),
    PLAYER_NOT_TRUSTED("islands.visit.not.trusted.message"),
    VISIT_NO_ISLAND("islands.visit.no.island.message"),
    PLAYER_ALREADY_TRUSTED("islands.already.trusted.message"),
    VISIT_PROCESSING("islands.visit.processing.message"),
    VISIT_IN_RAID("islands.visit.in.raid.message"),

    FACTION_GEMS_DONATION_SUCCESS("factions.gems.donation.success.message"),
    FACTION_CREATION_PROCESSING("factions.creation.processing.message"),
    FACTION_ACTION_DENY("factions.action.deny.message"),
    FACTION_NAME_LENGTH_LIMIT("factions.name.length.limit.message"),
    FACTION_NO_NUMBERS("factions.no.numbers.message"),
    FACTION_NON_ENGLISH("factions.non.english.message"),
    FACTION_NO_SYMBOLS("factions.no.symbols.message"),
    FACTION_PLAYER_ISLAND_REQUIRED("factions.player.island.required.message"),
    FACTION_INSUFFICIENT_FUNDS("factions.insufficient.funds.message"),
    FACTION_CREATION_SUCCESS("factions.creation.success.message"),
    FACTION_CREATION_NAME_DUPLICATE("factions.creation.name.duplicate.message"),

    FACTION_LEAVE_SUCCESS("factions.leave.success.message"),
    FACTION_LEAVE_OWNER_CONFIRMATION_LORE("factions.leave.owner.confirmation.lore"),

    FACTION_BROADCAST_MODEL("factions.broadcast.model.message"),

    FACTION_OWNER_TITLE("factions.owner.title.message"),
    FACTION_ADMIN_TITLE("factions.admin.title.message"),
    FACTION_MODERATOR_TITLE("factions.moderator.title.message"),
    FACTION_FIGHTER_TITLE("factions.fighter.title.message"),
    FACTION_MEMBER_TITLE("factions.member.title.message"),

    FACTION_MANAGE_SELF_DENY("factions.manage.self.deny.message"),
    FACTION_MANAGE_SELF_DENY_LORE("factions.manage.self.deny.lore"),
    FACTION_MANAGE_KICK_SUCCESS("factions.manage.kick.success.message"),
    FACTION_MANAGE_KICK_BROADCAST("factions.manage.kick.broadcast.message"),
    FACTION_MANAGE_BAN_SUCCESS("factions.manage.ban.success.message"),
    FACTION_MANAGE_BAN_BROADCAST("factions.manage.ban.broadcast.message"),
    FACTION_MANAGE_RANK_SELECTED("factions.manage.selected.rank.message"),
    RANK_CHANGE_SUCCESS("factions.rank.change.success.message"),
    FACTION_MANAGE_HIGHER_RANKS_DENY("factions.manage.higher.ranks.deny.message"),
    FACTION_MANAGE_HIGHER_RANKS_DENY_LORE("factions.manage.higher.ranks.deny.lore"),
    FACTION_MANAGE_NO_PERMISSIONS("factions.manage.no.permissions.message"),
    FACTION_MANAGE_NO_PERMISSIONS_LORE("factions.manage.no.permissions.lore"),

    FACTION_INVITE_SELF_DENY("factions.invite.self.deny.message"),
    FACTION_INVITE_IN_SAME_FACTION("factions.invite.same.faction.message"),
    FACTION_INVITE_CREATE_SUCCESS("factions.invite.create.success.message"),
    JOIN_REQUEST_NOTIFICATION("factions.invite.join.request.notification.message"),
    FACTION_INVITE_NOTIFICATION("factions.invite.notification.message"),
    JOIN_REQUEST_CREATE_SUCCESS("factions.invite.join.request.create.success.message"),
    JOIN_REQUEST_ALREADY_EXISTS("factions.invite.join.request.already.exists.message"),
    JOIN_REQUEST_SAME_FACTION("factions.invite.join.request.same.faction.message"),
    FACTION_INVITE_DUPLICATE("factions.invite.duplicate.message"),
    FACTION_INVITE_PLAYER_BANNED("factions.invite.player.banned.message"),
    FACTION_INVITE_REQUEST_JOIN_BANNED("factions.invite.request.join.banned.message"),
    FACTION_INVITE_REVOKE_SUCCESS("factions.invite.revoke.success.message"),
    FACTION_JOIN_REQUEST_ACCEPT_SUCCESS("factions.invite.join.request.accept.success.message"),
    FACTION_JOIN_REQUEST_ACCEPT_NOTIFICATION("factions.invite.join.request.accept.notification.message"),
    FACTION_JOIN_REQUEST_REJECT_SUCCESS("factions.invite.join.request.reject.success.message"),
    FACTION_JOIN_REQUEST_REJECT_NOTIFICATION("factions.invite.join.request.reject.notification.message"),
    FACTION_JOIN_REQUEST_NOT_EXIST("factions.invite.join.request.not.exist.message"),
    FACTION_JOIN_REQUEST_REVOKE_SUCCESS("factions.invite.join.request.revoke.success.message"),
    FACTION_JOIN_REQUEST_DENY_SUCCESS("factions.invite.join.request.deny.success.message"),
    PLAYER_FACTION_JOIN_SUCCESS("factions.invite.player.faction.join.success.message"),
    FACTION_INVITE_DENY_SUCCESS("factions.invite.deny.success.message"),
    FACTION_JOIN_BROADCAST("factions.invite.join.broadcast.message"),

    FACTION_DISBAND_DELETION_PROCESSING("factions.disband.deletion.processing.message"),
    FACTION_DISBAND_OWNER_REQUIRED("factions.disband.owner.required.message"),
    FACTION_DISBAND_COMMAND_CONFIRM("factions.disband.command.confirm.message"),
    FACTION_DISBAND_SUCCESS("factions.disband.success.message"),
    FACTION_DISBAND_BROADCAST("factions.disband.broadcast.message"),
    FACTION_DISBAND_COMMAND_BLOCK("factions.disband.command.block.message"),

    ALREADY_IN_FACTION("factions.already.in.faction.message"),
    NOT_IN_FACTION("factions.not.in.faction.message"),
    FACTION_NOT_FOUND("factions.not.found.message"),

    FACTION_INFO_LIST("factions.info.list.message"),
    MOTD_CHANGE_PROCESSING("factions.change.motd.processing.message"),
    MOTD_CHANGE_SUCCESS("factions.change.motd.success.message"),

    FACTION_RENAME_ON_COOLDOWN("factions.rename.on.cooldown.message"),
    FACTION_RENAME_NO_PERMISSIONS("factions.rename.no.permissions.message"),
    FACTION_RENAME_INSUFFICIENT_FUNDS("factions.rename.insufficient.funds.message"),

    NOTIFICATION_PENDING_FACTION_INVITATIONS("notifications.pending.faction.invitations.message"),
    NOTIFICATION_PENDING_JOIN_REQUESTS("notifications.pending.join.requests.message"),
    UNREAD_NOTIFICATIONS("notifications.unread.messages"),

    OBELISK_ACCESS_DENY("obelisk.access.deny.message"),
    OBELISK_DESTROY_DENY("obelisk.destroy.deny.message"),
    OBELISK_GUI_DENY("obelisk.gui.deny.message"),
    OBELISK_ITEM_NAME("obelisk.item.name.message"),
    OBELISK_ITEM_LORE("obelisk.item.lore.message"),
    LOADING_ITEM_MATERIAL("obelisk.loading.material.message"),
    LOADING_ITEM_TEXT("obelisk.loading.text.message"),
    LOADING_ITEM_LORE("obelisk.loading.lore.message"),

    RUNE_ENCHANT_DENY("runes.enchant.deny.message"),
    RUNE_GENERAL_DENY("runes.general.deny.message"),
    RUNE_CONVERSION_SUCCESS("runes.conversion.success.message"),
    RUNE_INSUFFICIENT_ITEMS("runes.insufficient.items.message"),
    RUNES_BALANCE_MESSAGE("runes.balance.message"),
    RUNES_GIVE_SUCCESS("runes.give.success.message"),
    RUNES_RESET_SUCCESS("runes.reset.success.message"),

    AUDIT_FACTION_CREATE_TITLE("audit.logs.faction.create.title.message"),
    AUDIT_FACTION_CREATE_DESCRIPTION("audit.logs.faction.create.description.message"),
    AUDIT_FACTION_JOIN_TITLE("audit.logs.join.title.message"),
    AUDIT_FACTION_JOIN_DESCRIPTION("audit.logs.join.description.message"),
    AUDIT_FACTION_LEAVE_TITLE("audit.logs.leave.title.message"),
    AUDIT_FACTION_LEAVE_DESCRIPTION("audit.logs.leave.description.message"),
    AUDIT_FACTION_MOTD_TITLE("audit.logs.motd.title.message"),
    AUDIT_FACTION_MOTD_DESCRIPTION("audit.logs.motd.description.message"),
    AUDIT_FACTION_TIMESTAMP_FORMAT("audit.logs.timestamp.format.message"),
    AUDIT_FACTION_BROADCAST_TITLE("audit.logs.broadcast.title.message"),
    AUDIT_FACTION_BROADCAST_DESCRIPTION("audit.logs.broadcast.description.message"),
    AUDIT_FACTION_KICK_TITLE("audit.logs.kick.title.message"),
    AUDIT_FACTION_KICK_DESCRIPTION("audit.logs.kick.description.message"),
    AUDIT_FACTION_BAN_TITLE("audit.logs.ban.title.message"),
    AUDIT_FACTION_BAN_DESCRIPTION("audit.logs.ban.description.message"),
    AUDIT_FACTION_INVITE_CREATE_TITLE("audit.logs.invite.create.title.message"),
    AUDIT_FACTION_INVITE_CREATE_DESCRIPTION("audit.logs.invite.create.description.message"),
    AUDIT_FACTION_JOIN_REQUEST_TITLE("audit.logs.join.request.title.message"),
    AUDIT_FACTION_JOIN_REQUEST_DESCRIPTION("audit.logs.join.request.description.message"),
    AUDIT_FACTION_INVITE_REVOKE_TITLE("audit.logs.invite.revoke.title.message"),
    AUDIT_FACTION_INVITE_REVOKE_DESCRIPTION("audit.logs.invite.revoke.description.message"),
    AUDIT_FACTION_JOIN_REQUEST_ACCEPT_TITLE("audit.logs.join.request.accept.title.message"),
    AUDIT_FACTION_JOIN_REQUEST_ACCEPT_DESCRIPTION("audit.logs.join.request.accept.description.message"),
    AUDIT_FACTION_JOIN_REQUEST_REJECT_TITLE("audit.logs.join.request.reject.title.message"),
    AUDIT_FACTION_JOIN_REQUEST_REJECT_DESCRIPTION("audit.logs.join.request.reject.description.message"),
    AUDIT_FACTION_PLAYER_JOIN_REQUEST_REVOKE_TITLE("audit.logs.player.join.request.revoke.title.message"),
    AUDIT_FACTION_PLAYER_JOIN_REQUEST_REVOKE_DESCRIPTION("audit.logs.player.join.request.revoke.description.message"),
    AUDIT_FACTION_PLAYER_JOIN_REQUEST_DENY_TITLE("audit.logs.player.join.request.deny.title.message"),
    AUDIT_FACTION_PLAYER_JOIN_REQUEST_DENY_DESCRIPTION("audit.logs.player.join.request.deny.description.message"),
    AUDIT_FACTION_PLAYER_INVITE_ACCEPT_TITLE("audit.logs.invite.accept.title.message"),
    AUDIT_FACTION_PLAYER_INVITE_ACCEPT_DESCRIPTION("audit.logs.invite.accept.description.message"),
    AUDIT_FACTION_PLAYER_INVITE_DENY_TITLE("audit.logs.invite.deny.title.message"),
    AUDIT_FACTION_PLAYER_INVITE_DENY_DESCRIPTION("audit.logs.invite.deny.description.message"),
    AUDIT_FACTION_DEFENCE_PURCHASE_TITLE("audit.logs.defence.purchase.title.message"),
    AUDIT_FACTION_DEFENCE_PURCHASE_DESCRIPTION("audit.logs.defence.purchase.description.message"),
    AUDIT_FACTION_DEFENCE_REMOVAL_TITLE("audit.logs.defence.removal.title.message"),
    AUDIT_FACTION_DEFENCE_REMOVAL_DESCRIPTION("audit.logs.defence.removal.description.message"),

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
    NOTIFICATION_FACTION_DISBANDED_TITLE("notifications.faction-disbanded-title"),
    NOTIFICATION_FACTION_DISBANDED_DESCRIPTION("notifications.faction-disbanded-description"),
    NOTIFICATION_FACTION_RANK_UPDATED_TITLE("notifications.faction-rank-updated-title"),
    NOTIFICATION_FACTION_RANK_UPDATED_DESCRIPTION("notifications.faction-rank-updated-description"),;

    public static final Map<String, YamlDocument> configs = new HashMap<>();
    private final String path;

    Messages(String path) {
        this.path = path;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void load(boolean setup) {
        try {
            new File(SkyApi.getInstance().getFileAPI().getConfigFolderPath(), "/language").mkdirs();
            if (setup) SLogger.setup("Saving default language <#05eb2f>[English]<#4294ed>.", false);
                else SLogger.info("Saving default language <#05eb2f>[English]<#4294ed>.");

            YamlDocument doc = YamlDocument.create(new File(SkyApi.getInstance().getFileAPI().getConfigFolderPath() + "/language/en_US/en_US.yml"), Objects.requireNonNull(Messages.class.getClassLoader().getResourceAsStream("language/en_US/en_US.yml")),
                    GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build());

            configs.put("en_US", doc);

            File folder = new File(SkyApi.getInstance().getFileAPI().getConfigFolderPath(), "/language");
            if (!folder.exists() || !folder.isDirectory()) throw new Exception("Could not find the language folder. Please report this error ASAP.");

            for (File dir : Objects.requireNonNull(folder.listFiles())) {
                
                if (dir.isDirectory()) {
                    if (setup) SLogger.setup("Registering Language: <#05eb2f>[{}]<#4294ed>", false, dir.getName());
                        else SLogger.info("Registering Language: <#05eb2f>[{}]<#4294ed>", dir.getName());

                    if (!dir.getName().equalsIgnoreCase(Settings.DEFAULT_LANGUAGE.getString())) configs.put(
                        dir.getName(),
                        YamlDocument.create(new File(dir + "/" + dir.getName() + ".yml"), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(),
                                DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build())
                    );

                    registerGUIs(dir, dir.getName());
                    SkyApi.getInstance().getDefenceFactory().register(dir, dir.getName());
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
            message = TextUtility.color(SERVER_NAME.get(locale) + "<reset><gray> Message not found: " + this.path, locale, receiver instanceof SkyUser ? (SkyUser) receiver : null , replacements);
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
