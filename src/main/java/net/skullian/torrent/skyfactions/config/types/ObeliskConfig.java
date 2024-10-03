package net.skullian.torrent.skyfactions.config.types;

import lombok.Getter;
import lombok.Setter;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import java.util.List;

@Getter
public enum ObeliskConfig {

    OBELISK_MATERIAL("Block.MATERIAL"),
    OBELISK_SPAWN_OFFSET("Block.SPAWN_OFFSET"),
    OBELISK_CUSTOM_MODEL_DATA("Block.CUSTOM_MODEL_DATA"),

    LOADING_ITEM_MATERIAL("Block.LOADING.MATERIAL"),
    LOADING_ITEM_TEXT("Block.LOADING.TEXT"),
    LOADING_ITEM_LORE("Block.LOADING.LORE");

    @Setter
    private static FileConfiguration config;
    private final String path;

    ObeliskConfig(String path) {
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

    public static ItemBuilder getLoadingItem() {
        ItemBuilder builder = new ItemBuilder(Material.getMaterial(LOADING_ITEM_MATERIAL.getString())).setDisplayName(TextUtility.color(LOADING_ITEM_TEXT.getString()));
        for (String str : LOADING_ITEM_LORE.getList()) {
            builder.addLoreLines(TextUtility.color(str));
        }

        return builder;
    }

    public boolean getBoolean() {
        return config.getBoolean(this.path);
    }
}
