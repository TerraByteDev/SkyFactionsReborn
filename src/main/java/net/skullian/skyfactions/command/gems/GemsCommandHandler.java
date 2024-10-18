package net.skullian.skyfactions.command.gems;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.command.CommandHandler;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.gems.cmds.GemsBalanceCommand;
import net.skullian.skyfactions.command.gems.cmds.GemsGiveCommand;
import net.skullian.skyfactions.command.gems.cmds.GemsHelpCommand;
import net.skullian.skyfactions.command.gems.cmds.GemsPayCommand;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.meta.SimpleCommandMeta;
import org.incendo.cloud.paper.PaperCommandManager;

import java.util.ArrayList;

public class GemsCommandHandler implements CommandHandler {

    PaperCommandManager<CommandSourceStack> manager;
    AnnotationParser<CommandSender> parser;
    ArrayList<CommandTemplate> subcommands = new ArrayList<>();

    public GemsCommandHandler() {
        this.manager = PaperCommandManager.builder()
                .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
                .buildOnEnable(SkyFactionsReborn.getInstance());

        this.parser = new AnnotationParser(
                manager,
                CommandSender.class,
                paramas -> SimpleCommandMeta.empty()
        );
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
    public AnnotationParser<CommandSender> getParser() {
        return this.parser;
    }

    @Override
    public ArrayList<CommandTemplate> getSubCommands() {
        return this.subcommands;
    }

    @Override
    public void registerSubCommands() {
        register(new GemsBalanceCommand(this));
        register(new GemsGiveCommand(this));
        register(new GemsHelpCommand(this));
        register(new GemsPayCommand(this));
    }
}
