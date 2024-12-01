package net.skullian.skyfactions.paper.module.impl.discord;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.ConfigHandler;
import net.skullian.skyfactions.common.config.types.ConfigTypes;
import net.skullian.skyfactions.common.module.SkyModuleManager;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.paper.module.SkyModule;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DiscordModule implements SkyModule {

    public DiscordModule() {}

    private JDA JDA;
    private TextChannel RAID_NOTIFICATION_CHANNEL;

    private Map<String, UUID> codes = new HashMap<>();

    private List<PresenceData> presenceData;
    private int currentPresence = 0;
    private ScheduledExecutorService scheduler;

    @Override
    public void onEnable() throws Exception {
        this.presenceData = fetchPresenceData();

        this.JDA = JDABuilder.createDefault(DiscordConfig.TOKEN.getString())
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_WEBHOOKS)
                .build();

        this.JDA.addEventListener(new DiscordListener());
        this.JDA.awaitReady();

        startPresenceScheduler();
        registerCommands();
        fetchRaidChannel();
    }

    @Override
    public void onReload() {
        loadConfig();
        fetchRaidChannel();
    }

    @Override
    public void onDisable() {
        if (scheduler != null && !scheduler.isShutdown()) this.scheduler.shutdown();
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

    @Override
    public boolean shouldEnable() {
        return DiscordConfig.ENABLED.getBoolean();
    }

    private void registerCommands() {
        JDA.updateCommands().addCommands(
                Commands.slash(DiscordConfig.COMMAND_NAME.getString(), DiscordConfig.COMMAND_DESCRIPTION.getString())
                        .addOption(OptionType.STRING, DiscordConfig.COMMAND_OPTION_NAME.getString(), DiscordConfig.COMMAND_OPTION_DESCRIPTION.getString(), true)
        ).queue();
    }

    private List<PresenceData> fetchPresenceData() {
        Section section = DiscordConfig.getConfig().getSection("Discord.PRESENCE");
        if (section == null) return Collections.emptyList();

        return section.getRoutesAsStrings(false).stream()
                .map(data -> {
                    Section activity = section.getSection(data);
                    return new PresenceData(
                            activity.getString("STATUS"),
                            activity.getString("TYPE"),
                            activity.getString("DESCRIPTION")
                    );
                }).collect(Collectors.toList());
    }

    private void startPresenceScheduler() {
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.scheduler.scheduleAtFixedRate(() -> {
             if (presenceData.isEmpty()) return;

             PresenceData presence = presenceData.get(currentPresence);
             JDA.getPresence().setPresence(
                     OnlineStatus.valueOf(presence.getSTATUS()),
                     Activity.of(Activity.ActivityType.valueOf(presence.getTYPE()), presence.getDESCRIPTION())
             );

             this.currentPresence = (currentPresence + 1) % presenceData.size();
        }, 0, DiscordConfig.PRESENCE_INTERVAL.getInt(), TimeUnit.SECONDS);
    }

    private void fetchRaidChannel() {
        String channelID = DiscordConfig.RAID_CHANNEL.getString();
        this.RAID_NOTIFICATION_CHANNEL = this.JDA.getTextChannelById(channelID);
        if (RAID_NOTIFICATION_CHANNEL == null) {
            throw new IllegalArgumentException("Invalid Discord raid notification channel ID. Please check your discord.yml configuration.");
        }
    }

    public void loadConfig() {

        SkyApi.getInstance().getConfigHandler().registerFile(ConfigTypes.DISCORD, new ConfigHandler("discord"));
        DiscordConfig.setConfig(SkyApi.getInstance().getConfigHandler().getFile(ConfigTypes.DISCORD).getConfig());
    }

    public UUID getUUIDFromCode(String code) {
        return this.codes.get(code);
    }

    public void onSuccessfulLink(String code) {
        this.codes.remove(code);
    }

    public static DiscordModule getInstance() {
        return (DiscordModule) SkyModuleManager.getEnabledModules().get(DiscordModule.class.getName());
    }
}
