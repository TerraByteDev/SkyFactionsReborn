package net.skullian.torrent.skyfactions.util.text.pattern;

import net.skullian.torrent.skyfactions.util.text.ColorAPI;

import java.util.regex.Matcher;

public class RainbowPattern implements MainPattern {

    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("<RAINBOW([0-9]{1,3})>(.*?)</RAINBOW>");

    public String process(String string) {
        Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            String saturation = matcher.group(1);
            String content = matcher.group(2);
            string = string.replace(matcher.group(), ColorAPI.rainbow(content, Float.parseFloat(saturation)));
        }
        return string;
    }


}
