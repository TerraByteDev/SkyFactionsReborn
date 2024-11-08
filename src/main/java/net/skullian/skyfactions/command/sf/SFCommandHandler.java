package net.skullian.skyfactions.command.sf;

import java.util.HashMap;
import java.util.Map;

import net.skullian.skyfactions.util.CooldownManager;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.meta.SimpleCommandMeta;
import org.incendo.cloud.paper.PaperCommandManager;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.command.CommandHandler;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.sf.cmds.SFHelpCommand;
import net.skullian.skyfactions.command.sf.cmds.SFInfoCommand;
import net.skullian.skyfactions.command.sf.cmds.SFNPCDisableCommand;
import net.skullian.skyfactions.command.sf.cmds.SFNPCReloadCommand;
import net.skullian.skyfactions.command.sf.cmds.SFReloadCommand;
import net.skullian.skyfactions.command.sf.cmds.SFSyncCommand;

public class SFCommandHandler implements CommandHandler {

    PaperCommandManager<CommandSourceStack> manager;
    AnnotationParser<CommandSourceStack> parser;
    Map<String, CommandTemplate> subcommands = new HashMap<>();

    public SFCommandHandler() {
        this.manager = PaperCommandManager.builder()
                .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
                .buildOnEnable(SkyFactionsReborn.getInstance());
        this.manager.registerCommandPostProcessor(new CooldownManager.CooldownPostprocessor<>());

        this.parser = new AnnotationParser<>(
                manager,
                CommandSourceStack.class,
                params -> SimpleCommandMeta.empty()
        );

        registerSubCommands(this.parser);
    }

    @Override
    public CommandHandler getHandler() {
        return this;
    }

    @Override
    public PaperCommandManager<CommandSourceStack> getManager() {
        return this.manager;
    }

    @Override
    public AnnotationParser<CommandSourceStack> getParser() {
        return this.parser;
    }

    @Override
    public Map<String, CommandTemplate> getSubCommands() {
        return this.subcommands;
    }

    @Override
    public void registerSubCommands(AnnotationParser<CommandSourceStack> parser) {
        register(new SFHelpCommand(this));
        register(new SFInfoCommand());
        register(new SFReloadCommand());
        register(new SFSyncCommand());
        register(new SFNPCReloadCommand());
        register(new SFNPCDisableCommand());
    }

    @Override
    public void register(CommandTemplate template) {
        parser.parse(template);
        subcommands.put(template.getName(), template);
    }
}
