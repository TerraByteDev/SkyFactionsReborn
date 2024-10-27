package net.skullian.skyfactions.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.paper.PaperCommandManager;

import java.util.ArrayList;
import java.util.Map;

public interface CommandHandler {
    CommandHandler getHandler();

    PaperCommandManager<CommandSourceStack> getManager();

    AnnotationParser<CommandSourceStack> getParser();

    Map<String, CommandTemplate> getSubCommands();

    void registerSubCommands(AnnotationParser<CommandSourceStack> parser);

    void register(CommandTemplate template);

}
