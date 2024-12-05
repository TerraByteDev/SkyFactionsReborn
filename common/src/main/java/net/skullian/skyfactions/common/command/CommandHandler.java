package net.skullian.skyfactions.common.command;

import lombok.Getter;
import net.skullian.skyfactions.common.user.SkyUser;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotations.AnnotationParser;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface CommandHandler {

    Map<String, Map<String, CommandTemplate>> commands = new ConcurrentHashMap<>();

    CommandHandler getHandler();

    CommandManager<SkyUser> getManager();

    default Map<String, CommandTemplate> getSubCommands(String parent) {
        return commands.get(parent);
    }

    void registerSubCommands(AnnotationParser<SkyUser> parser);

    void register(CommandTemplate template, AnnotationParser<?> parser);

}