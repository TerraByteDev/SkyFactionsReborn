package net.skullian.torrent.skyfactions.config;

public enum ExtraEnums {

    CREATE_ISLAND_GUI("guis/confirmations/create_island"),
    RAID_START_GUI("guis/confirmations/start_raid"),
    OBELISK_PLAYER_GUI("guis/obelisk/player_obelisk");



    private final String path;

    ExtraEnums(String path) {
        this.path = path;
    }

    public String getConfigPath() {
        return path;
    }
}
