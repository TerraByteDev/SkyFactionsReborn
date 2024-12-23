package net.skullian.skyfactions.paper.api;

import net.skullian.skyfactions.common.api.RaidAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.database.struct.IslandRaidData;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.common.util.SkyLocation;
import net.skullian.skyfactions.common.util.text.TextUtility;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


public class SpigotRaidAPI extends RaidAPI {

    @Override
    public CompletableFuture<String> getCooldownDuration(SkyUser player) {
        long cooldownDurationInMilliseconds = Settings.RAIDING_COOLDOWN.getLong();
        return SkyApi.getInstance().getPlayerAPI().getPlayerData(player.getUniqueId()).handle((data, ex) -> {
            if (ex != null) {
                SLogger.fatal("Failed to get player data for player {} - {}", player.getUniqueId(), ex);
                return null;
            }

            long currentTime = System.currentTimeMillis();

            if (currentTime - data.getLAST_RAID() >= cooldownDurationInMilliseconds) {
                return null;
            } else {
                long cooldownDuration = cooldownDurationInMilliseconds - (currentTime - data.getLAST_RAID());
                return DurationFormatUtils.formatDuration(cooldownDuration, "HH'h 'mm'm 'ss's'");
            }
        });
    }

    @Override
    public void startRaid(SkyUser player) {
        // TODO REFACTOR ALL OF THIS
        try {
            AtomicBoolean cancel = new AtomicBoolean(false);

            Messages.RAID_PROCESSING.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()));

            /*SkyFactionsReborn.getDatabaseManager().updateLastRaid(player, System.currentTimeMillis()).thenAccept(result -> SkyFactionsReborn.getDatabaseManager().getGems(player).thenAccept(count -> SkyFactionsReborn.getDatabaseManager().subtractGems(player, count, Settings.RAIDING_COST.getInt()))).exceptionally(ex -> {
                ex.printStackTrace();
                cancel.set(true);
                Messages.ERROR.send(player, "operation", "start a raid", "debug", "SQL_RAID_START");
                handleRaidExecutionError(player, false);
                return null;
            }).get();*/

