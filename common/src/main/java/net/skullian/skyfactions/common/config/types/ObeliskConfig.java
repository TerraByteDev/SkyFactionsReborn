package net.skullian.skyfactions.common.config.types;

import dev.dejvokep.boostedyaml.YamlDocument;
import lombok.Getter;
import lombok.Setter;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

import java.util.List;

@Getter
public enum ObeliskConfig {

    OBELISK_MATERIAL("block.material"),
    OBELISK_SPAWN_OFFSET("block.spawn-offset"),
    OBELISK_CUSTOM_MODEL_DATA("block.custom-model-data");

    @Setter
    private static YamlDocument config;
    private final String path;

    ObeliskConfig(String path) {
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

    public static SkyItemStack getLoadingItem(SkyUser player) {
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

        return SkyItemStack.builder()
                .material(Messages.LOADING_ITEM_MATERIAL.getString(locale))
                .lore(Messages.LOADING_ITEM_LORE.getStringList(locale))
                .displayName(Messages.LOADING_ITEM_TEXT.getString(locale))
                .build();
    }

    public boolean getBoolean() {
        return config.getBoolean(this.path);
    }
}
