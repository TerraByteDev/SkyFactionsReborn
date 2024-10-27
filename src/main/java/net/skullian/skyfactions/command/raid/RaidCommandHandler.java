package net.skullian.skyfactions.command.raid;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.command.CommandHandler;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.raid.cmds.RaidHelpCommand;
import net.skullian.skyfactions.command.raid.cmds.RaidResetCooldown;
import net.skullian.skyfactions.command.raid.cmds.RaidStartCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.meta.SimpleCommandMeta;
import org.incendo.cloud.paper.PaperCommandManager;
import org.stringtemplate.v4.compiler.STParser.templateAndEOF_return;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RaidCommandHandler implements CommandHandler {

    PaperCommandManager<CommandSourceStack> manager;
    AnnotationParser<CommandSourceStack> parser;
    Map<String, CommandTemplate> subcommands = new HashMap<>();

    public RaidCommandHandler() {
        this.manager = PaperCommandManager.builder()
                .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
                .buildOnEnable(SkyFactionsReborn.getInstance());

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
        register(new RaidHelpCommand(this));
        register(new RaidResetCooldown());
        register(new RaidStartCommand());
    }

    @Override
    public void register(CommandTemplate template) {
        parser.parse(template);
        subcommands.put(template.getName(), template);
    }
}
