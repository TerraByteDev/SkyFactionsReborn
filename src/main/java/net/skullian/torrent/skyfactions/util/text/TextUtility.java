package net.skullian.torrent.skyfactions.util.text;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Nullable;
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

    public static boolean isEnglish(@Nullable CharSequence seq) {
        if (seq == null) {
            return false;
        } else {
            int length = seq.length();
            if (length == 0) {
                return false;
            } else {
                for (int i = 0; i < length; i++) {
                    char character = seq.charAt(i);

                    if (character != ' ' && character != '_' && !isEnglishDigitOrLetter(character)) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    public static boolean hasSymbols(@Nullable CharSequence seq) {
        if (seq == null) {
            return false;
        } else {
            int length = seq.length();
            if (length == 0) {
                return false;
            } else {
                for (int i = 0; i < length; i++) {
                    char character = seq.charAt(i);
                    if (character != ' ' && character != '_' && !Character.isLetterOrDigit(character)) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    public static boolean containsNumbers(@Nullable CharSequence seq) {
        if (seq == null) {
            return false;
        } else {
            int length = seq.length();
            if (length != 0) {
                for (int i = 0; i < length; i++) {
                    char character = seq.charAt(i);
                    if (Character.isDigit(character)) {
                        return true;
                    }
                }

            }
            return false;
        }
    }

    public static boolean isEnglishDigitOrLetter(char character) {
        return isEDigit(character) || isELetter(character);
    }

    public static boolean isEDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    public static boolean isELetter(char ch) {
        return ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z';
    }

}
