package net.skullian.torrent.skyfactions.discord;

import lombok.extern.log4j.Log4j2;
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
import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import org.bukkit.entity.Player;
import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.constants.player.KingdomPlayer;
import org.kingdoms.data.KingdomsDataCenter;
import org.kingdoms.data.managers.KingdomManager;
import org.kingdoms.main.Kingdoms;

import java.sql.SQLException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Log4j2(topic = "SkyFactionsReborn")
public class DiscordHandler {

    public JDA JDA;
    private static TextChannel RAID_NOTIFICATION_CHANNEL;

    public Map<String, UUID> codes = new HashMap<>();
    public boolean enabled = false;

    public void initialiseBot() {
        try {
            if (!SkyFactionsReborn.configHandler.DISCORD_CONFIG.getBoolean("Discord.ENABLED")) return;

            JDA = JDABuilder.createDefault(SkyFactionsReborn.configHandler.DISCORD_CONFIG.getString("Discord.TOKEN"))
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


            String channelID = SkyFactionsReborn.configHandler.DISCORD_CONFIG.getString("Discord.RAID_CHANNEL");
            RAID_NOTIFICATION_CHANNEL = JDA.getTextChannelById(channelID);
            if (RAID_NOTIFICATION_CHANNEL == null) {
                throw new NullPointerException("Invalid notification channel ID. Check your config.");
            }
        } catch (InterruptedException error) {
            LOGGER.fatal("----------------------- DISCORD EXCEPTION -----------------------");
            LOGGER.fatal("There was an error while initialising the bot.");
            LOGGER.fatal("Please contact the devs.");
            LOGGER.fatal("----------------------- DISCORD EXCEPTION -----------------------");
            error.printStackTrace();
        }
    }

    public String createLinkCode(Player player) {
        String code;
        do {
            code = String.format("%04d", (int) (Math.random() * 10000));
        } while (codes.containsKey(code));

        System.out.println(code);
        System.out.println("put");
        codes.put(code, player.getUniqueId());

        return code;
    }

    public void disconnect() {
        try {
            JDA.shutdown();
            if (!JDA.awaitShutdown(Duration.ofSeconds(10))) {
                JDA.shutdownNow();
            }
        } catch (InterruptedException error) {
            LOGGER.fatal("----------------------- DISCORD EXCEPTION -----------------------");
            LOGGER.fatal("There was an error while stopping the bot.");
            LOGGER.fatal("Please contact the devs.");
            LOGGER.fatal("----------------------- DISCORD EXCEPTION -----------------------");
            error.printStackTrace();
        }
    }

    public void pingRaid(Player attacker, Player victim) {
            SkyFactionsReborn.db.getDiscordLink(victim).thenAccept(id -> {
                if (id != null) {
                    EmbedBuilder embedBuilder = new EmbedBuilder()
                            .setDescription(SkyFactionsReborn.configHandler.MESSAGES_CONFIG.getString("Messages.Discord.DISCORD_RAID_MESSAGE").replace("%attacker%", attacker.getName()))
                            .setThumbnail(SkyFactionsReborn.configHandler.DISCORD_CONFIG.getString("Discord.AVATAR_API").replace("%player%", attacker.getUniqueId().toString()));

                    RAID_NOTIFICATION_CHANNEL.sendMessage("<@" + id + ">").setEmbeds(embedBuilder.build()).queue();
                }
            }).exceptionally(ex -> {
                ex.printStackTrace();
                return null;
            });
    }
}
