package net.skullian.skyfactions.command.sf;

import java.util.HashMap;
import java.util.Map;

import net.skullian.skyfactions.util.CooldownManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.meta.SimpleCommandMeta;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
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
import org.incendo.cloud.paper.util.sender.PlayerSource;

public class SFCommandHandler implements CommandHandler {

    LegacyPaperCommandManager<CommandSender> manager;
    AnnotationParser<CommandSender> parser;
    Map<String, CommandTemplate> subcommands = new HashMap<>();

    public SFCommandHandler() {
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
        register(new SFHelpCommand(this), parser);
        register(new SFInfoCommand(), parser);
        register(new SFReloadCommand(), parser);
        register(new SFSyncCommand(), parser);
        register(new SFNPCReloadCommand(), parser);
        register(new SFNPCDisableCommand(), parser);
    }

    @Override
    public void register(CommandTemplate template, AnnotationParser<?> parser) {
        parser.parse(template);
        subcommands.put(template.getName(), template);
    }
}
