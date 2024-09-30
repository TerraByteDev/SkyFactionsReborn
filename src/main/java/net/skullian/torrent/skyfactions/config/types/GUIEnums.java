package net.skullian.torrent.skyfactions.config.types;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public enum GUIEnums {

    RUNES_SUBMIT_GUI("guis/runes_ui"),
    CREATE_ISLAND_GUI("guis/confirmations/create_island"),
    RAID_START_GUI("guis/confirmations/start_raid"),
    FACTION_LEAVE_GUI("guis/confirmations/faction_leave"),

    OBELISK_PLAYER_GUI("guis/obelisk/player_obelisk"),
    OBELISK_FACTION_GUI("guis/obelisk/faction_obelisk"),
    OBELISK_MEMBER_MANAGEMENT_GUI("guis/obelisk/member_management"),
    OBELISK_MANAGE_MEMBER_GUI("guis/obelisk/manage_member"),
    OBELISK_AUDIT_LOG_GUI("guis/obelisk/audit_log"),
    OBELISK_INVITE_SELECTION_GUI("guis/obelisk/invites/invite_selection"),
    OBELISK_INVITE_INCOMING_GUI("guis/obelisk/invites/incoming_requests"),
    OBELISK_INVITE_OUTGOING_GUI("guis/obelisk/invites/outgoing_invites"),
    OBELISK_PLAYER_INVITE_MANAGE_GUI("guis/obelisk/invites/player_invite_manage"),
    OBELISK_PLAYER_INCOMING_INVITES_GUI("guis/obelisk/invites/player_faction_invites"),
    OBELISK_PLAYER_INVITE_TYPE_SELECTION_GUI("guis/obelisk/invites/player_invite_selection"),
    OBELISK_PLAYER_JOIN_REQUEST_MANAGE_GUI("guis/obelisk/invites/player_join_request"),
    OBELISK_JOIN_REQUEST_MANAGE_GUI("guis/obelisk/invites/join_request_manage"),
    OBELISK_PLAYER_NOTIFICATIONS_GUI("guis/obelisk/player_notifications"),
    OBELISK_DEFENCE_PURCHASE_GUI("guis/obelisk/defences/defence_purchase"),

    PAGINATION_MODEL("guis/pagination");


    private final String path;
    public static final Map<String, FileConfiguration> configs = new HashMap<>();

    GUIEnums(String path) {
        this.path = path;
    }

    public String getConfigPath() {
        return path;
    }
}
