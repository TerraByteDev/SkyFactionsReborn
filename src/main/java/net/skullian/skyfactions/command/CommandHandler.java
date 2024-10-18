package net.skullian.skyfactions.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.paper.PaperCommandManager;

import java.util.ArrayList;

public interface CommandHandler {
    CommandHandler getHandler();

    PaperCommandManager<CommandSourceStack> getManager();

    AnnotationParser<CommandSender> getParser();

    ArrayList<CommandTemplate> getSubCommands();

    default void register(CommandTemplate command) {
        getParser().parse((Object) command);
        getSubCommands().add(command);
    }

    void registerSubCommands();

}
