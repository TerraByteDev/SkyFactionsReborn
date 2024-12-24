package net.skullian.skyfactions.common.config.types;

import dev.dejvokep.boostedyaml.YamlDocument;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public enum Settings {

    DATABASE_TYPE("database.type"),
    DATABASE_HOST("database.database-host"),
    DATABASE_NAME("database.database-name"),
    DATABASE_USERNAME("database.database-username"),
    DATABASE_PASSWORD("database.database-password"),
    DATABASE_USE_SSL("database.use-ssl"),
    DATABASE_MAX_LIFETIME("database.max-lifetime"),
    DATABASE_MAX_POOL_SIZE("database.max-pool-size"),
    CACHE_SAVE_INTERVAL("database.cache-save-interval"),

    DEFAULT_LANGUAGE("language.default-language"),
    BLACKLISTED_PHRASES("language.blacklisted-phrases"),

    HUB_WORLD_NAME("hub.world-name"),
    HUB_LOCATION("hub.hub-location"),

    COMMAND_COOLDOWN("cooldowns.command"),
    ITEM_COOLDOWN("cooldowns.gui-item"),

    SOUNDS_ISLAND_CREATE_SUCCESS("sounds.island-create-success"),
    SOUNDS_ISLAND_CREATE_SUCCESS_PITCH("sounds.island-create-success-pitch"),
    ALARM_SOUND("sounds.alarm-sound"),
    ALARM_SOUND_PITCH("sounds.alarm-pitch"),
    COUNTDOWN_SOUND("sounds.countdown-sound"),
    COUNTDOWN_SOUND_PITCH("sounds.countdown-pitch"),
    ERROR_SOUND("sounds.error-sound"),
    ERROR_SOUND_PITCH("sounds.error-sound-pitch"),
    DEFENCE_PURCHASE_SUCCESS_SOUND("sounds.defence-purchase-success-sound"),
    DEFENCE_PURCHASE_SUCCESS_SOUND_PITCH("sounds.defence-purchase-success-sound-pitch"),

    ISLAND_FACTION_WORLD("island.faction-world-name"),
    ISLAND_PLAYER_WORLD("island.island-world-name"),
    ISLAND_PLAYER_SCHEMATIC("island.normal-island-schematic"),
    ISLAND_FACTION_SCHEMATIC("island.faction-island-schematic"),
    ISLAND_TELEPORT_ON_JOIN("island.teleport-on-join"),
    ISLAND_TELEPORT_ON_DEATH("island.teleport-on-death"),
    ISLAND_PREVENT_NETHER_PORTALS("island.prevent-nether-portals"),
    ISLAND_ALLOWED_DIMENSIONS("island.allowed-dimensions"),

    NPC_INTEGRATION_ENABLED("npc.enabled"),
    NPC_PLAYER_ISLANDS_ENTITY("npc.entity.player-islands"),
    NPC_FACTION_ISLANDS_ENTITY("npc.entity.faction-islands"),
    NPC_FACTORY("npc.factory"),
    NPC_PLAYER_ISLANDS_NAME("npc.names.player-islands"),
    NPC_FACTION_ISLANDS_NAME("npc.names.faction-islands"),
    NPC_PLAYER_ISLANDS_SKIN("npc.skins.player-islands"),
    NPC_FACTION_ISLANDS_SKIN("npc.skins.faction-islands"),
    NPC_PLAYER_ISLANDS_OFFSET("npc.offsets.player-islands"),
    NPC_FACTION_ISLANDS_OFFSET("npc.offsets.faction-islands"),

    GEN_PLAYER_REGION_SIZE("generation.player-islands.region-size"),
    GEN_PLAYER_REGION_PADDING("generation.player-islands.region-padding"),
    GEN_PLAYER_GRID_ORIGIN("generation.player-islands.grid-origin"),

    GEN_FACTION_REGION_SIZE("generation.faction-islands.region-size"),
    GEN_FACTION_REGION_PADDING("generation.faction-islands.region-padding"),
    GEN_FACTION_GRID_ORIGIN("generation.faction-islands.grid-origin"),

    GEMS_CAN_WIDHTRAW("gems.can-withdraw"),
    GEMS_MATERIAL("gems.material"),
    GEMS_CUSTOM_MODEL_DATA("gems.custom-model-data"),

    RAIDING_COST("raiding.raiding-cost"),
    RAIDING_COOLDOWN("raiding.raiding-cooldown"),
    RAIDED_COOLDOWN("raiding.raided-cooldown"),
    RAIDING_SPAWN_HEIGHT("raiding.spawn-height"),
    RAIDING_MUSIC_FILES("raiding.music-file-names"),
    RAIDING_SPAWN_RADIUS("raiding.random-spawn-radius"),
    RAIDING_COUNTDOWN_DURATION("raiding.countdown-duration"),
    RAIDING_TELEPORT_TO_PREPARATION_AREA("raiding.teleport-to-preparation"),
    RAIDING_PREPARATION_WORLD("raiding.raid-preparation-world"),
    RAIDING_PREPARATION_POS("raiding.raid-preparation-pos"),
    RAIDING_PREPARATION_TIME("raiding.raid-preparation-time"),
    RAIDING_PLAYER_IMMUNITY("raiding.player-raid-immunity"),
    RAIDING_FACTION_IMMUNITY("raiding.faction-raid-immunity"),

    FACTION_CREATION_MIN_LENGTH("factions.faction-creation.minimum-name-length"),
    FACTION_CREATION_MAX_LENGTH("factions.faction-creation.maximum-name-length"),
    FACTION_CREATION_ALLOW_NUMBERS("factions.faction-creation.allow-numbers"),
    FACTION_CREATION_ALLOW_NON_ENGLISH("factions.faction-creation.allow-non-english"),
    FACTION_CREATION_ALLOW_SYMBOLS("factions.faction-creation.allow-symbols"),
    FACTION_CREATION_COST("factions.faction-creation.creation-cost"),

    FACTION_RENAME_ENABLED("factions.faction-renaming.allow-renaming"),
    FACTION_RENAME_COOLDOWN("factions.faction-renaming.cooldown"),
    FACTION_RENAME_COST("factions.faction-renaming.rename-cost"),

    FACTION_BAN_PERMISSIONS("factions.faction-ranks.ban"),
    FACTION_KICK_PERMISSIONS("factions.faction-ranks.kick"),
    FACTION_MANAGE_RANK_PERMISSIONS("factions.faction-ranks.manage-rank"),
    FACTION_MODIFY_MOTD_PERMISSIONS("factions.faction-ranks.modify-motd"),
    FACTION_MANAGE_INVITES_PERMISSIONS("factions.faction-ranks.manage-invites"),
    FACTION_CREATE_BROADCAST_PERMISSIONS("factions.faction-ranks.create-broadcast"),

    FACTION_MANAGE_BROADCAST_KICKS("factions.faction-manage.broadcast-kicks"),
    FACTION_MANAGE_BROADCAST_BANS("factions.faction-manage.broadcast-bans"),
    FACTION_MANAGE_RANK_SHOW_ENCHANTED("factions.faction-manage.manage-rank.show-enchanted"),

    NOTIFICATIONS_INTERVAL("notifications.interval");

    @Setter
    private static YamlDocument config;
    private final String path;

    Settings(String path) {
        this.path = path;
    }

    public List<String> getList() {
        return config.getStringList(this.path);
    }

    public List<Integer> getIntegerList() {
        return config.getIntList(this.path);
    }

    public String getString() {
        return config.getString(this.path);
    }

    public int getInt() {
        return config.getInt(this.path);
    }

    public long getLong() {
        return config.getLong(this.path);
    }

    public boolean getBoolean() {
        return config.getBoolean(this.path);
    }


}