            if (cancel.get()) return;
            IslandRaidData island = getRandomRaidable(player);
            if (island != null) {
                UUID playerUUID = UUID.fromString(island.getUuid());

                if (cancel.get()) return;
                handlePlayers(player, playerUUID);
                processingRaid.put(player.getUniqueId(), playerUUID);
            } else {
                Messages.ERROR.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "operation", "start a raid", "debug", "ISLAND_RETURNED_NULL");
            }

        } catch (Exception error) {
            ErrorUtil.handleError(player, "start a raid", "MAIN_RAID_START", error);
            handleRaidExecutionError(player, false);
        }
    }

    @Override
    public void handlePlayers(SkyUser attacker, UUID uuid) {
        /*AtomicBoolean cancel = new AtomicBoolean(false);

        if (!isPlayerOnline(uuid)) {
            SkyApi.getInstance().getDiscordHandler().pingRaid(attacker, Bukkit.getOfflinePlayer(uuid).getPlayer());
        } else {
            Player def = Bukkit.getPlayer(uuid);
            if (def == null) return;

            alertPlayer(def, attacker);
            teleportToPreparationArea(def);

            SpigotIslandAPI.getPlayerIsland(def.getUniqueId()).thenAccept(is -> SkyApi.getInstance().getDatabaseManager().getPlayerIslandManager().setIslandCooldown(is, System.currentTimeMillis())).exceptionally(ex -> {
                cancel.set(true);

                ex.printStackTrace();
                Messages.ERROR.send(def, attacker.locale().getLanguage(), "operation", "start a raid", "debug", "SQL_ISLAND_COOLDOWN");
                Messages.ERROR.send(attacker, attacker.locale().getLanguage(), "operation", "start a raid", "debug", "SQL_ISLAND_COOLDOWN");
                handleRaidExecutionError(def, true);
                handleRaidExecutionError(attacker, false);
                return null;
            });
            if (cancel.get()) return;

            Bukkit.getScheduler().runTaskLater(SkyFactionsReborn.getInstance(), () -> {
                if (!def.isOnline()) return;
                SpigotIslandAPI.getPlayerIsland(def.getUniqueId()).thenAccept(island -> {
                    World islandWorld = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
                    if (islandWorld != null && island != null) {
                        Location returnLoc = island.getCenter(islandWorld);
                        SpigotRegionAPI.teleportPlayerToLocation(def, returnLoc);

                        returnLoc.setY(Settings.RAIDING_SPAWN_HEIGHT.getInt());
                        SpigotRegionAPI.teleportPlayerToLocation(attacker, returnLoc);

                        showCountdown(uuid, attacker).thenAccept(re -> {
                            SoundUtil.playMusic(def, attacker);
                        }).exceptionally(ex -> {
                            cancel.set(true);
                            ex.printStackTrace();
                            Messages.ERROR.send(attacker, attacker.locale().getLanguage(), "operation", "start a raid", "debug", "MAIN_RAID_COUNTDOWN");
                            Messages.ERROR.send(def, attacker.locale().getLanguage(), "operation", "start a raid", "debug", "MAIN_RAID_COUNTDOWN");
                            handleRaidExecutionError(def, true);
                            handleRaidExecutionError(attacker, false);

                            return null;
                        });
                    }
                });

            }, (Settings.RAIDING_PREPARATION_TIME.getInt() * 20L));
        }*/
    }

    @Override
    public boolean hasEnoughGems(SkyUser player) {
        /*try {
            int required = Settings.RAIDING_COST.getInt();
            AtomicInteger currentGems = new AtomicInteger();
            SkyApi.getInstance().getDatabaseManager().getCurrencyManager().getGems(player.getUniqueId()).thenAccept(currentGems::set).exceptionally(ex -> {
                ex.printStackTrace();
                Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "check your gem count", "debug", "SQL_GEMS_GET");
                return null;
            }).get();

            if (currentGems.get() < required) {
                Messages.RAID_INSUFFICIENT_GEMS.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "raid_cost", required);
                return false;
            } else {
                return true;
            }
        } catch (InterruptedException | ExecutionException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "start a raid", "debug", "SQL_GEMS_GET");
        }*/

        return false;
    }

    @Override
    public IslandRaidData getRandomRaidable(SkyUser player) {
        try {
            AtomicReference<List<IslandRaidData>> data = new AtomicReference<>(new ArrayList<>());
            SkyApi.getInstance().getDatabaseManager().getPlayerIslandManager().getRaidableIslands(player).thenAccept(data::set).exceptionally(ex -> {
                ErrorUtil.handleError(player, "start a raid", "SQL_RAIDABLE_GET", ex);
                return null;
            }).get();

            if (data.get().isEmpty()) {
                Messages.RAID_NO_PLAYERS.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()));
            } else {
                Random random = new Random();
                return data.get().get(random.nextInt(data.get().size()));
            }
        } catch (InterruptedException | ExecutionException error) {
            ErrorUtil.handleError(player, "start a raid", "SQL_RAIDABLE_GET", error);
        }
        return null;
    }

    @Override
    public void alertPlayer(SkyUser player, SkyUser attacker) {
        SkyApi.getInstance().getSoundAPI().soundAlarm(player);
        List<String> alertList = Messages.RAID_IMMINENT_MESSAGE.getStringList(SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()));

        if (player.isOnline()) {
            for (String msg : alertList) {
                player.sendMessage(TextUtility.color(msg.replace("player_name", player.getName()).replace("raider", attacker.getName()), SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), player));
            }
        }
    }

    @Override
    public void teleportToPreparationArea(SkyUser player) {
        if (player.isOnline()) {
            List<Integer> loc = Settings.RAIDING_PREPARATION_POS.getIntegerList();
            World world = Bukkit.getWorld(Settings.RAIDING_PREPARATION_WORLD.getString());
            if (world != null) {
                SkyLocation location = new SkyLocation(world.getName(), loc.get(0), loc.get(1), loc.get(2));
                player.teleport(location);
            }
        }
    }

    @Override
    public CompletableFuture<Void> showCountdown(UUID def, SkyUser att) {
        return CompletableFuture.runAsync(() -> {
            /*try {
                Player defp = Bukkit.getPlayer(def);
                int length = Settings.RAIDING_COUNTDOWN_DURATION.getInt();
                String countdown_sound = Settings.COUNTDOWN_SOUND.getString();
                int countdown_pitch = Settings.COUNTDOWN_SOUND_PITCH.getInt();
                final Component attackerSubtitle = TextUtility.color(Messages.RAIDING_COUNTDOWN_SUBTITLE.getString(att.locale().getLanguage()), att.locale().getLanguage(), att);
                final Component defendantSubtitle = defp != null ? TextUtility.color(Messages.RAIDING_COUNTDOWN_SUBTITLE.getString(defp.locale().getLanguage()), defp.locale().getLanguage(), defp) : null;
                Thread.sleep(3500);
                for (int i = length; i == 0; i--) {
                    final Component mainTitle = Component.text(i + 1, NamedTextColor.RED);

                    final Title attackerTitle = Title.title(mainTitle, attackerSubtitle);
                    if (defp != null) att.showTitle(attackerTitle);

                    SkyApi.getInstance().getSoundAPI().playSound(defp, countdown_sound, countdown_pitch, 1f);
                    SoundUtil.playSound(att, countdown_sound, countdown_pitch, 1f);

                    Thread.sleep(1000);
                }
            } catch (InterruptedException error) {
                throw new RuntimeException(error);
            }*/
        });

    }

    @Override
    public void handleRaidExecutionError(SkyUser player, boolean isDefendant) {
        /*if (player != null) {
            if (processingRaid.containsKey(player.getUniqueId()) || processingRaid.containsValue(player.getUniqueId())) {
                processingRaid.remove(player.getUniqueId());
            }
        }

        SkyApi.getInstance().getCacheService().getEntry(player.getUniqueId()).setNewLastRaid(player.getUniqueId(), 0);
        SkyApi.getInstance().getCacheService().getEntry(player.getUniqueId()).addGems(Settings.RAIDING_COST.getInt());

        if (isDefendant) {
            SpigotIslandAPI.getPlayerIsland(player.getUniqueId()).thenAccept(island -> SkyApi.getInstance().getDatabaseManager().getPlayerIslandManager().setIslandCooldown(island, 0).exceptionally(ex -> {
                ex.printStackTrace();
                Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "handle raid errors", "debug", "SQL_ISLAND_COOLDOWN");
                return null;
            })).exceptionally(ex -> {
                ex.printStackTrace();
                Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "handle raid errors", "debug", "SQL_ISLAND_GET");
                return null;
            });
        }*/
    }
}