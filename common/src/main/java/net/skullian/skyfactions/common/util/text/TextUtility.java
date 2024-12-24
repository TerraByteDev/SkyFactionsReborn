package net.skullian.skyfactions.common.util.text;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.user.SkyUser;
import org.jetbrains.annotations.NotNull;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class TextUtility {

    public static Component color(String string, String locale, SkyUser user, Object... replacements) {
        YamlDocument config = Messages.configs.getOrDefault(locale, Messages.getFallbackDocument());

        TagResolver[] resolvers = new TagResolver[(replacements.length != 0 ? (replacements.length / 2) + 1 : 1)];
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 >= replacements.length) break;
            int index = i / 2;

            resolvers[index] = Placeholder.parsed(String.valueOf(replacements[i]), String.valueOf(replacements[i + 1]));
        }

        String prefix = config.getString(Messages.SERVER_NAME.getPath());
        resolvers[replacements.length != 0 ? (replacements.length / 2) : 0] = Placeholder.parsed("server_name", prefix != null && !prefix.isEmpty() ? prefix : "");

        string = SkyApi.getInstance().getPlayerAPI().processText(user, string);

        return MiniMessage.miniMessage().deserialize(string, resolvers);
    }

    public static String legacyColor(String string, String locale, SkyUser user, Object... replacements) {
        return LegacyComponentSerializer.legacySection().serialize(color(string, locale, user, replacements));
    }

    public static Component fromList(List<?> list, String locale, SkyUser user, Object... replacements) {
        if (list == null || list.isEmpty()) return null;

        List<Component> components = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            components.add(color(list.get(i).toString(), locale, user, replacements));
        }

        return Component.join(JoinConfiguration.newlines(), components);
    }

    public static List<String> fromListLegacy(List<?> list, String locale, SkyUser user, Object... replacements) {
        if (list == null || list.isEmpty()) return null;

        List<String> components = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            components.add(legacyColor(list.get(i).toString(), locale, user, replacements));
        }

        return components;
    }

    public static List<Component> color(List<String> strings, String locale, SkyUser user, Object... replacements) {
        return strings.stream()
                .map(text -> color(text, locale, user, replacements))
                .toList();
    }

    public static List<String> legacyColor(List<String> strings, String locale, SkyUser user, Object... replacements) {
        return strings.stream()
                .map(text -> legacyColor(text, locale, user, replacements))
                .toList();
    }

    public static boolean isEnglish(@NotNull CharSequence seq) {
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

    public static boolean hasSymbols(@NotNull CharSequence seq) {
        int length = seq.length();
        if (length != 0) {
            for (int i = 0; i < length; i++) {
                char character = seq.charAt(i);
                if (character != ' ' && character != '_' && !Character.isLetterOrDigit(character)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if a string contains blacklisted words.
     * Blacklisted words (regex) are configured in config.yml.
     *
     * @param name   String to check.
     * @return {@link Boolean}
     */
    public static boolean containsBlockedPhrases(String name) {
        boolean regexMatch = false;
        List<String> blacklistedNames = Settings.BLACKLISTED_PHRASES.getList();

        for (String blacklistedName : blacklistedNames) {
            if (Pattern.compile(blacklistedName).matcher(name).find()) {
                regexMatch = true;
                break;
            }
        }

        return regexMatch;
    }

    public static boolean containsNumbers(@NotNull CharSequence seq) {
        return seq.chars().anyMatch(Character::isDigit);
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
            formattedTime.append(days).append(" day").append(days > 1 ? "s" : "").append(", ");
            hasContent = true;
        }
        if (hours > 0 || hasContent) {
            formattedTime.append(hours).append(" hour").append(hours > 1 ? "s" : "").append(", ");
            hasContent = true;
        }
        if (minutes > 0 || hasContent) {
            formattedTime.append(minutes).append(" minute").append(minutes > 1 ? "s" : "").append(", ");
            hasContent = true;
        }
        if (seconds > 0 || !hasContent) {
            formattedTime.append(seconds).append(" second").append(seconds > 1 ? "s" : "");
        }
        return formattedTime.toString();
    }

    public static Object[] convertFromString(String str) {
        if (str == null || str.isEmpty()) {
            return new Object[0];
        }
        return Arrays.stream(str.split(","))
                .map(part -> part.trim().replace("[", "").replace("]", ""))
                .toArray();
    }

    public static List<String> toParts(String string) {
        return Arrays.asList(string.split("/"));
    }

    @SafeVarargs
    public static List<String> merge(List<String>... lists) {
        return Arrays.stream(lists)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
