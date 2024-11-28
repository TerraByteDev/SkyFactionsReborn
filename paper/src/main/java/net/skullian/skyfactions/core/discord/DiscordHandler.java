package net.skullian.skyfactions.core.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.core.module.modules.discord.DiscordConfig;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.util.ErrorUtil;
import net.skullian.skyfactions.core.util.SLogger;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class DiscordHandler {

    public JDA JDA;
    private static TextChannel RAID_NOTIFICATION_CHANNEL;

    public Map<String, UUID> codes = new HashMap<>();
    public boolean enabled = false;

    public void initialiseBot() {
        try {
            if (!DiscordConfig.ENABLED.getBoolean()) return;

            JDA = JDABuilder.createDefault(DiscordConfig.TOKEN.getString())
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_WEBHOOKS)
                    .setActivity(Activity.playing("SkyFactions"))
                    .build();

            JDA.addEventListener(new DiscordLinkHandler());

            JDA.awaitReady();
            JDA.updateCommands().addCommands(
                    Commands.slash("link-mc", "Links your discord account to your minecraft account for SkyFactions.")
                            .addOption(OptionType.STRING, "code", "Code that you were given on the SkyFactions server when running /link.", true)
            ).queue();

            enabled = true;


            String channelID = DiscordConfig.RAID_CHANNEL.getString();
            RAID_NOTIFICATION_CHANNEL = JDA.getTextChannelById(channelID);
            if (RAID_NOTIFICATION_CHANNEL == null) {
                throw new NullPointerException("Invalid notification channel ID. Check your config.");
            }
        } catch (InterruptedException error) {
            SLogger.fatal("----------------------- DISCORD EXCEPTION -----------------------");
            SLogger.fatal("There was an error while initialising the bot.");
            SLogger.fatal("Please contact the devs.");
            SLogger.fatal("----------------------- DISCORD EXCEPTION -----------------------");
            error.printStackTrace();
        }
    }

    public String createLinkCode(Player player) {
        String code;
        do {
            code = String.format("%04d", (int) (Math.random() * 10000));
        } while (codes.containsKey(code));

        codes.put(code, player.getUniqueId());

        return code;
    }

    public void disconnect() {
        if (!DiscordConfig.ENABLED.getBoolean()) return;
        try {
            JDA.shutdown();
            if (!JDA.awaitShutdown(Duration.ofSeconds(10))) {
                JDA.shutdownNow();
            }
        } catch (InterruptedException error) {
            SLogger.fatal("----------------------- DISCORD EXCEPTION -----------------------");
            SLogger.fatal("There was an error while stopping the bot.");
            SLogger.fatal("Please contact the devs.");
            SLogger.fatal("----------------------- DISCORD EXCEPTION -----------------------");
            error.printStackTrace();
        }
    }

    public void pingRaid(Player attacker, Player victim) {
        SpigotPlayerAPI.getPlayerData(victim.getUniqueId()).whenComplete((data, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(victim, "start your raid", "SQL_GET_DISCORD", ex);
                return;
            }

            if (!data.getDISCORD_ID().equals("none")) {
                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setDescription(Messages.DISCORD_RAID_MESSAGE.getString(victim.locale().getLanguage()).replace("attacker", attacker.getName()))
                        .setThumbnail(DiscordConfig.AVATAR_API.getString().replace("player", attacker.getUniqueId().toString()));

                RAID_NOTIFICATION_CHANNEL.sendMessage("<@" + data.getDISCORD_ID() + ">").setEmbeds(embedBuilder.build()).queue();
            }
        });
    }
}
