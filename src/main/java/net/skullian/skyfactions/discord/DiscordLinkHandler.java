package net.skullian.skyfactions.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.config.types.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.awt.*;
import java.util.UUID;

public class DiscordLinkHandler extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("link-mc")) {
            String code = event.getOption("code").getAsString();
            UUID playerUUID = SkyFactionsReborn.discordHandler.codes.get(code);
            if (playerUUID != null) {

                OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID);


                SkyFactionsReborn.databaseHandler.registerDiscordLink(playerUUID, event.getUser().getId()).thenAccept(result -> {
                    if (player.isOnline()) {
                        Messages.DISCORD_LINK_SUCCESS.send(player.getPlayer(), "%discord_name%", event.getUser().getName());
                    }

                    event.reply("").setEmbeds(buildEmbed(Color.GREEN, SkyFactionsReborn.configHandler.MESSAGES_CONFIG.getString("Messages.Discord.DISCORD_LINK_SUCCESS_MESSAGE").replace("%player_name%", player.getName())).build()).queue();
                    SkyFactionsReborn.discordHandler.codes.remove(code);
                });
            } else {
                event.reply("").setEmbeds(buildEmbed(Color.RED, SkyFactionsReborn.configHandler.MESSAGES_CONFIG.getString("Messages.Discord.LINK_FAILED")).build()).queue();
            }
        }
    }

    private EmbedBuilder buildEmbed(Color color, String body) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(color)
                .setDescription(body);

        return embedBuilder;
    }
}
