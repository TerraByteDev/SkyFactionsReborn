package net.skullian.skyfactions.api;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.skullian.skyfactions.config.types.GUIEnums;
import net.skullian.skyfactions.gui.data.GUIData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.data.PaginationItemData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GUIAPI {

    /**
     * No idea why you'd want to use this :/
     *
     * @param guiName GUI 'path' in the config folder. E.g. "confirmations/create_island" corresponds to the "confirmations/create_island.yml"
     * @return {@link GUIData} GUI's corresponding Data.
     * @throws IllegalArgumentException
     */
    public static GUIData getGUIData(String guiName) throws IllegalArgumentException {
        YamlDocument config = GUIEnums.configs.get("guis/" + guiName);
        if (config != null) {
            String guiTitle = config.getString("TITLE");
            String openSound = config.getString("OPEN_SOUND");
            int openPitch = config.getInt("OPEN_PITCH");
            List<String> layout = config.getStringList("LAYOUT");
            String[] layoutConv = layout.toArray(new String[0]);

            return new GUIData(guiTitle, openSound, openPitch, layoutConv);
        } else {
            throw new IllegalArgumentException("Invalid GUI Name: " + guiName);
        }
    }

    /**
     * Get a specific GUI's configured item datas.
     *
     * @param guiName GUI 'path' in the config folder. E.g. "confirmations/create_island" corresponds to the "confirmations/create_island.yml"
     * @param player  We only need this because there is a placeholder of %player_name%.
     * @return {@link List<ItemData>}
     * @throws IllegalArgumentException
     */
    public static List<ItemData> getItemData(String guiName, Player player) throws IllegalArgumentException {
        YamlDocument config = GUIEnums.configs.get("guis/" + guiName);
        if (config != null) {

            Section itemsConfig = config.getSection("ITEMS");
            List<ItemData> data = new ArrayList<>();
            for (String name : itemsConfig.getRoutesAsStrings(false)) {
                boolean isModel = name.equalsIgnoreCase("MODEL");
                Section itemData = itemsConfig.getSection(name);

                char charValue = !isModel ? itemData.getString("char").charAt(0) : "x".charAt(0);
                String material = itemData.getString("material");
                String text = itemData.getString("text").replace("%player_name%", player.getName());
                String sound = itemData.getString("sound");
                String texture = itemData.getString("skull");
                int pitch = itemData.getInt("pitch");
                List<String> lore = itemData.getStringList("lore");

                data.add(new ItemData(name, charValue, text, material, texture, sound, pitch, lore));
            }

            return data;
        } else {
            throw new IllegalArgumentException("Invalid GUI Name: " + guiName);
        }
    }

    /**
     * Get the configuration for paginated GUIs.
     *
     * @return {@link List<PaginationItemData>} Pagination UI configuration.
     * @throws RuntimeException
     */
    public static List<PaginationItemData> getPaginationData(Player player) throws RuntimeException {
        YamlDocument config = GUIEnums.configs.get("guis/pagination");
        if (config != null) {

            Section itemsConfig = config.getSection("ITEMS");
            List<PaginationItemData> data = new ArrayList<>();
            for (String name : itemsConfig.getRoutesAsStrings(false)) {
                Section itemData = itemsConfig.getSection(name);

                char charValue = itemData.getString("char").charAt(0);
                String material = itemData.getString("material");
                String texture = itemData.getString("skull");
                String itemName = itemData.getString("name").replace("%player_name%", player.getName());
                String sound = itemData.getString("sound");
                int pitch = itemData.getInt("pitch");
                String morePagesLore = itemData.getString("more_pages_lore");
                String noPagesLore = itemData.getString("no_pages_lore");

                data.add(new PaginationItemData(name, charValue, itemName, material, texture, sound, pitch, morePagesLore, noPagesLore));
            }

            return data;
        } else {
            throw new RuntimeException("Failed to get pagination data!");
        }
    }

    public static ItemStack createItem(ItemData data, UUID playerUUID) {
        ItemStack stack;
        if (data.getMATERIAL().equalsIgnoreCase("PLAYER_HEAD")) {
            String texture = data.getBASE64_TEXTURE();
            if (texture.equalsIgnoreCase("%player_skull%")) {
                stack = SkullAPI.getPlayerSkull(new ItemStack(Material.PLAYER_HEAD), playerUUID);
            } else {
                stack = SkullAPI.convertToSkull(new ItemStack(Material.PLAYER_HEAD), data.getBASE64_TEXTURE());
            }
        } else {
            stack = new ItemStack(Material.getMaterial(data.getMATERIAL()));
        }

        return stack;
    }

    public static ItemStack createItem(PaginationItemData data, UUID playerUUID) {
        ItemStack stack;
        if (data.getMATERIAL().equalsIgnoreCase("PLAYER_HEAD")) {
            String texture = data.getBASE64_TEXTURE();
            if (texture.equalsIgnoreCase("%player_skull%")) {
                stack = SkullAPI.getPlayerSkull(new ItemStack(Material.PLAYER_HEAD), playerUUID);
            } else {
                stack = SkullAPI.convertToSkull(new ItemStack(Material.PLAYER_HEAD), data.getBASE64_TEXTURE());
            }
        } else {
            stack = new ItemStack(Material.getMaterial(data.getMATERIAL()));
        }

        return stack;
    }
}
