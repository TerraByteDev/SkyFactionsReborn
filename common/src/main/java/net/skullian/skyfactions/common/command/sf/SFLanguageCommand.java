package net.skullian.skyfactions.common.command.sf;

import net.skullian.skyfactions.common.api.LanguageAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.user.SkyUser;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;

import java.util.ArrayList;
import java.util.List;

public class SFLanguageCommand extends CommandTemplate {
    @Override
    public String getParent() {
        return "sf";
    }

    @Override
    public String getName() {
        return "language";
    }

    @Override
    public String getDescription() {
        return "Change your language.";
    }

    @Override
    public String getSyntax() {
        return "/sf language <new_language>";
    }

    @Suggestions("language")
    public List<String> languageSuggestion(CommandContext<SkyUser> context, CommandInput input) {
        List<String> suggestions = new ArrayList<>();
        Messages.configs.values().forEach((config) -> {
            String languageName = config.getString("Messages.LANGUAGE_NAME");
            if (languageName != null) {
                suggestions.add(languageName);
            }
        });

        return suggestions;
    }

    @Command("language <new_language>")
    @Permission(value = {"skyfactions.sf.language"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            SkyUser player,
            @Argument(value = "new_language", description = "The new language.", suggestions = "language") String newLanguage
    ) {
        if (!hasPermission(player, true)) return;
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

        String fetchedNewLocale = LanguageAPI.getLocaleValue(newLanguage);
        if (fetchedNewLocale == null) {
            Messages.INVALID_LANGUAGE.send(player, locale);
        } else {
            SkyApi.getInstance().getPlayerAPI().modifyLocale(player, fetchedNewLocale, locale);
        }
    }

    @Override
    public List<String> permission() {
        return List.of("skyfactions.sf.language");
    }
}
