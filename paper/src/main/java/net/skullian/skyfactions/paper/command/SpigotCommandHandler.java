package net.skullian.skyfactions.paper.command;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.command.CommandHandler;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.gui.CooldownManager;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.paper.SkyFactionsReborn;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.meta.SimpleCommandMeta;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SpigotCommandHandler implements CommandHandler {

    private final LegacyPaperCommandManager<SkyUser> manager;
    private final AnnotationParser<SkyUser> parser;

    public SpigotCommandHandler() {
        SenderMapper<CommandSender, SkyUser> senderMapper = SenderMapper.create(
                commandSender -> {
                    if (commandSender instanceof Player player) {
                        return SkyApi.getInstance().getUserManager().cloudFetch(player.getUniqueId(), commandSender, false);
                    }
                    return SkyApi.getInstance().getUserManager().cloudFetch(UUID.randomUUID(), commandSender, true);
                },
                sender -> (CommandSender) sender.getCommandSender()
        );

        this.manager = new LegacyPaperCommandManager<>(
                SkyFactionsReborn.getInstance(),
                ExecutionCoordinator.asyncCoordinator(),
                senderMapper
        );
        this.manager.registerCommandPostProcessor(new CooldownManager.CooldownPostprocessor<>());

        this.parser = new AnnotationParser<>(
                this.manager,
                SkyUser.class,
                params -> SimpleCommandMeta.empty()
        );

        registerSubCommands(this.parser);
    }

    @Override
    public CommandHandler getHandler() {
        return this;
    }

    @Override
    public CommandManager<SkyUser> getManager() {
        return manager;
    }

    @Override
    public void register(CommandTemplate template, AnnotationParser<?> parser) {
        parser.parse(template);
        commands.computeIfAbsent(template.getParent(), k -> new ConcurrentHashMap<>()).put(template.getName(), template);
    }
}
