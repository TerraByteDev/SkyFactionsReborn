package net.skullian.skyfactions.paper.command;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

import java.util.Map;

public interface CommandHandler {
    CommandHandler getHandler();

    LegacyPaperCommandManager<CommandSender> getManager();

    Map<String, CommandTemplate> getSubCommands();

    void registerSubCommands(AnnotationParser<CommandSender> parser);

    void register(CommandTemplate template, AnnotationParser<?> parser);

}
