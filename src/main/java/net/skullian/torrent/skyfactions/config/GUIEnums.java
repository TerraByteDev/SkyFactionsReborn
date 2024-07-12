package net.skullian.torrent.skyfactions.config;

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
