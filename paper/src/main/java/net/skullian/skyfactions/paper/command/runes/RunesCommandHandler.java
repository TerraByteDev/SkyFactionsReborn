package net.skullian.skyfactions.paper.command.runes;

import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.paper.command.CommandHandler;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.paper.command.runes.subcommands.RunesBalanceCommand;
import net.skullian.skyfactions.paper.command.runes.subcommands.RunesGiveCommand;
import net.skullian.skyfactions.paper.command.runes.subcommands.RunesHelpCommand;
import net.skullian.skyfactions.paper.command.runes.subcommands.RunesResetCommand;
import net.skullian.skyfactions.paper.util.CooldownManager;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.meta.SimpleCommandMeta;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

import java.util.HashMap;
import java.util.Map;

public class RunesCommandHandler implements CommandHandler {
    LegacyPaperCommandManager<CommandSender> manager;
    AnnotationParser<CommandSender> parser;
    Map<String, CommandTemplate> subcommands = new HashMap<>();

    public RunesCommandHandler() {
        this.manager = LegacyPaperCommandManager.createNative(
                SkyFactionsReborn.getInstance(),
                ExecutionCoordinator.simpleCoordinator()
        );
        this.manager.registerCommandPostProcessor(new CooldownManager.CooldownPostprocessor<>());

        this.parser = new AnnotationParser<>(
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
        register(new RunesGiveCommand(), parser);
        register(new RunesHelpCommand(this), parser);
        register(new RunesBalanceCommand(), parser);
        register(new RunesResetCommand(), parser);
    }

    @Override
    public void register(CommandTemplate template, AnnotationParser<?> parser) {
        parser.parse(template);
        subcommands.put(template.getName(), template);
    }
}
