package net.skullian.skyfactions.common.api;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.skullian.skyfactions.common.config.types.GUIEnums;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.gui.data.GUIData;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.PaginationItemData;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GUIAPI {

    /**
     * No idea why you'd want to use this :/
     *
     * @param guiName GUI 'path' in the config folder. E.g. "confirmations/create_island" corresponds to the "confirmations/create_island.yml"
     * @param user Player in question. This is so that the correct data for the player's locale can be fetched.
     *
     * @return {@link GUIData} GUI's corresponding Data.
     */
    public static GUIData getGUIData(String guiName, SkyUser user) throws IllegalArgumentException {
        YamlDocument config = GUIEnums.configs.getOrDefault(SkyApi.getInstance().getPlayerAPI().getLocale(user.getUniqueId()), getFallbackLanguage()).get(guiName);
        if (config != null) {
            String guiTitle = config.getString("TITLE").replace("<player_name>", user.getName());
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
     * @param user  We only need this because there is a placeholder of player_name. Also for locale crap.
     *
     * @return {@link List<ItemData>}
     */
    public static List<ItemData> getItemData(String guiName, SkyUser user) throws IllegalArgumentException {
        YamlDocument config = GUIEnums.configs.getOrDefault(SkyApi.getInstance().getPlayerAPI().getLocale(user.getUniqueId()), getFallbackLanguage()).get(guiName);
        if (config != null) {

            Section itemsConfig = config.getSection("ITEMS");
            List<ItemData> data = new ArrayList<>();
            for (String name : itemsConfig.getRoutesAsStrings(false)) {
                boolean isModel = name.equalsIgnoreCase("MODEL");
                Section itemData = itemsConfig.getSection(name);

                char charValue = !isModel ? itemData.getString("char").charAt(0) : "x".charAt(0);
                String material = itemData.getString("material");
                String text = itemData.getString("text").replace("<player_name>", user.getName());
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
     *
     */
    public static List<PaginationItemData> getPaginationData(SkyUser user) throws RuntimeException {
        YamlDocument config = GUIEnums.configs.getOrDefault(SkyApi.getInstance().getPlayerAPI().getLocale(user.getUniqueId()), getFallbackLanguage()).get("pagination");
        if (config != null) {

            Section itemsConfig = config.getSection("ITEMS");
            List<PaginationItemData> data = new ArrayList<>();
            for (String name : itemsConfig.getRoutesAsStrings(false)) {
                Section itemData = itemsConfig.getSection(name);

                char charValue = itemData.getString("char").charAt(0);
                String material = itemData.getString("material");
                String texture = itemData.getString("skull");
                String itemName = itemData.getString("name").replace("<player_name>", user.getName());
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

    private static Map<String, YamlDocument> getFallbackLanguage() {
        return GUIEnums.configs.get(Settings.DEFAULT_LANGUAGE.getString());
    }

    public static SkyItemStack createItem(ItemData data, UUID playerUUID) {
        return create(data.getMATERIAL(), data.getBASE64_TEXTURE(), playerUUID);
    }

    public static SkyItemStack createItem(PaginationItemData data, UUID playerUUID) {
        return create(data.getMATERIAL(), data.getBASE64_TEXTURE(), playerUUID);
    }

    private static SkyItemStack create(String material, String texture, UUID playerUUID) {
        SkyItemStack.SkyItemStackBuilder stack = SkyItemStack.builder();
        if (material.equalsIgnoreCase("PLAYER_HEAD")) {
            if (texture.equalsIgnoreCase("<player_skull>")) {
                stack = SkyApi.getInstance().getPlayerAPI().getPlayerSkull(stack.material("PLAYER_HEAD"), playerUUID);
            } else {
                stack = SkyApi.getInstance().getPlayerAPI().convertToSkull(stack.material("PLAYER_HEAD"), texture);
            }
        } else {
            stack = stack.material(material);
        }

        return stack.build();
    }
}
