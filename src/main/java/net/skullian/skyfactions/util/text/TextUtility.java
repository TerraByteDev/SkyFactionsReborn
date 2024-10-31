package net.skullian.skyfactions.util.text;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import dev.dejvokep.boostedyaml.YamlDocument;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.util.DependencyHandler;

public class TextUtility {

    public static Component color(String string, String locale, OfflinePlayer player, Object... replacements) {
        YamlDocument config = Messages.configs.getOrDefault(locale, Messages.getFallbackDocument());

        TagResolver[] resolvers = new TagResolver[(replacements.length / 2)];
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 >= replacements.length) break;
            resolvers[i - 1] = Placeholder.parsed(String.valueOf(replacements[i]), String.valueOf(replacements[i + 1]));
        }

        String prefix = config.getString("Messages." + Messages.SERVER_NAME.getPath());
        resolvers[resolvers.length] = Placeholder.parsed("server_name", prefix != null && !prefix.isEmpty() ? prefix : "");

        if (DependencyHandler.isEnabled("PlaceholderAPI")) string = PlaceholderAPI.setPlaceholders(player, string);
        return MiniMessage.miniMessage().deserialize(string, resolvers);
    }

    public static String legacyColor(String string, String locale, OfflinePlayer player, Object... replacements) {
        return LegacyComponentSerializer.legacySection().serialize(color(string, locale, player, replacements));
    }

    public static Component fromList(List<?> list, String locale, OfflinePlayer player, Object... replacements) {
        if (list == null || list.isEmpty()) return null;

        List<Component> components = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            components.add(color(list.get(i).toString(), locale, player, replacements));
            if (i + 1 != list.size()) components.add(Component.text(""));
        }

        return Component.join(JoinConfiguration.newlines(), components);
    }

    public static boolean isEnglish(@NotNull CharSequence seq) {
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

    /**
     * Check if a string contains blacklisted words.
     * Blacklisted words (regex) are configured in config.yml.
     *
     * @param player Player to check
     * @param name   String to check.
     * @return {@link Boolean}
     */
    public static boolean hasBlacklistedWords(Player player, String name) {
        boolean regexMatch = false;
        List<String> blacklistedNames = Settings.FACTION_CREATION_BLACKLISTED_NAMES.getList();

        for (String blacklistedName : blacklistedNames) {
            if (Pattern.compile(blacklistedName).matcher(name).find()) {
                regexMatch = true;
                break;
            }
        }

        if (regexMatch) {
            Messages.FACTION_NAME_PROHIBITED.send(player, player.locale().getLanguage());
            return true;
        } else {
            return false;
        }
    }

    public static boolean hasSymbols(@NotNull CharSequence seq) {
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

    public static boolean containsNumbers(@NotNull CharSequence seq) {
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

    public static String formatExtendedElapsedTime(long previousTime) {
        long currentTime = System.currentTimeMillis();

        Duration duration = Duration.ofMillis(currentTime - previousTime);
        long days = duration.toDaysPart();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        StringBuilder formattedTime = new StringBuilder();
        boolean hasContent = false;

        if (days > 0) {
            formattedTime.append(days).append(" day");
            if (days > 1) {
                formattedTime.append("s");
            }
            formattedTime.append(", ");
            hasContent = true;
        }
        if (hours > 0 || hasContent) {
            formattedTime.append(hours).append(" hour");
            if (hours > 1) {
                formattedTime.append("s");
            }
            formattedTime.append(", ");
            hasContent = true;
        }
        if (minutes > 0 || hasContent) {
            formattedTime.append(minutes).append(" minute");
            if (minutes > 1) {
                formattedTime.append("s");
            }
            formattedTime.append(", ");
            hasContent = true;
        }
        if (seconds > 0 || !hasContent) {
            formattedTime.append(seconds).append(" second");
            if (seconds > 1) {
                formattedTime.append("s");
            }
        }

        return formattedTime.toString();
    }

    public static List<String> toParts(String string) {
        String[] parts = string.split("/");
        List<String> partsList = Arrays.asList(parts);
        return partsList;
    }

}
