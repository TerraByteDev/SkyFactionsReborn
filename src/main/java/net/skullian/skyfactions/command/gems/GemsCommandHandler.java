package net.skullian.skyfactions.command.gems;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.command.CommandHandler;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.gems.cmds.*;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.config.types.Settings;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.meta.SimpleCommandMeta;
import org.incendo.cloud.paper.PaperCommandManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GemsCommandHandler implements CommandHandler {

    PaperCommandManager<CommandSourceStack> manager;
    AnnotationParser<CommandSourceStack> parser;
    Map<String, CommandTemplate> subcommands = new HashMap<>();

    public GemsCommandHandler() {
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
        register(new GemsBalanceCommand());
        register(new GemsGiveCommand());
        register(new GemsHelpCommand(this));
        register(new GemsPayCommand());
        if (Settings.GEMS_CAN_WIDHTRAW.getBoolean()) {
            register(new GemsWithdrawCommand());
            register(new GemsDepositCommand());
        }
    }

    @Override
    public void register(CommandTemplate template) {
        parser.parse(template);
        subcommands.put(template.getName(), template);
    }


}
