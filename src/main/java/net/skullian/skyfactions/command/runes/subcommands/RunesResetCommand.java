package net.skullian.skyfactions.command.runes.subcommands;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.IslandAPI;
import net.skullian.skyfactions.api.RunesAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.util.ErrorUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Command("runes")
public class RunesResetCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "reset";
    }

    @Override
    public String getDescription() {
        return "Reset a player's or Faction's runes.";
    }

    @Override
    public String getSyntax() {
        return "/runes reset <type> <player/faction>";
    }

    @Suggestions("resetTypeSelection")
    public List<String> selectionSuggestion(CommandContext<CommandSourceStack> context, CommandInput input) {
        return List.of("player", "faction");
    }

    @Suggestions("playerFactionName")
    public List<String> suggestPlayerOrFaction(CommandContext<CommandSourceStack> context, CommandInput input) {
        if (input.input().startsWith("runes reset player")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        } else if (input.input().startsWith("runes reset faction")) {
            return new ArrayList<>(FactionAPI.factionNameCache.keySet());
        }

        return List.of();
    }

    @Command("reset <type> <playerFactionName>")
    @Permission(value = {"skyfactions.runes.reset"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            CommandSender sender,
            @Argument(value = "type", suggestions = "giveTypeSelection") String type,
            @Argument(value = "playerFactionName", suggestions = "playerFactionName") String playerFactionName
    ) {
        if ((sender instanceof Player) && !CommandsUtility.hasPerm((Player) sender, permission(), true)) return;
        String locale = sender instanceof Player ? ((Player) sender).locale().getLanguage() : Messages.getDefaulLocale();


    }



    @Override
    public List<String> permission() {
        return List.of("skyfactions.runes.reset");
    }
}
