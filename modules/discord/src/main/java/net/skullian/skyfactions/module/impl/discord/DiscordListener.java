package net.skullian.skyfactions.module.impl.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.user.SkyUser;

import java.awt.*;
import java.util.Objects;
import java.util.UUID;

public class DiscordListener extends ListenerAdapter {

    private DiscordModule module;

    public DiscordListener(DiscordModule module) {
         this.module = module;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals(DiscordConfig.COMMAND_NAME.getString())) {
            String code = Objects.requireNonNull(event.getOption(DiscordConfig.COMMAND_OPTION_NAME.getString())).getAsString();
            UUID uuid = module.getUUIDFromCode(code);

            if (uuid != null) {
                SkyUser player = SkyApi.getInstance().getUserManager().getUser(uuid);
                SkyApi.getInstance().getCacheService().getEntry(uuid).setNewDiscordID(uuid, event.getUser().getId());

                if (player.isOnline()) {
                    Messages.DISCORD_LINK_SUCCESS.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "discord_name", event.getUser().getName());
                }

                event.reply("").setEmbeds(buildEmbed(Color.getColor(DiscordConfig.SUCCESS_COLOR.getString()), Messages.DISCORD_APP_LINK_SUCCESS.getString(Messages.getDefaulLocale()).replace("player_name", player.getName())).build()).queue();
                module.onSuccessfulLink(code);
            } else {
                event.reply("").setEmbeds(buildEmbed(Color.getColor(DiscordConfig.ERROR_COLOR.getString()), Messages.DISCORD_APP_LINK_FAILED.getString(Messages.getDefaulLocale())).build()).queue();
            }
        }
    }

    private EmbedBuilder buildEmbed(Color color, String body) {
        return new EmbedBuilder()
                .setColor(color)
                .setDescription(body);
    }
}
