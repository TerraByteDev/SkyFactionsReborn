package net.skullian.skyfactions.common;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SharedConstants {

    public static final String PLUGIN_NAMESPACE = "skyfactionsreborn"; // Namespace of all NamespacedKeys

    public static final String DEFENCE_IDENTIFIER_KEY = "defence-identifier"; // Used to store the Defence ID of a defence item. [STRING]
    public static final String DEFENCE_DATA_KEY = "defence-data"; // Used to store all defence data in a block - a JSON string containing durability, ammo, etc. [STRING]
    public static final String DEFENCE_DAMAGE_KEY = "defence-damage"; // Applied to an entity which is being targeted by a Defence. [INTEGER]
    public static final String DEFENCE_DAMAGE_MESSAGE_KEY = "damage-message"; // Applied if a targeted defence entity is a player. Will be sent if hit. [STRING]

    public static final String OBELISK_TYPE_KEY = "obelisk-type"; // Identifies whether an obelisk is a faction or player. [STRING]
    public static final String OBELISK_OWNER_KEY = "obelisk-owner"; // Identifies the owner of an obelisk. Will either be a UUID of a player or name of a faction. [STRING]
    public static final String OBELISK_KEY = "obelisk"; // Present if a block is an obelisk. [BOOLEAN]

    public static final String WORLD_BORDER_DATA_KEY = "world-border-data"; // Used to set WorldBorder Data tags [BorderDataTags]
    public static final String WORLD_BORDER_X_KEY = "center-x"; // Key for WorldBorder PDC. [DOUBLE]
    public static final String WORLD_BORDER_Z_KEY = "center-z"; // Key for WorldBorder PDC. [DOUBLE]
    public static final String WORLD_BORDER_SIZE_KEY = "size"; // Key for WorldBorder PDC. [DOUBLE]

    public static final String GEM_IDENTIFIER_KEY = "gem"; // Used to identify whether an item is a Gem. [BOOLEAN]
}