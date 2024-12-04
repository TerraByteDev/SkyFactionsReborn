package net.skullian.skyfactions.paper.command.discord;

import net.skullian.skyfactions.common.gui.CooldownManager;
import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.paper.command.CommandHandler;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.paper.command.discord.cmds.LinkCommand;
import net.skullian.skyfactions.paper.command.discord.cmds.UnlinkCommand;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.meta.SimpleCommandMeta;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.paper.PaperCommandManager;

import java.util.HashMap;
import java.util.Map;

public class DiscordCommandHandler implements CommandHandler {

    LegacyPaperCommandManager<CommandSender> manager;
    AnnotationParser<CommandSender> parser;
    Map<String, CommandTemplate> subcommands = new HashMap<>();

    public DiscordCommandHandler() {


        this.manager = LegacyPaperCommandManager.createNative(
                SkyFactionsReborn.getInstance(),
                ExecutionCoordinator.simpleCoordinator()
        );
        this.manager.registerCommandPostProcessor(new CooldownManager.CooldownPostprocessor<>());

        this.parser = new AnnotationParser(
                manager,
                CommandSender.class,
                params -> SimpleCommandMeta.empty()
        );

        registerSubCommands(this.parser);
    }


    @Override
    public CommandHandler getHandler() {
        return this;
    }

    @Override
    public LegacyPaperCommandManager<CommandSender> getManager() {
        return this.manager;
    }

    @Override
    public Map<String, CommandTemplate> getSubCommands() {
        return this.subcommands;
    }

    @Override
    public void registerSubCommands(AnnotationParser<CommandSender> parser) {
        register(new LinkCommand(), parser);
        register(new UnlinkCommand(), parser);
    }

    @Override
    public void register(CommandTemplate template, AnnotationParser<?> parser) {
        parser.parse(template);
        subcommands.put(template.getName(), template);
    }

}
