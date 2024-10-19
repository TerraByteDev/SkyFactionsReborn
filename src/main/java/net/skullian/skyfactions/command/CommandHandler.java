package net.skullian.skyfactions.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.paper.PaperCommandManager;

import java.util.ArrayList;

public interface CommandHandler {
    CommandHandler getHandler();

    PaperCommandManager<CommandSourceStack> getManager();

    AnnotationParser<CommandSourceStack> getParser();

    ArrayList<CommandTemplate> getSubCommands();

    void registerSubCommands(AnnotationParser<CommandSourceStack> parser);

    void register(CommandTemplate template);

}
