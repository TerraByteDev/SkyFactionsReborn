package net.skullian.torrent.skyfactions.config;

public enum GUIEnums {

    CREATE_ISLAND_CONFIRMATION("guis/confirmations/create_island"),
    START_RAID_CONFIRMATION("guis/confirmations/start_raid");

    private final String path;

    GUIEnums(String path) {
        this.path = path;
    }

    public String getConfigPath() {
        return path;
    }
}
