package net.skullian.torrent.skyfactions.config;

public enum ExtraEnums {

    CREATE_ISLAND_GUI("guis/confirmations/create_island"),
    RAID_START_GUI("guis/confirmations/start_raid"),
    FACTION_LEAVE_GUI("guis/confirmations/faction_leave"),

    OBELISK_PLAYER_GUI("guis/obelisk/player_obelisk"),
    OBELISK_FACTION_GUI("guis/obelisk/faction_obelisk"),
    OBELISK_MEMBER_MANAGEMENT_GUI("guis/obelisk/member_management"),
    OBELISK_AUDIT_LOG_GUI("guis/obelisk/audit_log"),

    PAGINATION_MODEL("guis/pagination");



    private final String path;

    ExtraEnums(String path) {
        this.path = path;
    }

    public String getConfigPath() {
        return path;
    }
}
