package net.skullian.skyfactions.command.faction;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.Getter;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.command.CommandHandler;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.faction.cmds.*;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.meta.SimpleCommandMeta;
import org.incendo.cloud.paper.PaperCommandManager;

import java.util.ArrayList;

public class FactionCommandHandler implements CommandHandler {

    PaperCommandManager<CommandSourceStack> manager;
    AnnotationParser<CommandSender> parser;
    @Getter
    ArrayList<CommandTemplate> subcommands = new ArrayList<>();

    public FactionCommandHandler() {
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
        register(new FactionBroadcastCommand());
        register(new FactionCreateCommand());
        register(new FactionDonateCommand());
        register(new FactionHelpCommand(this));
        register(new FactionInfoCommand());
        register(new FactionInviteCommand());
        register(new FactionLeaveCommand());
        register(new FactionMOTDCommand());
        register(new FactionRequestJoinCommand());
        register(new FactionTeleportCommand());
    }
}
