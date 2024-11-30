package net.skullian.skyfactions.paper.command.raid;

import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.paper.command.CommandHandler;
import net.skullian.skyfactions.paper.command.CommandTemplate;
import net.skullian.skyfactions.paper.command.raid.cmds.RaidHelpCommand;
import net.skullian.skyfactions.paper.command.raid.cmds.RaidResetCooldown;
import net.skullian.skyfactions.paper.command.raid.cmds.RaidStartCommand;
import net.skullian.skyfactions.paper.util.CooldownManager;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.meta.SimpleCommandMeta;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

import java.util.HashMap;
import java.util.Map;

public class RaidCommandHandler implements CommandHandler {

    LegacyPaperCommandManager<CommandSender> manager;
    AnnotationParser<CommandSender> parser;
    Map<String, CommandTemplate> subcommands = new HashMap<>();

    public RaidCommandHandler() {
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
        register(new RaidResetCooldown(), parser);
        register(new RaidStartCommand(), parser);
        register(new RaidHelpCommand(this), parser);
    }

    @Override
    public void register(CommandTemplate template, AnnotationParser<?> parser) {
        parser.parse(template);
        subcommands.put(template.getName(), template);
    }
}
