package net.skullian.torrent.skyfactions.util.text.pattern;

import net.skullian.torrent.skyfactions.util.text.ColorAPI;

import java.awt.*;
import java.util.regex.Matcher;

public class GradientPattern implements MainPattern {

    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\{#([A-Fa-f0-9]{6})[>}](.*?)[<{]/#([A-Fa-f0-9]{6})}");

    public String process(String string) {
        Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            String start = matcher.group(1);
            String end = matcher.group(3);
            String content = matcher.group(2);
            string = string.replace(matcher.group(), ColorAPI.color(content, new Color(Integer.parseInt(start, 16)), new Color(Integer.parseInt(end, 16))));
        }
        return string;
    }

}
