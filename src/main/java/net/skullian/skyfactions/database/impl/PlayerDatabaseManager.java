package net.skullian.skyfactions.database.impl;

import net.skullian.skyfactions.event.PlayerHandler;
import org.bukkit.entity.Player;
import org.jooq.DSLContext;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.skullian.skyfactions.database.Tables.PLAYERDATA;

public class PlayerDatabaseManager {

    private final DSLContext ctx;

    public PlayerDatabaseManager(DSLContext ctx) {
        this.ctx = ctx;
    }

    public CompletableFuture<Boolean> isPlayerRegistered(Player player) {
        return CompletableFuture.supplyAsync(() -> ctx.fetchExists(PLAYERDATA, PLAYERDATA.UUID.eq(player.getUniqueId().toString())));
    }

    public CompletableFuture<Void> registerPlayer(Player player) {
        return CompletableFuture.runAsync(() -> {
            ctx.insertInto(PLAYERDATA)
                    .columns(PLAYERDATA.UUID, PLAYERDATA.FACTION, PLAYERDATA.DISCORD_ID, PLAYERDATA.LAST_RAID, PLAYERDATA.LOCALE)
                    .values(player.getUniqueId().toString(), "none", "none", (long) 0, PlayerHandler.getLocale(player.getUniqueId()))
                    .execute();
        });
    }

    public CompletableFuture<Void> registerDiscordLink(UUID playerUUID, String discordID) {
        return CompletableFuture.runAsync(() -> {
            ctx.update(PLAYERDATA)
                    .set(PLAYERDATA.DISCORD_ID, discordID)
                    .where(PLAYERDATA.UUID.eq(playerUUID.toString()))
                    .execute();
        });
    }

    public CompletableFuture<String> getDiscordID(Player player) {
        return CompletableFuture.supplyAsync(() -> ctx.select(PLAYERDATA.DISCORD_ID)
                .from(PLAYERDATA)
                .where(PLAYERDATA.UUID.eq(player.getUniqueId().toString()))
                .fetchOneInto(String.class));
    }

    public CompletableFuture<Long> getLastRaid(Player player) {
        return CompletableFuture.supplyAsync(() -> ctx.select(PLAYERDATA.LAST_RAID)
                .from(PLAYERDATA)
                .where(PLAYERDATA.UUID.eq(player.getUniqueId().toString()))
                .fetchOneInto(Long.class));
    }

    public CompletableFuture<Void> updateLastRaid(Player player, long time) {
        return CompletableFuture.runAsync(() -> {
             ctx.update(PLAYERDATA)
                     .set(PLAYERDATA.LAST_RAID, time)
                     .where(PLAYERDATA.UUID.eq(player.getUniqueId().toString()))
                     .execute();
        });
    }

    public CompletableFuture<String> getPlayerLocale(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> ctx.select(PLAYERDATA.LOCALE)
                .from(PLAYERDATA)
                .where(PLAYERDATA.UUID.eq(uuid.toString()))
                .fetchOneInto(String.class));
    }

    public CompletableFuture<Void> setPlayerLocale(UUID uuid, String locale) {
        return CompletableFuture.runAsync(() -> {
            ctx.update(PLAYERDATA)
                    .set(PLAYERDATA.LOCALE, locale)
                    .where(PLAYERDATA.UUID.eq(uuid.toString()))
                    .execute();
        });
    }


}
