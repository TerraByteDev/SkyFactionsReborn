package net.skullian.skyfactions.config.types;

import java.util.List;

import net.skullian.skyfactions.api.PlayerAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import dev.dejvokep.boostedyaml.YamlDocument;
import lombok.Getter;
import lombok.Setter;
import net.skullian.skyfactions.util.text.TextUtility;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

@Getter
public enum ObeliskConfig {

    OBELISK_MATERIAL("Block.MATERIAL"),
    OBELISK_SPAWN_OFFSET("Block.SPAWN_OFFSET"),
    OBELISK_CUSTOM_MODEL_DATA("Block.CUSTOM_MODEL_DATA");

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

    public static ItemBuilder getLoadingItem(Player player) {
        ItemBuilder builder = new ItemBuilder(Material.getMaterial(Messages.LOADING_ITEM_MATERIAL.getString(PlayerAPI.getLocale(player.getUniqueId())))).setDisplayName(TextUtility.legacyColor(Messages.LOADING_ITEM_TEXT.getString(PlayerAPI.getLocale(player.getUniqueId())), PlayerAPI.getLocale(player.getUniqueId()), player));
        for (String str : Messages.LOADING_ITEM_LORE.getStringList(PlayerAPI.getLocale(player.getUniqueId()))) {
            builder.addLoreLines(TextUtility.legacyColor(str, PlayerAPI.getLocale(player.getUniqueId()), player));
        }

        return builder;
    }

    public boolean getBoolean() {
        return config.getBoolean(this.path);
    }
}
