package net.skullian.skyfactions.common.api;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.skullian.skyfactions.common.config.types.Messages;

import java.util.Map;

public class LanguageAPI {

    public static String getLocaleValue(String language) {
        for (Map.Entry<String, YamlDocument> entry : Messages.configs.entrySet()) {
            if (entry.getValue().getString("Messages.LANGUAGE_NAME").equalsIgnoreCase(language)) {
                return entry.getKey();
            }
        }
        return null;
    }

}
