package net.skullian.torrent.skyfactions.api;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.config.ConfigTypes;
import net.skullian.torrent.skyfactions.util.gui.GUIData;
import net.skullian.torrent.skyfactions.util.gui.ItemData;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

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
            List<String> items = config.getStringList("ITEMS");

            return new GUIData(guiTitle, openSound, openPitch, layoutConv, items);
        }

        return null;
    }


    public static List<ItemData> getItemData(List<String> itemsList, String guiName) throws IOException, InvalidConfigurationException {
        File file = new File(SkyFactionsReborn.getInstance().getDataFolder(), "/guis/" + guiName + ".yml");
        if (file.exists()) {

            YamlConfiguration config = new YamlConfiguration();
            config.load(file);

            List<ItemData> list = new ArrayList<>();
            for (Object itemObj : itemsList) {
                Map<?,?> item = (Map<?,?>) itemObj;
                String itemName = (String) item.keySet().iterator().next();
                char charValue = (char) item.get("char");
                String material = (String) item.get("material");
                String text = (String) item.get("text");
                String sound = (String) item.get("sound");
                int pitch = (int) item.get("pitch");
                List<String> lore = (List<String>) item.get("lore");

                list.add(new ItemData(itemName, charValue, text, material, sound, pitch, lore));
            }

            return list;
        }

        return new ArrayList<>();
    }
}
