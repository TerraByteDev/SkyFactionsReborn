package net.skullian.skyfactions.common.config.impl;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.NameFormatters;
import de.exlll.configlib.YamlConfigurationProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldMayBeFinal")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Configuration
public class Messages {
    private static Messages instance;

    private static final String HEADER = """
            #      __  ___
            #    /  |/  /__  ______________ _____ ____  _____
            #   / /|_/ / _ \\/ ___/ ___/ __ `/ __ `/ _ \\/ ___/
            #  / /  / /  __(__  |__  ) /_/ / /_/ /  __(__  )
            # /_/  /_/\\___/____/____/\\__,_/\\__, /\\___/____/
            #                             /____/
            
            # =============== MESSAGE CONFIG =============== #
            
            # Coloring follows https://docs.terrabytedev.com/skyfactions/configuration/languages.html#text-colours-rgb
            """;

    private static final YamlConfigurationProperties CONFIGURATION_PROPERTIES = YamlConfigurationProperties.newBuilder()
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE)
            .charset(StandardCharsets.UTF_8)
            .header(HEADER)
            .build();

    @Comment({
            "\nUsed in the /sf language tab complete to allow players to choose available",
            "languages other than auto-detected ones. Avoid changing this."
    })
    private String languageName = "English";

    @Comment({
            "\nThe name of your server.",
            "You can use this in all other messages via the '<server_name>' placeholder."
    })
    private String serverName = "<gray>[<reset><gradient:#0083FF:#00FFC7><bold>SkyFactions</gradient><reset><gray>]";

    @Comment("Sent when a player tries to run a command they do not have permission for.")
    private String permissionDeny = "<server_name> <red><bold>Hey!<reset><gray> You are not allowed to execute this command.";
    @Comment("Message sent when the plugin is being reloaded.")
    private String reloading = "<server_name><reset><gray> Reloading SkyFactions, please wait...";
    @Comment("Message sent when plugin is finished reloading.")
    private String reloaded = "<server_name><reset><green> Plugin reloaded!";
    @Comment("When the player tries to use a command or item that is on cooldown (configured in config.yml!).")
    private String cooldown = "<server_name><reset><gray> You must wait <red><bold><cooldown><reset><gray> seconds before continuing.";
    @Comment("Sent when the player uses a command with improper syntax.")
    private String incorrectUsage = "<server_name><reset><gray> Incorrect usage of command. Usage: <red><bold><usage>";
    @Comment("Sent when a command is ran on an unknown player (when applicable).")
    private String unknownPlayer = "<server_name><reset><gray> Unknown player: <red><bold><player>";
    @Comment("Sent when the plugin is processing a request. Everything is fully async so responses aren't guaranteed to be immediate.")
    private String pleaseWait = "<server_name><reset><gray> <operation>, please wait...";
    @Comment("Sent when a player tries to do text-related actions with blacklisted words. (e.g. faction name, MOTD, broadcast, etc).")
    private String blacklistedPhrase = "<server_name><reset><red>Your message contains blacklisted words.";

    @Comment({"\nSent when /sf sync is ran."})
    private String syncRunning = "<server_name><reset><yellow> Syncing cached data with the database. <gray>This may take a while!";
    @Comment("Sent when /sf sync is ran and completes.")
    private String syncSuccess = "<server_name><reset><green> Successfully synced cached data with the database!";

    @Comment({
            "\nSent when an error occurs when trying to execute a command or task.",
            "Placeholders: <operation> - what was happening / <debug> - Debug code to be used in accordance to https://docs.terrabytedev.com/skyfactions/errors-and-debugging."
    })
    private String error = "<server_name><reset><gray> Something went wrong when trying to <operation>. Please try again later. (<red><debug><gray>)";


    public record Commands(
            @Comment("Sent at the start and the bottom of the help command response.")
            String commandHead,

            @Comment({
                "\nPossible placeholders: <command_syntax> - Syntax of Command (e.g. \"/<command> help\") // <command_name> - Command Name (e.g. \"help\") // <command_description> - Description of Command.",
                "displayed when user uses /<command> help (or just does /<command>, which routes to /<command> help). It outlines all messages."
            })
            String commandInfo,

            @Comment("\nSent when a player runs a command and there are no commands to show (e.g. they don't have perms to see them).")
            String noCommandsFound
    ) {}

    @Comment("\n/sf language configurations")
    private Commands commands = new Commands(
            "<gray>------------- <reset><server_name><reset><gray> -------------",
            "<gradient:#15FB08:#00B0CA><command_syntax></gradient><reset><gold> - <dark_aqua><command_description>",
            "<red><bold>No commands found!"
    );

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Configuration
    public static class Islands {
        @Comment("Sent when a player tries to create an island when they already have one.")
        private String islandCreationDeny = "<server_name> <reset><red><bold>Hey! <reset><gray>You already have an island!";

        @Comment("\nSent when the player creates an island, and it is being created.")
        private String creatingMessage = "<server_name><reset><yellow> Creating your island, please wait...";

        @Comment("\nSent when the island has been successfully created.")
        private String createdMessage = "<server_name><reset><green><bold> Your island has been created!";

        @Comment("\nSent when the player tries to run a command related to islands when they do not have one.")
        private String noIsland = "<server_name> <red><bold>Hey!<reset><gray> You do not have an island!";

        @Comment("\nSent when a player in question does not have an island (but someone tries to do something such as /runes give)")
        private String playerHasNoIsland = "<server_name> <red><bold>Hey!<reset><gray> That player does not have an island!";

        @Comment("\nSent when the player runs /island delete confirm and the deletion is processing.")
        private String deletionProcessing = "<server_name><reset><yellow> Deleting your island, please wait...";

        @Comment("Sent when the player initially runs /island delete, and has to rerun the command below.")
        private String confirmDeletion = "<server_name><reset><gray> Please run<red><bold> /island delete confirm<reset><gray> to confirm deletion of your island.<newline><red><italic>This will wipe your inventory, enderchest, runes and gems!";

        @Comment("Sent when the player runs /island delete confirm and the island has been removed.")
        private String deletionSuccess = "<server_name><reset><gray> Your island has been successfully deleted.";

        @Comment("Sent when the player runs /island delete confirm before they run /island delete (2 step!)")
        private String deletionBlock = "<server_name><red><bold> Hey!<reset><gray> You must run <red><bold>/island delete<reset><gray> before using this command.";

        @Comment({"\nSent when a player successfully trusts another player to visit their island.",
                "Placeholders: <player> - Name of player who is trusted."})
        private String trustSuccess = "<server_name><green> Successfully trusted <player>!";

        @Comment("Sent when a player tries to trust a player that is already trusted.")
        private String alreadyTrusted = "<server_name><reset><red> You have already trusted that player!";

        @Comment({"\nSent when a player successfully untrusts a player.",
                "Placeholders: <player> - Untrusted player."})
        private String untrustSuccess = "<server_name><green> Successfully untrusted <player>.";

        @Comment("Sent when a player tries to untrust a player that is not trusted already.")
        private String untrustFailure = "<server_name><red> That player is not trusted by you!";

        @Comment("\nSent while the visit is processing.")
        private String visitProcessing = "<server_name><yellow> Teleporting, please wait...";

        @Comment("Sent when a player tries to visit a player's island without being trusted.")
        private String visitNotTrusted = "<server_name><red><bold> Hey!<reset><gray> You must be trusted to visit that island!";

        @Comment("Sent when a player tries to visit a player that has no island.")
        private String visitNoIsland = "<server_name><red><bold> Hey!<reset><gray> That player does not have an island!";

        @Comment("Sent when a player tries to visit a player that is participating in a raid.")
        private String visitInRaid = "<server_name><red><bold> Hey!<reset><gray> That player is raiding/being raided.";

        @Comment("\nSent if the player tries to use nether portals (when disabled).")
        private String netherPortalsBlocked = "<server_name><red><bold> Hey!<reset><gray> You cannot use nether portals here!";

        @Comment("\nSent when the player tries to teleport to their faction / player island when they are already on it.")
        private String alreadyOnIsland = "<server_name><red><bold> Hey!<reset><gray> You are already on your island!";

        @Comment({"\nSent when a player tries to /island visit another, but they are already on their island.",
                "Placeholders: <player> - Name of the player they tried to visit."})
        private String visitAlreadyOnIsland = "<server_name><red><bold> Hey!<reset><gray> You are already on <player>'s island!";
    }

    public record DiscordMessages(
            @Comment({"Sent to the player when they request to link (runs /link).",
                    "Placeholders: <code> - Code to run in the discord."})
            String linkPrompt,

            @Comment({"\nMessage content of the embed sent when a player is being raided.",
                    "Placeholders: <attacker> - Name of attacker."})
            String discordRaidMessage,

            @Comment({"\nMessage sent to the player INGAME when linking is successful.",
                    "Placeholders: <discord_name> - Username (tag) of the discord user you link to."})
            String linkSuccessMessage,

            @Comment({"\nMessage sent to the user IN DISCORD when linking is successful. (IS AN EMBED!)",
                    "Placeholders: <player_name> - Name of ingame player you link to."})
            String discordLinkSuccessMessage,

            @Comment("\nSent when an error occurs when linking. (IS AN EMBED!)")
            String discordLinkError,

            @Comment({"\nSent when the user is already linked (when discord command is run) (IS AN EMBED!)",
                    "Placeholders: <player_name> - Name of ingame player you are already linked to."})
            String discordAlreadyLinked,

            @Comment({"\nSent when the user is already linked (when ingame command is run) (IS AN EMBED!)",
                    "Placeholders: <discord_name> - Username (tag) of the discord user you are already linked to."})
            String alreadyLinked,

            @Comment("\nSent in Discord when the code is invalid. (IS AN EMBED!).")
            String linkFailed,

            @Comment("\nSent when the player tries to unlink (runs /unlink) when they are not unlinked.")
            String notLinked,

            @Comment("\nSent when the player successfully unlinks their Discord account.")
            String unlinkSuccess
    ) {}

    @Comment({
            "\nUsed in the discord module.",
            "We only keep the message configurations for modules in here because it keeps all the locale-based configurations in one place."
    })
    private DiscordMessages discordMessages = new DiscordMessages(
            "<server_name> <reset><yellow> Run '<red><bold>/link-mc <code><reset><yellow>' in the discord to link your account!",
            "`❗` You are being raided by **<attacker>**!\n\n`⚔️` Defend thy honour!",
            "<server_name> <green><bold>Success!<reset><gray> You are now linked to <discord_name>",
            "`✅` Success! You are now linked to <player_name>",
            "`❌` Oops! Looks like something went wrong when trying to link your account.\nPlease try again later.",
            "`❌` You are already linked to <player_name>",
            "<server_name> <red><bold>Hey! <reset><gray>You are already linked to <discord_name>!",
            "`❓` The code you entered is invalid. Please try again.",
            "<server_name> <red><bold>Hey! <reset><gray>You are not linked to a Discord account. Run <red><bold>/link<reset><gray> to get started!",
            "<server_name><reset><gray> You have unlinked your discord account. You will no longer receive raid notifications on Discord."
    );

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Configuration
    public static class NPC {

        @Comment("Sent when a player tries to access someone else's NPC (e.g. when visiting).")
        private String npcAccessDeny = "<server_name><red><bold> Hey!<reset><gray> You do not have access to this NPC!";

        @Comment("\nSent when a player runs /sf updatenpcs.")
        private String npcReloading = "<server_name><yellow> Reloading NPCs, please wait...";

        @Comment({"\nSent after a player runs /sf updatenpcs, once it has finished.",
                "Placeholders: <count> - How many NPCs were updated"})
        private String npcReloaded = "<server_name><green> Finished updating NPCs. <gray>(<count> NPCs affected)";

        @Comment("\nSent when an admin runs /sf disablenpcs.(<count> NPCs removed).")
        private String npcDisabling = "<server_name><yellow> Disabling NPCs, please wait...";

        @Comment({"\nSent after all NPCs were disabled.",
                "Placeholders: <count> - Amount of NPCs disabled."})
        private String npcDisabled = "<server_name><green> Finished disabling NPCs. <gray>(<count> NPCs removed)";

        @Comment({"\nControl what happens when NPCs are interacted with.",
                "See https://docs.terrabytedev.com/skyfactions/mechanics/npcs.html#actions"})
        private NpcActions actions = new NpcActions();

        @Getter
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        @Configuration
        public static class NpcActions {
            @Comment("Actions(s) to run when the NPC on player islands are interacted with.")
            private List<String> playerIslands = List.of("[console] tell <player_name> You clicked me!");

            @Comment("\nAction(s) to run when the NPC on faction islands are interacted with.")
            private List<String> factionIslands = List.of("[console] tell <player_name> You clicked me!");
        }

    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Configuration
    public static class Raiding {
        @Comment("Display Name of the raid confirmation GUI item (after player executes /raid)")
        private String raidConfirmationName = "<green><bold>Confirm";

        @Comment({"Lore of the raid confirmation GUI item. Can be multiple lines.",
                "Placeholders: <raid_cost> - Cost in Gems of the raid."})
        private List<String> raidConfirmationLore = List.of(
                "<green><italic>This will cost you <raid_cost> Gems.",
                "<red>You will not be able to raid for 3 hours."
        );

        @Comment("Display Name of the raid cancel GUI item (after player executes /raid)")
        private String raidCancelName = "<red><bold>Cancel";

        @Comment("\nSent when the player initiates a raid (assuming there are raidable players, has enough gems, etc.)")
        private String startingRaid = "<server_name> <reset><yellow>Starting your raid, please wait...";

        @Comment({"\nSent to the player if they try start a raid when on cooldown.",
                "Placeholders: <cooldown> - Cooldown left."})
        private String raidOnCooldown = "<server_name> <red><bold>Hey!<reset><gray> You are on cooldown for <yellow><bold><cooldown>.";

        @Comment({"\nSent to the player if they do not have sufficient gems to start a raid.",
                "Placeholders: <raid_cost> - How many gems to raid."})
        private String raidInsufficientGems = "<server_name> <red><bold>Hey!<reset><gray> You do not have enough gems to raid. You need <raid_cost> gems to raid.";

        @Comment("\nSent when there are no raidable players.")
        private String noPlayers = "<server_name> <reset><red>There are no players that can be raided! Try again later.";

        @Comment("\nSent to the player when the raid is processing (preparing).")
        private String raidProcessing = "<server_name> <reset><yellow>Starting a raid, please wait...";

        @Comment("\nUpon countdown, a title going \"5\", then \"4\", etc (depends on your countdown duration is displayed). That is not configurable but the subtitle is.")
        private String countdownSubtitle = "<green>Get ready!";

        @Comment({"\nSent to a player when they are being raided (imminent) and are online. Is as a list, each value is a new line.",
                "Placeholders: <raider> - Player attacking / <player_name> - Player being raided."})
        private List<String> raidedNotification = List.of(
                "<red><bold>---------------------------------------",
                "<green><bold><player_name><yellow><italic>, you are being raided by <red><bold><raider><yellow><italic>!",
                "<yellow><italic>Get back to your island and defend thy honour!",
                "<red><bold>---------------------------------------"
        );
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Configuration
    public static class Defences {
        @Comment("Appended to the end of the defence purchase confirmation (or defence upgrade) item if the player does not have enough Runes.")
        private List<String> insufficientRunesLore = List.of(
                "",
                "<red><italic>You do not have enough",
                "<red><italic>Runes to do this!"
        );

        @Comment("\nAppended to the end of the defence purchase confirmation item if the player doesn't have enough space in their inventory.")
        private List<String> insufficientInventoryLore = List.of(
                "",
                "<red><italic>You do not have enough",
                "<red><italic>space in your inventory!"
        );

        @Comment({"\nAppended to the end of certain defence control UI elements when the viewer does not have permissions to use that function.",
                "Example: Remove the defence while being a fighter."})
        private List<String> insufficientPermissionsLore = List.of(
                "",
                "<red><italic>You do not have the sufficient",
                "<red><italic>rank to do this!"
        );

        @Comment({"\nSent when a player successfully purchases a defence.",
                "Placeholders: <defence_name> - Name of the defence purchased"})
        private String defencePurchaseSuccess = "<server_name> <yellow>Successfully purchased <reset><defence_name><yellow>!";

        @Comment({"\nSent when a player successfully places a defence.",
                "Placeholders: <defence_name> - Name of the defence placed"})
        private String defencePlaceSuccess = "<server_name> <gray>Successfully placed <reset><defence_name><gray>!";

        @Comment("\nSent when the player tries to mine the defence.")
        private String defenceDestroyDeny = "<server_name> <red><bold>Hey! <reset><gray>You must remove the defence from the management GUI!";

        @Comment({"\nIn the defence management GUI, the \"<operation>\" placeholder is used (by default) to dynamically show whether",
                "the passive/hostile targeting is enabled or disabled.",
                "You can configure what this looks like here."})
        private String enablePlaceholder = "<green>Enable";

        @Comment({"\nIn the defence management GUI, the \"<operation>\" placeholder is used (by default) to dynamically show whether",
                "the passive/hostile targeting is enabled or disabled.",
                "You can configure what this looks like here."})
        private String disablePlaceholder = "<red>Disable";

        @Comment({"\nSent when a player tries to place too many defences.",
                "Placeholders: <defence_max> - Configured (in defences.yml) maximum defence count for players / factions."})
        private String tooManyDefences = "<server_name><red><bold> Hey!<reset><yellow> You have too many defences! <gray>(Max <defence_max>)";

        @Comment("\nSent when a player removes a defence via the GUI.")
        private String defenceRemoveSuccess = "<server_name><green> Successfully removed defence.";

        @Comment("\nSent when a player tries to access a portion of a defence that they do not have the sufficient rank for.")
        private String defenceInsufficientPermissions = "<server_name><red><bold> Hey!<reset><yellow> You don't have the sufficient rank to do that!";
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Configuration
    public static class Gems {
        @Comment("Sent when the player runs /gems or /gems balance")
        private String gemCount = "<yellow>You have: <green><bold><count><reset><yellow> Gems.";

        @Comment({"\nSent after a successful /gems paid command.",
                "Placeholders: <amount> - Amount of gems given / <player> - Player affected."})
        private String gemsPaySuccess = "<server_name><reset><green> Successfully paid<reset><yellow> <amount><reset><green> Gems to <reset><yellow><player>.";

        @Comment({"\nSent to the recipient (if online) when they are paid gems.",
                "Placeholders: <amount> - Amount of gems given / <player_name> - Name of player who paid."})
        private String gemsPayNotify = "<server_name><reset> <green><player_name> <yellow>has paid you <greeen><bold><amount> <reset><yellow>Gems.";

        @Comment({"\nSent after an admin successfully /gems give a player / faction gems.",
                "Placeholders: <amount> - Amount of gems given / <name> - Name of Player / Faction affected."})
        private String gemGiveSuccess = "<server_name><reset><green> Successfully gave<reset><yellow> <amount><reset><green> Gems to<reset><yellow> <name>.";

        @Comment({"\nSent when a player successfully withdraws gems.",
                "Placeholders: <amount> - Amount of gems withdrawn"})
        private String gemsWithdrawSuccess = "<server_name><reset><green> Successfully withdrew <reset><yellow><amount> Gems.";

        @Comment({"\nSent when a player successfully deposits gems.",
                "Placeholders: <amount> - Amount of gems deposited"})
        private String gemsDepositSuccess = "<server_name><reset><green> Successfully deposited <reset><yellow><amount> Gems.";

        @Comment("\nSent when a player tries to pay another player more gems than they have in their balance.")
        private String insufficientGemCount = "<server_name> <red><bold>Hey!<reset><gray> You do not have enough gems for that!";

        @Comment("\nSent when a player tries to withdraw more gems than they have in their inventory.")
        private String insufficientInventorySpace = "<server_name><red> There was not enough space in your inventory to withdraw the desired amount of gems.";

        @Comment("\nSent when a player tries to deposit gems when there are none in their inventory.")
        private String noGemsPresent = "<server_name> <red><bold>Hey! <reset><gray>You do not have any gems in your inventory!";

        @Comment("\nItem name of the gem item (when withdrew).")
        private String itemName = "<bold>{#37FF00}Gem{/#89FF58";

        @Comment("\nItem lore for the gem (when withdrawed).")
        private List<String> itemLore = new ArrayList<>(); // No lore for me!
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Configuration
    public static class Factions {
        @Comment({"Sent when a player donates some of their gems to their faction.",
                "Placeholders: <amount> - Amount of gems donated."})
        private String gemsDonationSuccess = "<server_name><green> Successfully donated<reset><yellow> <amount><reset><green> Gems to your faction!";

        @Comment("\nSent when a player tries to create, join a faction (etc.) when they are already in one.")
        private String alreadyInFaction = "<server_name><red><bold> Hey!<reset><gray> You are already in a faction!";

        @Comment("\nSent when a player tries to do faction-related commands that require you to be in a faction.")
        private String notInFaction = "<server_name><red><bold> Hey!<reset><gray> You are not in a faction!";

        @Comment({"\nSent when a player tries to join, get info of, etc. of a faction that does not exist.",
                "Placeholders: <name> - Faction Name."})
        private String factionNotFound = "<server_name><red> Could not find faction with a name of <name>.";

        @Comment("\nSent when a player tries to do faction-related commands (or access portions of the ui) that require you to have the proper permissions (e.g. kick when a member).")
        private String actionDeny = "<server_name><red><bold> Hey!<reset><gray> You do not have the sufficient permissions to do this.";

        @Comment("\n") private FactionCreation factionCreation = new FactionCreation();
        @Comment("\n") private FactionInfo factionInfo = new FactionInfo();
        @Comment("\n") private FactionRename factionRename = new FactionRename();
        @Comment("\n") private ChangeMOTD changeMotd = new ChangeMOTD();
        @Comment("\n") private FactionTitles factionTitles = new FactionTitles();
        @Comment("\n") private FactionBroadcast factionBroadcast = new FactionBroadcast();
        @Comment("\n") private FactionManage factionManage = new FactionManage();
        @Comment("\n") private FactionInvite factionInvite = new FactionInvite();
        @Comment("\n") private FactionDisband factionDisband = new FactionDisband();
        @Comment("\n") private FactionAbandon factionAbandon = new FactionAbandon();

        @Getter
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        @Configuration
        public static class FactionCreation {
            @Comment("Sent while creation of a faction is processing.")
            private String factionCreateProcessing = "<server_name> <yellow>Creating your faction, please wait...";

            @Comment({"\nSent when a player tries to create a faction with a name that is too short / too long.",
                    "Placeholders: <min> - Minimum name length / <max> - Maximum name length."})
            private String nameLengthLimit = "<server_name><red><bold> Hey!<reset><gray> The name must be between<red><bold> <min><reset><gray> and <red><bold><max> <reset><gray>characters long.";

            @Comment("Sent when a player tries to create a faction name that has numbers in (permitting it is configured to disallow numbers).")
            private String nameNoNumbers = "<server_name><red><bold> Hey!<reset><gray> You cannot use numbers in your name.";

            @Comment("Sent when a player tries to create a faction that contains non-english characters.")
            private String nameNonEnglish = "<server_name><red><bold> Hey!<reset><gray> You cannot use non-english characters in your name.";

            @Comment("Sent when a player tries to create a faction that contains symbols.")
            private String nameNoSymbols = "<server_name><red><bold> Hey!<reset><gray> You cannot use symbols in your name.";

            @Comment("Sent when a player tries to create a faction that contains prohibited words.")
            private String nameProhibited = "<server_name><red> Your name contains prohibited words.";

            @Comment("Sent when a player tries to create a faction that already exists with the same name.")
            private String duplicateName = "<server_name> <red>A faction already exists with that name!";

            @Comment("Sent when a player tries to create a faction when they do not have a player island.")
            private String playerIslandRequired = "<server_name><red><bold> Hey!<reset><gray> You must have a player island before creating your own faction!";

            @Comment({"\n\nSent when a player tries to create a faction but does not have enough runes.",
                    "Placeholders: <creation_cost> - Cost of faction creation."})
            private String insufficientFunds = "<server_name><red><bold> Hey!<reset><gray> You do not have enough runes to create a faction. You need <green><bold><creation_cost> Runes<reset><gray> to create a faction.";

            @Comment("\nSent when a player successfully creates a new faction.")
            private String factionCreateSuccess = "<server_name><green> Successfully created your faction!";
        }

        @Getter
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        @Configuration
        public static class FactionInfo {
            @Comment({"Message sent when the player runs /faction info. Each entry is a new line.",
                    "\nPlaceholders: <motd> - MOTD of the faction.",
                    "Placeholders: <owner> - Owner of the faction. / <admins> - Admins of the faction.",
                    "Placeholders: <moderators> - Moderators of the faction / <fighters> - Fighters of the faction",
                    "Placeholders: <members> - Members of the faction / <faction_name> - Name of the faction"})
            private List<String> informationMessage = List.of(
                    "<gold><bold>------------- <red><bold><faction_name> <gold><bold>-------------<reset>",
                    "<yellow>MOTD:<reset> <motd><reset>",
                    "",
                    "<yellow>Lord: <green><owner><reset>",
                    "<yellow>Dukes: <green><admins><reset>",
                    "<yellow>Barons: <green><moderators><reset>",
                    "<yellow>Knights: <green><fighters><reset>",
                    "<yellow>Subjects: <green><members><reset>",
                    "<gold><bold>------------- <red><bold><faction_name> <gold><bold>-------------<reset>"
            );
        }

        @Getter
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        @Configuration
        public static class FactionRename {
            @Comment({"Sent when a faction owner attempts to rename their faction when they are still on cooldown (configured in config.yml).",
                    "Placeholders: <cooldown> - Cooldown left."})
            private String renameOnCooldown = "<server_name><red><bold> Hey!<reset><gray> You are on cooldown for <red><cooldown>";

            @Comment({
                    "\nThe only reason why there are individual no-permission messages for most functions is so that you can customise them further",
                    "than just having a generic \"You do not have permissions for this action!\" message. Given, this makes it more of a pain IF",
                    "you want all the permission messages to be the same.",
                    "Sent when a faction member tries to rename the faction when they do not have permission (they must be owner!)"
            })
            private String renameNoPermissions = "<server_name><red><bold> Hey!<reset><gray> You do not have permissions to rename the faction!";

            @Comment({"\nSent when the faction does not have enough runes to rename the faction - Cost configurable in config.yml.",
                    "Placeholders: <rename-cost> - Cost of renaming the faction (in runes)."})
            private String renameInsufficientFunds = "<server_name><red><bold> Hey!<reset><gray> You do not have enough runes to rename the faction! You need <red><bold><rename-cost> Runes.";
        }

        @Getter
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        @Configuration
        public static class ChangeMOTD {

            @Comment("Sent while the server is processing the MOTD change.")
            private String motdProcessing = "<server_name><yellow> Changing the MOTD, please wait...";

            @Comment("\nSent once the server has finished changing the MOTD.")
            private String motdChangeSuccess = "<server_name><green> Successfully changed the MOTD!";
        }

        @Getter
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        @Configuration
        public static class FactionTitles {

            @Comment("See https://docs.terrabytedev.com/skyfactions/mechanics/factions/faction-ranks-titles")
            private String owner = "<red><bold>Lord";

            private String admin = "<dark_red><bold>Duke";

            private String moderator = "<blue><bold>Baron";

            private String fighter = "<gold><bold>Knight";

            private String member = "<yellow>Subject";
        }

        @Getter
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        @Configuration
        public static class FactionLeave {
            @Comment({"Sent when a player successfully leaves a faction.",
                    "Placeholders: <faction_name> - Name of the faction."})
            private String factionLeaveSuccess = "<server_name> <green>You have successfully left <yellow><faction_name>.";

            @Comment("\nAppended to the bottom of the leave confirmation gui item lore when the player is the owner.")
            private List<String> ownerConfirmationLore = List.of(
                    "<red><bold>WARNING: <italic>You are the owner of",
                    "<red><italic>this faction. If you leave, an",
                    "<red><italic>Election will begin. Rejoining will",
                    "<red><bold>not <italic>reinstate you as owner."
            );
        }

        @Getter
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        @Configuration
        public static class FactionBroadcast {

            @Comment({"Model for faction broadcasts.",
                    "Placeholders: <broadcast> - The broadcast message.",
                    "Placeholders: <broadcaster> - The player who created the broadcast."})
            private List<String> broadcastModel = List.of(
                    "<gray>╔═════════╝ <yellow>FACTION BROADCAST <gray>╚═════════╗<reset>",
                    "<broadcast><gray> - <broadcaster>",
                    "<gray>╚═════════╗ <yellow>FACTION BROADCAST <gray>╔═════════╝"
            );
        }

        @Getter
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        @Configuration
        public static class FactionManage {
            @Comment("Sent when a player tries to select themselves in the management UI.")
            private String manageSelfDeny = "<server_name><red><bold> Hey!<reset><gray> You cannot manage yourself!";

            @Comment("\nPut in the lore line of the management UI for yourself.")
            private List<String> manageSelfDenyLore = List.of("<red>You cannot manage yourself!");

            @Comment("\nSent when a player tries to moderate a member who has a higher rank.")
            private String manageHigherRanksDeny = "<server_name><red> You cannot manage those who are a higher rank than you!";

            @Comment("\nAppended to the bottom of the lore of the management UI for members with a higher rank.")
            private List<String> manageHigherRanksDenyLore = List.of("<red>You cannot manage this member!");

            @Comment("\nSent when a player tries to kick someone when they are not given permission (configured in config.yml ranks section in factions).")
            private String manageNoPermissions = "<server_name><red> You do not have permissions for this action!";

            @Comment("\nAppended the bottom of the lore of the management UI member item when you do not have permissions for that action (e.g. Ban).")
            private List<String> manageNoPermissionsLore = List.of("<server_name><red> You do not have permissions for this!");

            @Comment({"\nSent to the player (admin, moderator, owner) when they successfully kick a player.",
                    "Placeholders: <player> - Player who was kicked."})
            private String kickSuccess = "<server_name> <reset><gray>Successfully kicked <red><bold><player>";

            @Comment({"\nIf broadcast-kicks is enabled in the config, this is what will be broadcast.",
                    "Placeholders: <kicked> - Player who was kicked."})
            private String kickBroadcast = "<red><bold><kicked><reset><yellow> has been kicked from the Faction.";

            @Comment({"\nSent to the player (admin, owner) when they successfully kick a player.",
                    "Placeholders: <player> - Player who was kicked."})
            private String banSuccess = "<server_name> <reset><gray>Successfully banned <red><bold><player>";

            @Comment({"I\nf broadcast-bans is enabled in the config, this is what will be broadcast.",
                    "Placeholders: <banned> - Player who was banned."})
            private String banBroadcast = "<red><bold><banned> <reset><yellow>has been banned from the Faction.";

            @Comment({
                    "\nAdded onto the title of the rank item when updating a player's rank (assuming they're using the placeholder '<is-selected>')",
                    "A space is purposefully left at the end."
            })
            private String selected = "<green><italic>(Selected) "; // Purposefully leave a space here. Essential.

            @Comment({"\nSent when a player's rank is successfully changed (in the faction rank manage UI, assuming they have permission).",
                    "Placeholders: <player_name> - Name of the player whose rank was changed / <new-rank> - New rank of the player."})
            private String rankChangeSuccess = "<server_name> <reset><yellow>Successfully changed <green><player_name>'s<yellow> rank to <green><new-rank>.";
        }

        @Getter
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        @Configuration
        public static class FactionInvite {

            @Comment("Sent when the player tries to invite themselves to their Faction.")
            private String inviteSelfDeny = "<server_name> <red><bold>Hey!<reset><gray> You cannot invite yourself!";

            @Comment("\nSent when the player tries to invite someone in the same Faction.")
            private String inviteInSameFaction = "<server_name> <red><bold>Hey!<reset><gray> That player is already in your Faction!";

            @Comment({"\nSent when the player successfully invites another player to their Faction.",
                    "Placeholders: <player_name> - Name of the player being invited."})
            private String inviteCreateSuccess = "<server_name> <yellow>Successfully invited <green><player_name> <yellow>to your Faction.";

            @Comment("\nSent to a player when they are invited to another Faction, if online.")
            private String inviteNotification = "<server_name> <green>You have a new Faction invitation. Check your Obelisk.";

            @Comment("\nSent when a player tries to invite a player to their Faction, when they have already been invited.")
            private String inviteDuplicate = "<server_name> <red>You have already invited that player!";

            @Comment("\nSent when a player tries to invite a player to their Faction, when they are banned from that Faction.")
            private String invitePlayerBanned = "<server_name> <red><bold>Hey! <reset><gray>That player is banned from your Faction!";

            @Comment("\nSent when a player tries to request to join a Faction when they are banned from it.")
            private String requestJoinBanned = "<server_name> <red><bold>Hey! <reset><gray>You are banned from that Faction!";

            @Comment("\nSent to all Faction moderators, admins and owner (if online) when someone requests to join their Faction.")
            private String joinRequestNotification = "<server_name> <green>You have a new Faction join request. Check your Obelisk.";

            @Comment({"\nSent to a player when they create a join request for a Faction.",
                    "Placeholders: <faction_name> - Name of the Faction."})
            private String joinRequestCreateSuccess = "<server_name> <yellow>Successfully created a join request for <green><faction_name>.";

            @Comment("\nSent when a player tries to create another join request when they already have an outgoing one.")
            private String joinRequestAlreadyExists = "<server_name> <red>You already have a join request for that Faction!";

            @Comment("\nSent when a player tries to view their outgoing join request when they don't have one.")
            private String joinRequestNotExist = "<server_name> <red>You do not have an outgoing Join Request!";

            @Comment("\nSent when a player tries to create a join request to a Faction that they are already in.")
            private String joinRequestSameFaction = "<server_name> <red>You are already in that Faction!";

            @Comment({"\nSent when a player revokes a Faction invite.",
                    "Placeholders: <player_name> - Player who the invite was going to."})
            private String inviteRevokeSuccess = "<server_name> <green>Successfully revoked your invitation to <green><player_name>.";

            @Comment({"\nSent when a player (moderator, admin or owner) accepts a Faction join request.",
                    "Placeholders: <player_name> - Player who was accepted."})
            private String joinRequestAcceptSuccess = "<server_name> <green>Successfully accepted <yellow><player_name>'s <green>join request.";

            @Comment({"\nSent when a player (moderator, admin or owner) accepts a Faction join request.",
                    "Placeholders: <player_name> - Player who was rejected."})
            private String joinRequestRejectSuccess = "<server_name> <yellow>Successfully rejected <red><bold><player_name>'s <reset><yellow> join request.";

            @Comment({"\nSent when a player revokes their outgoing join request.",
                    "Placeholders: <faction_name> - Name of the Faction your join request WAS going to."})
            private String joinRequestRevokeSuccess = "<server_name> <green>Your join request to <yellow><faction_name> <green>has been revoked.";

            @Comment({"\nSent when a player confirms the acceptance of their join request, or accepted a Faction invite.",
                    "Placeholders: <faction_name> - Name of the Faction you are now part of."})
            private String playerFactionJoinSuccess = "<server_name> <yellow>You are now part of the Faction <green><faction_name>!";

            @Comment({"\nSent when a player, after their join request is accepted by the Faction, rejects it.",
                    "Placeholders: <faction_name> - Name of the Faction your join request was going to."})
            private String joinRequestDenySuccess = "<server_name> <red>You have rejected <yellow><faction_name>'s join request acceptance.";

            @Comment({"\nSent to the player (if online) when their join request to a Faction is accepted.",
                    "Placeholders: <faction_name> - Name of the Faction."})
            private String joinRequestAcceptNotification = "<server_name> <green>Your join request to <yellow><faction_name> <yellow>has been accepted!";

            @Comment({"\nSent to the player (if online) when their join request to a Faction is rejected.",
                    "Placeholders: <faction_name> - Name of the Faction."})
            private String joinRequestRejectNotification = "<server_name> <yellow>Your join request to <red><bold><faction_name><reset><yellow> has been rejected.";

            @Comment({"\nSent to the player when they denied a Faction's invite.",
                    "Placeholders: <faction_name> - Name of the Faction."})
            private String factionInviteDenySuccess = "<server_name> <yellow>You successfully denied <green><faction_name>'s <yellow>invite.";

            @Comment({"\nSent to online players (of the Faction) when a new player joins the Faction.",
                    "Placeholders: <player_name> - Name of the player who joined."})
            private String factionJoinBroadcast = "<green><player_name> <yellow>Has joined the Faction!";
        }

        @Getter
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        @Configuration
        public static class FactionDisband {

            @Comment("Sent when the player successfully disbands their Faction (and it is being processed by the plugin).")
            private String deletionProcessing = "<server_name> <grey>Deleting your Faction, please wait...";

            @Comment("\nSent when a non-owner tries to disband their Faction.")
            private String factionDisbandOwnerRequired = "<server_name> <red><bold>Hey! <reset><grey>You must be the owner of the Faction to do this!";

            @Comment("\nSent when the faction owner initially runs /faction disband and must confirm it.")
            private String factionDisbandCommandConfirm = "<server_name> <yellow>Are you sure you want to disband the Faction? Run <red>/faction disband confirm<yellow> to confirm<newline><grey>You will lose all progress, all players will be removed from the Faction.";

            @Comment("\nSent once the Faction was successfully disbanded.")
            private String factionDisbandSuccess = "<server_name> <green>Successfully disbanded your Faction.";

            @Comment("\nSent when the player tries to run '/faction disband confirm' before running '/faction disband'.")
            private String factionDisbandCommandBlock = "<server_name> <red><bold>Hey! <reset><grey>You must run <red>/faction disband <yellow>before doing that.";

            @Comment("\nSent to all online players when the faction is disbanded.")
            private String factionDisbandBroadcast = "<red><bold>The faction has been disbanded.";
        }

        @Getter
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        @Configuration
        public static class FactionAbandon {

            @Comment("Sent when a non-owner of the Faction attempts to abandon the Faction.")
            private String abandonPermissionsDeny = "<server_name> <red><bold>Hey! <reset><grey>You do not have the sufficient permissions to abandon the faction!";
        }

    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Configuration
    public static class Notifications {

        @Comment({"Sent when a player has a pending Faction invitation in their Obelisk.",
                "Placeholders: <count> - Amount of pending Faction invitations."})
        private String pendingFactionInvitations = "<server_name> <yellow>You have <green><bold><count><reset><yellow> pending Faction invitations in your Obelisk!";

        @Comment({"\nSent when a Faction has pending join requests in their Obelisk.",
                "Placeholders: <count> - Amount of pending join requests."})
        private String pendingJoinRequests = "<server_name> <yellow>You have <green><bold><count> <reset><yellow>pending Faction join requests in your Obelisk!";

        @Comment({"\nSent to the player when they have unread notifications.",
                "Placeholders: <count> - Amount of unread notifications."})
        private String unreadNotifications = "<server_name> <yellow>You have <green><bold><count><reset><yellow> unread notifications in your Obelisk.";

        @Comment("\nSent when a notification is dismissed while right-clicking.")
        private String notificationDismissSuccess = "<server_name> <green>Successfully dismissed notification.";

        @Comment("\nMessage configuration for Notifications.")
        private NotificationTypes notificationTypes = new NotificationTypes();

        @Getter
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        @Configuration
        public static class NotificationTypes {

            @Comment("\nFormat of the timestamp.")
            private String notificationTimestamp = "<yellow><time> Ago";

            @Comment({
                    "\nFormat:",
                    "notification-title - (e.g. FACTION_INVITE_TITLE): The title of the notification, when being viewed in the Notification player obelisk GUI.",
                    "notification-description - (e.g. FACTION_INVITE_DESCRIPTION): Description of the notification. Gives more information such as timestamp.",
                    "Use '/' for a new lore line! This is so it is not put onto a singular line of lore!", // todo fix this and use minimessage
                    "\nNotification for when the player is invited to a Faction.",
                    "Placeholders: <player_name> - Player who invited you / <faction_name> - Name of the Faction in question."
            })
            private String factionInviteTitle = "<green><bold>Faction Invite";
            private String factionInviteDescription = "<green><player_name> <yellow>wants you /<yellow>to join <green><faction_name>.";

            @Comment({"Notification for when a Faction moderator / admin / owner rejects your join request.",
                    "Placeholders: <player_name> - Player who rejected your join request / <faction_name> - Name of the Faction."})
            private String joinRequestRejectTitle = "<red><bold>Join Request Rejected";
            private String joinRequestRejectDescription = "<yellow><player_name> <red>rejected your join request to <green><faction_name>.";

            @Comment({"\nNotification for when a Faction moderator / admin / owner accepts your join request.",
                    "Placeholders: <player_name> - Player who accepted your join request / <faction_name> - Name of the Faction."})
            private String joinRequestAcceptTitle = "<green><bold>Join Request Accepted";
            private String joinRequestAcceptDescription = "<green><player_name> <yellow>accepted your join/request to <green><faction_name>./<gray>See the invite section of your/Obelisk for more info.";

            @Comment({"\nNotification for when a player is kicked from their faction.",
                    "Placeholders: <faction_name> - Player who was kicked."})
            private String factionKickedTitle = "<red><bold>Kicked from Faction";
            private String factionKickedDescription = "<yellow>You were kicked from your Faction <red><faction_name>!";

            @Comment({"\nNotification for when a player is banned from their faction.",
                    "Placeholders: <faction_name> - Player who was banned."})
            private String factionBannedTitle = "<red><bold>Banned from Faction";
            private String factionBannedDescription = "<yellow>You were banned from your Faction <red><faction_name>!";

            @Comment({"\nNotification for when a Faction is disbanded.",
                    "Placeholders: <player_name> - Player who disbanded the Faction."})
            private String factionDisbandedTitle = "<red><bold>Faction Disbanded";
            private String factionDisbandedDescription = "<yellow>The Faction was disbanded by/<red><player_name> while you were offline.";

            @Comment({"\nNotification for when a player's rank is updated.",
                    "Placeholders: <new_rank> - New rank of the player / <player_name> - Player who updated your rank."})
            private String factionRankUpdatedTitle = "<green>Rank Updated";
            private String factionRankUpdatedDescription = "<yellow>You rank was updated to <new_rank> by <green><player_name>.";
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Configuration
    public static class Obelisk {

        @Comment("Sent when a player tries to access another player's or Faction's Obelisk (e.g. when visiting).")
        private String accessDeny = "<server_name> <red><bold>Hey!<reset><gray> You cannot access this Obelisk!";

        @Comment("Sent when the player tries to mine the block.")
        private String destroyDeny = "<server_name> <red><bold>Hey!<reset><gray> You cannot mine this block!";

        @Comment({"Sent when a faction member tries to access a prohibited portion of the obelisk UI.",
                "For example, a faction member / knight / moderator tries to access the player management UI.",
                "Placeholders: <rank> - Required rank for access. Color coded according to FACTION_TITLES (above)."})
        private String obeliskGuiDeny = "<server_name> <red><bold>Hey!<reset><gray> You must be at least a <rank><reset><gray> to access this menu!";

        @Comment("\n")
        private ObeliskBlock obeliskBlock = new ObeliskBlock();

        @Getter
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        @Configuration
        public static class ObeliskBlock {

            @Comment("Name of the Obelisk Block Item")
            private String itemName = "{#0854FB}<bold>Obelisk{/#8000FF}";

            @Comment("\nLore of the Obelisk Block Item")
            private List<String> itemLore = List.of(
                    "<gold><bold>┍━━━━╝✹╚━━━━┑",
                    "<red><underlined><bold>Obelisk",
                    "<yellow>This block is one of the main mechanics of SkyFactions.",
                    "<yellow>This allows you to manage defences, resources, and more.",
                    "<dark_red><italic>Guard it with your life.",
                    "<gold><bold>┕━━━━╗✹╔━━━━┙"
            );
        }

        @Comment({
                "\nCertain parts of the obelisk GUI take some time to load.",
                "You can configure the placeholder item here."
        })
        private Loading loading = new Loading();

        @Getter
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        @Configuration
        public static class Loading {

            @Comment("Material of the item")
            private String material = "COMPASS";

            @Comment("Text of the item. Supports color codes")
            private String text = "<yellow>Please wait...";

            private List<String> lore = List.of(
                    "<gray>We're loading this section",
                    "<gray>of your Obelisk. Bear with us!"
            );
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Configuration
    public static class Runes {

        @Comment("Sent when items with enchants are not allowed for rune conversion.")
        private String enchantsDeny = "<server_name> <red>You cannot convert items with enchants.";

        @Comment("\nSent when a player tries to convert an item with lore, or is blacklisted / not whitelisted.")
        private String generalDeny = "<server_name> <red>You cannot convert this item.";

        @Comment({"\nSent when a player runs /runes balance.",
                "Placeholders: <count> - Amount of Runes they have."})
        private String runesBalance = "<yellow>You have: <green><bold><count><reset><yellow> Runes.";

        @Comment({"\nSent when an admin runs /runes give <type> <player / faction> <amount> (Admin Command!)",
                "Placeholders: <amount> - Amount of additional runes given / <name> - Player / Faction affected."})
        private String runesGiveSuccess = "<server_name><reset><green> Successfully paid<reset><yellow> <amount><reset><green> Runes to <reset><yellow><name>.";

        @Comment({"\nSent when an admin runes /runes reset <type> <player / faction> (Admin Command!)",
                "Placeholders: <name> - Player / Faction affected."})
        private String runesResetSuccess = "<server_name><reset><green> Successfully reset<reset><yellow> <name>'s<reset><green> Runes.";

        @Comment("\nSent when they successfully convert their items to runes.")
        private String conversionSuccess = "<server_name><green><bold> Success!<reset><gray> You have gained <green><bold><added><reset><gray> runes!";

        @Comment("\nSent when the player doesn't put enough of any item in to get any runes back.")
        private String insufficientItems = "<server_name><red> You did not submit enough items to receive any runes.";
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Configuration
    public static class AuditLogs {

        @Comment({"Format of the timestamp.",
                "Placeholders: <time> - Amount of time \"ago\" that the audit log entry was created."})
        private String auditTimestamp = "<yellow><time> Ago";

        @Comment({
                "\nFormat:",
                "action-title - (e.g. join-title): The title of the audit log, when being viewed in the Audit Log obelisk GUI.",
                "action-description (e.g. join-description): Description of the audit log. Gives more information such as timestamp.",
                "Use '/' for a new lore line! This is so it is not placed onto a singular line of lore!", // todo fix this and use minimessage
                "\nWhen the faction is first created.",
                "Placeholders: <player_name> - Name of player who created the faction.",
                "Placeholders: <faction_name> - Name of the faction."
        })
        private String factionCreateTitle = "<green><bold>Faction Created";
        private String factionCreateDescription = "<green><player_name><reset><yellow> created the /<yellow>faction <green><faction_name>.";

        @Comment({"Sent when a player joins the faction.",
                "Placeholders: <player_name> - Player who joined."})
        private String joinTitle = "<green><bold>Player Joined";
        private String joinDescription = "<green><player_name><reset><yellow> joined the faction.";

        @Comment({"Sent when a player leaves the faction.",
                "Placeholders: <player_name> - Player who left."})
        private String leaveTitle = "<red><bold>Player Left";
        private String leaveDescription = "<red><player_name><reset><yellow> left the faction.";

        @Comment({"Sent when a player updates the MOTD.",
                "Placeholders: <player_name> - Player who updated the MOTD.",
                "Placeholders: <new-motd> - New MOTD."})
        private String motdTitle = "<yellow><bold>MOTD Updated";
        private String motdDescription = "<green><player_name> <reset><yellow>updated the /<yellow>Faction's MOTD to<reset> /<new-motd>";

        @Comment({"Sent when a player (moderator, admin or owner) creates a broadcast.",
                "Placeholders: <player_name> - Player who created the broadcast."})
        private String broadcastTitle = "<green><bold>Broadcast Created";
        private String broadcastDescription = "<green><player_name> <reset><yellow>created a new broadcast.";

        @Comment({"Sent when a player (moderator, admin or owner) kicks a player.",
                "Placeholders: <kicked> - Name of player who was kicked. <player> - Player who kicked them."})
        private String kickTitle = "<red><bold>Player Kicked";
        private String kickDescription = "<green><kicked> <yellow>was kicked from the Faction/<yellow>by <green><player>.";

        @Comment({"Sent when a player (admin or owner) bans a player.",
                "Placeholders: <banned> - Name of player who was banned. <player> - Player who banned them."})
        private String banTitle = "<dark_red><bold>Player Banned";
        private String banDescription = "<green><banned> <yellow>was banned from the Faction/<yellow>by <green><player>.";

        @Comment({"Sent when a player (moderator, admin or owner) creates an invitation.",
                "Placeholder: <inviter> - Name of player who created the invite / <player_name> - Invited player."})
        private String inviteCreateTitle = "<green><bold>Invite Created";
        private String inviteCreateDescription = "<green><inviter> <yellow>invited <green><player_name> <yellow>to the Faction.";

        @Comment({"Sent when a player creates a join request to the Faction.",
                "Placeholder: <player_name> - Player who created the join request."})
        private String joinRequestTitle = "<green><bold>Join Request Created";
        private String joinRequestDescription = "<green><player_name> <yellow>wants to join the Faction.";

        @Comment({"Sent when a player (moderator, admin or owner) revokes an invitation.",
                "Placeholders: <player> - Player who revoked the invite / <invited> - Player who was invited."})
        private String inviteRevokeTitle = "<red><bold>Invite Revoked";
        private String inviteRevokeDescription = "<green><player> <yellow>revoked a Faction invite for <green><invited>";

        @Comment({"Sent when a player (moderator, admin or owner) accepts a join request.",
                "Placeholders: <player_name> - Player who was accepted. <member> - Player who accepted the join request."})
        private String joinRequestAcceptTitle = "<green><bold>Join Request Accepted";
        private String joinRequestAcceptDescription = "<green><player_name> <yellow>was accepted into the Faction/<yellow>by <green><member>.";

        @Comment({"Sent when a player (moderator, admin or owner) rejects a join request.",
                "Placeholders: <faction_player> - Faction member who rejected the join request / <player> - Player who made the join request in the first place."})
        private String joinRequestRejectTitle = "<red><bold>Join Request Rejected";
        private String joinRequestRejectDescription = "<green><faction_player> <yellow>rejected <green><player>'s/<yellow>join request.";

        @Comment({"Sent when a player revokes their outgoing join request to join your Faction.",
                "Placeholders: <player_name> - Owner of the join request."})
        private String playerJoinRequestRevokeTitle = "<red><bold>Join Request Revoked";
        private String playerJoinRequestRevokeDescription = "<green><player_name> <yellow>revoked their/join request to the Faction.";

        @Comment({"Sent when, after you accept a player's join request, they deny your acceptance.",
                "Placeholders: <player_name> - Owner of the join request."})
        private String playerJoinRequestDenyTitle = "<red><bold>Join Request Denied";
        private String playerJoinRequestDenyDescription = "<green><player_name> <yellow>denied your acceptance/of their join request.";

        @Comment({"Sent when a player accepts a Faction's invite.",
                "Placeholders: <player_name> - Player who accepted the invite."})
        private String inviteAcceptTitle = "<green><bold>Invite Accepted";
        private String inviteAcceptDescription = "<green><player_name> <yellow>accepted your Faction invite.";

        @Comment({"Sent when a player denies a Faction's invite.",
                "Placeholders: <player_name> - Player who denied the invite."})
        private String inviteDenyTitle = "<red><bold>Invite Denied";
        private String inviteDenyDescription = "<green><player_name> <yellow>denied your Faction invite.";

        @Comment({"Sent when a player (moderator, admin or owner) purchases a defence.",
                "Placeholders: <player_name> - Name of the player / <defence_name> - Configured name of the defence bought."})
        private String defencePurchaseTitle = "<yellow>Defence Purchased";
        private String defencePurchaseDescription = "<green><player_name> <yellow>purchased the <reset><defence_name><yellow> defence.";

        @Comment({"Sent when a player (moderator, admin or owner) removes a defence.",
                "Placeholders: <player_name> - Player who removed it / <defence_name> - Configured name of the defence bought."})
        private String defenceRemovalTitle = "<red><bold>Defence Removed";
        private String defenceRemovalDescription = "<green><player_name> <yellow>removed a <reset><defence_name><yellow> defence.";
    }




}
