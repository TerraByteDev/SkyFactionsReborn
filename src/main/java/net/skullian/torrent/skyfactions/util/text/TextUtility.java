package net.skullian.torrent.skyfactions.util.text;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class TextUtility {

    public static String color (final String string) {
        return ColorAPI.process(string);
    }

    public static final ObjectMapper objectMapper = new ObjectMapper();

    public static String fromList(List<?> list) {
        if (list == null || list.isEmpty()) return null;
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            if (org.bukkit.ChatColor.stripColor(list.get(i).toString()).equals("")) builder.append("\n&r");
            else builder.append(list.get(i).toString()).append(i + 1 != list.size() ? "\n" : "");
        }


        return builder.toString();
    }

    public static String[] stringToArray(String arrayString) {
        try {
            return objectMapper.readValue(arrayString, String[].class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String arrayToString(String[] array) {
        try {
            return objectMapper.writeValueAsString(array);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
