package net.skullian.skyfactions.paper;

import net.skullian.skyfactions.common.SharedConstants;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class PaperSharedConstants {

    public static final NamespacedKey DEFENCE_IDENTIFIER_KEY = from(SharedConstants.DEFENCE_IDENTIFIER_KEY);
    public static final NamespacedKey DEFENCE_DATA_KEY = from(SharedConstants.DEFENCE_DATA_KEY);
    public static final NamespacedKey DEFENCE_DAMAGE_KEY = from(SharedConstants.DEFENCE_DAMAGE_KEY);
    public static final NamespacedKey DEFENCE_DAMAGE_MESSAGE_KEY = from(SharedConstants.DEFENCE_DAMAGE_MESSAGE_KEY);

    public static final NamespacedKey OBELISK_TYPE_KEY = from(SharedConstants.OBELISK_TYPE_KEY);
    public static final NamespacedKey OBELISK_OWNER_KEY = from(SharedConstants.OBELISK_OWNER_KEY);
    public static final NamespacedKey OBELISK_KEY = from(SharedConstants.OBELISK_KEY);

    public static final NamespacedKey WORLD_BORDER_DATA_KEY = from(SharedConstants.WORLD_BORDER_DATA_KEY);
    public static final NamespacedKey WORLD_BORDER_X_KEY = from(SharedConstants.WORLD_BORDER_X_KEY);
    public static final NamespacedKey WORLD_BORDER_Z_KEY = from(SharedConstants.WORLD_BORDER_Z_KEY);
    public static final NamespacedKey WORLD_BORDER_SIZE_KEY = from(SharedConstants.WORLD_BORDER_SIZE_KEY);

    public static final NamespacedKey GEM_IDENTIFIER_KEY = from(SharedConstants.GEM_IDENTIFIER_KEY);

    @NotNull
    private static NamespacedKey from(@NotNull String key) {
        return new NamespacedKey(SkyFactionsReborn.getInstance(), key);
    }
}
