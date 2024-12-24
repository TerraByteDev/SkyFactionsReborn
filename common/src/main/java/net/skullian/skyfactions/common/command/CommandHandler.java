package net.skullian.skyfactions.common.command;

import net.skullian.skyfactions.common.command.discord.LinkCommand;
import net.skullian.skyfactions.common.command.discord.UnlinkCommand;
import net.skullian.skyfactions.common.command.faction.*;
import net.skullian.skyfactions.common.command.gems.*;
import net.skullian.skyfactions.common.command.island.*;
import net.skullian.skyfactions.common.command.raid.RaidHelpCommand;
import net.skullian.skyfactions.common.command.raid.RaidResetCooldown;
import net.skullian.skyfactions.common.command.raid.RaidStartCommand;
import net.skullian.skyfactions.common.command.runes.RunesBalanceCommand;
import net.skullian.skyfactions.common.command.runes.RunesGiveCommand;
import net.skullian.skyfactions.common.command.runes.RunesHelpCommand;
import net.skullian.skyfactions.common.command.runes.RunesResetCommand;
import net.skullian.skyfactions.common.command.sf.*;
import net.skullian.skyfactions.common.user.SkyUser;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotations.AnnotationParser;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface CommandHandler {

    Map<String, Map<String, CommandTemplate>> commands = new ConcurrentHashMap<>();

    CommandHandler getHandler();

    CommandManager<SkyUser> getManager();

    default Map<String, CommandTemplate> getSubCommands(String parent) {
        return commands.get(parent);
    }

    default Map<String, Map<String, CommandTemplate>> getSubCommands() { return commands; }

    default void registerSubCommands(AnnotationParser<SkyUser> parser) {
        register(new LinkCommand(), parser);
        register(new UnlinkCommand(), parser);

        register(new FactionBroadcastCommand(), parser);
        register(new FactionCreateCommand(), parser);
        register(new FactionDisbandCommand(), parser);
        register(new FactionDonateCommand(), parser);
        register(new FactionHelpCommand(), parser);
        register(new FactionInfoCommand(), parser);
        register(new FactionInviteCommand(), parser);
        register(new FactionLeaveCommand(), parser);
        register(new FactionMOTDCommand(), parser);
        register(new FactionRenameCommand(), parser);
        register(new FactionRequestJoinCommand(), parser);
        register(new FactionTeleportCommand(), parser);

        register(new GemsBalanceCommand(), parser);
        register(new GemsDepositCommand(), parser);
        register(new GemsGiveCommand(), parser);
        register(new GemsHelpCommand(), parser);
        register(new GemsPayCommand(), parser);
        register(new GemsWithdrawCommand(), parser);

        register(new IslandCreateCommand(), parser);
        register(new IslandDeleteCommand(), parser);
        register(new IslandHelpCommand(), parser);
        register(new IslandTeleportCommand(), parser);
        register(new IslandTrustCommand(), parser);
        register(new IslandUntrustCommand(), parser);
        register(new IslandVisitCommand(), parser);

        register(new RaidHelpCommand(), parser);
        register(new RaidResetCooldown(), parser);
        register(new RaidStartCommand(), parser);

        register(new RunesBalanceCommand(), parser);
        register(new RunesGiveCommand(), parser);
        register(new RunesHelpCommand(), parser);
        register(new RunesResetCommand(), parser);

        register(new SFHelpCommand(), parser);
        register(new SFInfoCommand(), parser);
        register(new SFLanguageCommand(), parser);
        register(new SFNPCDisableCommand(), parser);
        register(new SFNPCReloadCommand(), parser);
        register(new SFReloadCommand(), parser);
        register(new SFSyncCommand(), parser);
    }

    void register(CommandTemplate template, AnnotationParser<?> parser);

}