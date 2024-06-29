package net.skullian.torrent.skyfactions.api;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.util.gui.GUIData;
import net.skullian.torrent.skyfactions.util.gui.ItemData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GUIAPI {

    public static GUIData getGUIData(String guiName) throws IOException, InvalidConfigurationException {
        File file = new File(SkyFactionsReborn.getInstance().getDataFolder(), "/guis/" + guiName + ".yml");
        if (file.exists()) {

            YamlConfiguration config = new YamlConfiguration();
            config.load(file);

            String guiTitle = config.getString("TITLE");
            String openSound = config.getString("OPEN_SOUND");
            int openPitch = config.getInt("OPEN_PITCH");
            List<String> layout = config.getStringList("LAYOUT");
            String[] layoutConv = layout.toArray(new String[0]);

            return new GUIData(guiTitle, openSound, openPitch, layoutConv);
        }

        return null;
    }


    public static List<ItemData> getItemData(String guiName) throws IOException, InvalidConfigurationException {
        File file = new File(SkyFactionsReborn.getInstance().getDataFolder(), "/guis/" + guiName + ".yml");
        if (file.exists()) {

            file = new File(SkyFactionsReborn.getInstance().getDataFolder(), "/guis/" + guiName + ".yml");
            if (file.exists()) {

                YamlConfiguration config = new YamlConfiguration();
                config.load(file);

                ConfigurationSection itemsConfig = config.getConfigurationSection("ITEMS");
                List<ItemData> data = new ArrayList<>();
                for (String name : itemsConfig.getKeys(false)) {
                    ConfigurationSection itemData = itemsConfig.getConfigurationSection(name);

                    char charValue = itemData.getString("char").charAt(0);
                    String material = itemData.getString("material");
                    String text = itemData.getString("text");
                    String sound = itemData.getString("sound");
                    int pitch = itemData.getInt("pitch");
                    List<String> lore = itemData.getStringList("lore");

                    data.add(new ItemData(name, charValue, text, material, sound, pitch, lore));
                }

                return data;
            }
        }

        return new ArrayList<>();
    }
}
