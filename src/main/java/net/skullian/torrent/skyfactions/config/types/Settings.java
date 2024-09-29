package net.skullian.torrent.skyfactions.config.types;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

@Getter
public enum Settings {

    DATABASE_TYPE("Database.TYPE"),
    DATABASE_HOST("Database.DATABASE_HOST"),
    DATABASE_NAME("Database.DATABASE_NAME"),
    DATABASE_USERNAME("Database.DATABASE_USERNAME"),
    DATABASE_PASSWORD("Database.DATABASE_PASSWORD"),
    DATABASE_MAX_LIFETIME("Database.MAX_LIFETIME"),

    HUB_WORLD_NAME("Hub.WORLD_NAME"),
    HUB_LOCATION("Hub.HUB_LOCATION"),

    COMMAND_COOLDOWN("Commands.COOLDOWN"),

    SOUNDS_ISLAND_CREATE_SUCCESS("Sounds.ISLAND_CREATE_SUCCESS"),
    SOUNDS_ISLAND_CREATE_SUCCESS_PITCH("Sounds.ISLAND_CREATE_SUCCESS_PITCH"),
    SOUNDS_GUI_OPEN("Sounds.GUI_OPEN"),
    SOUNDS_GUI_OPEN_PITCH("Sounds.GUI_OPEN_PITCH"),
    ALARM_SOUND("Sounds.ALARM_SOUND"),
    ALARM_SOUND_PITCH("Sounds.ALARM_PITCH"),
    COUNTDOWN_SOUND("Sounds.COUNTDOWN_SOUND"),
    COUNTDOWN_SOUND_PITCH("Sounds.COUNTDOWN_PITCH"),

    ISLAND_FACTION_WORLD("Island.FACTION_WORLD_NAME"),
    ISLAND_PLAYER_WORLD("Island.ISLAND_WORLD_NAME"),
    ISLAND_PLAYER_SCHEMATIC("Island.NORMAL_ISLAND_SCHEMATIC"),
    ISLAND_FACTION_SCHEMATIC("Island.FACTION_ISLAND_SCHEMATIC"),
    ISLAND_TELEPORT_ON_JOIN("Island.TELEPORT_ON_JOIN"),
    ISLAND_TELEPORT_ON_DEATH("Island.TELEPORT_ON_DEATH"),

    GEN_PLAYER_REGION_SIZE("Generation.PLAYER_ISLANDS.REGION_SIZE"),
    GEN_PLAYER_REGION_PADDING("Generation.PLAYER_ISLANDS.REGION_PADDING"),
    GEN_PLAYER_GRID_ORIGIN("Generation.PLAYER_ISLANDS.GRID_ORIGIN"),

    GEN_FACTION_REGION_SIZE("Generation.FACTION_ISLANDS.REGION_SIZE"),
    GEN_FACTION_REGION_PADDING("Generation.FACTION_ISLANDS.REGION_PADDING"),
    GEN_FACTION_GRID_ORIGIN("Generation.FACTION_ISLANDS.GRID_ORIGIN"),

    RAIDING_COST("Raiding.RAIDING_COST"),
    RAIDING_COOLDOWN("Raiding.RAIDING_COOLDOWN"),
    RAIDED_COOLDOWN("Raiding.RAIDED_COOLDOWN"),
    RAIDING_SPAWN_HEIGHT("Raiding.SPAWN_HEIGHT"),
    RAIDING_MUSIC_FILES("Raiding.MUSIC_FILE_NAMES"),
    RAIDING_SPAWN_RADIUS("Raiding.RANDOM_SPAWN_RADIUS"),
    RAIDING_COUNTDOWN_DURATION("Raiding.COUNTDOWN_DURATION"),
    RAIDING_COUNTDOWN_SUBTITLE("Raiding.COUNTDOWN_SUBTITLE"),
    RAIDING_TELEPORT_TO_PREPARATION_AREA("Raiding.TELEPORT_TO_PREPARATION"),
    RAIDING_PREPARATION_WORLD("Raiding.RAID_PREPARATION_WORLD"),
    RAIDING_PREPARATION_POS("Raiding.RAID_PREPARATION_POS"),
    RAIDING_PREPARATION_TIME("Raiding.RAID_PREPARATION_TIME"),

    MAX_PLAYER_DEFENCES("Defences.MAX_PLAYER_DEFENCES"),
    MAX_FACTION_DEFENCES("Defences.MAX_FACTION_DEFENCES"),

    FACTION_CREATION_MIN_LENGTH("Factions.FACTION_CREATION.MINIMUM_NAME_LENGTH"),
    FACTION_CREATION_MAX_LENGTH("Factions.FACTION_CREATION.MAXIMUM_NAME_LENGTH"),
    FACTION_CREATION_ALLOW_NUMBERS("Factions.FACTION_CREATION.ALLOW_NUMBERS"),
    FACTION_CREATION_ALLOW_NON_ENGLISH("Factions.FACTION_CREATION.ALLOW_NON_ENGLISH"),
    FACTION_CREATION_ALLOW_SYMBOLS("Factions.FACTION_CREATION.ALLOW_SYMBOLS"),
    FACTION_CREATION_BLACKLISTED_NAMES("Factions.FACTION_CREATION.BLACKLISTED_NAMES"),
    FACTION_CREATION_COST("Factions.FACTION_CREATION.CREATION_COST"),

    FACTION_MANAGE_BROADCAST_KICKS("Factions.FACTION_MANAGE.BROADCAST_KICKS"),
    FACTION_MANAGE_BROADCAST_BANS("Factions.FACTION_MANAGE.BROADCAST_BANS"),

    NOTIFICATIONS_INTERVAL("Notifications.INTERVAL");

    @Setter
    private static FileConfiguration config;
    private final String path;

    Settings(String path) {
        this.path = path;
    }

    public List<String> getList() {
        return config.getStringList(this.path);
    }

    public List<Integer> getIntegerList() {
        return config.getIntegerList(this.path);
    }

    public String getString() {
        return config.getString(this.path);
    }

    public int getInt() {
        return config.getInt(this.path);
    }

    public boolean getBoolean() {
        return config.getBoolean(this.path);
    }


}
