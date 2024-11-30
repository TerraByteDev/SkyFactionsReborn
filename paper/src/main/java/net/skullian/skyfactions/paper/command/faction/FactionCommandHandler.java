package net.skullian.skyfactions.paper.command.faction;

import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.paper.command.CommandHandler;
import net.skullian.skyfactions.paper.command.CommandTemplate;
import net.skullian.skyfactions.paper.command.faction.cmds.*;
import net.skullian.skyfactions.paper.config.types.Settings;
import net.skullian.skyfactions.paper.util.CooldownManager;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.meta.SimpleCommandMeta;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

import java.util.HashMap;
import java.util.Map;

public class FactionCommandHandler implements CommandHandler {

    LegacyPaperCommandManager<CommandSender> manager;
    AnnotationParser<CommandSender> parser;
    Map<String, CommandTemplate> subcommands = new HashMap<>();

    public FactionCommandHandler() {
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
        register(new FactionHelpCommand(this), parser);
        register(new FactionBroadcastCommand(), parser);
        register(new FactionCreateCommand(), parser);
        register(new FactionDonateCommand(), parser);
        register(new FactionInfoCommand(), parser);
        register(new FactionInviteCommand(), parser);
        register(new FactionLeaveCommand(), parser);
        register(new FactionMOTDCommand(), parser);
        register(new FactionRequestJoinCommand(), parser);
        register(new FactionTeleportCommand(), parser);
        register(new FactionDisbandCommand(), parser);
        if (Settings.FACTION_RENAME_ENABLED.getBoolean()) register(new FactionRenameCommand(), parser);
    }

    @Override
    public void register(CommandTemplate template, AnnotationParser<?> parser) {
        parser.parse(template);
        subcommands.put(template.getName(), template);
    }
}
