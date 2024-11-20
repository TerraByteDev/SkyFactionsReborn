package net.skullian.skyfactions.database.impl;

import net.skullian.skyfactions.event.PlayerHandler;
import org.bukkit.entity.Player;
import org.jooq.DSLContext;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.skullian.skyfactions.database.tables.PlayerData.PLAYER_DATA;

public class PlayerDatabaseManager {

    private final DSLContext ctx;

    public PlayerDatabaseManager(DSLContext ctx) {
        this.ctx = ctx;
    }

    public CompletableFuture<Boolean> isPlayerRegistered(Player player) {
        return CompletableFuture.supplyAsync(() -> ctx.fetchExists(PLAYER_DATA, PLAYER_DATA.UUID.eq(player.getUniqueId().toString())));
    }

    public CompletableFuture<Void> registerPlayer(Player player) {
        return CompletableFuture.runAsync(() -> {
            ctx.insertInto(PLAYER_DATA)
                    .columns(PLAYER_DATA.UUID, PLAYER_DATA.DISCORD_ID, PLAYER_DATA.LAST_RAID, PLAYER_DATA.LOCALE)
                    .values(player.getUniqueId().toString(), "none", (long) 0, PlayerHandler.getLocale(player.getUniqueId()))
                    .execute();
        });
    }

    public CompletableFuture<Void> registerDiscordLink(UUID playerUUID, String discordID) {
        return CompletableFuture.runAsync(() -> {
            ctx.update(PLAYER_DATA)
                    .set(PLAYER_DATA.DISCORD_ID, discordID)
                    .where(PLAYER_DATA.UUID.eq(playerUUID.toString()))
                    .execute();
        });
    }

    public CompletableFuture<String> getDiscordID(Player player) {
        return CompletableFuture.supplyAsync(() -> ctx.select(PLAYER_DATA.DISCORD_ID)
                .from(PLAYER_DATA)
                .where(PLAYER_DATA.UUID.eq(player.getUniqueId().toString()))
                .fetchOneInto(String.class));
    }

    public CompletableFuture<Long> getLastRaid(Player player) {
        return CompletableFuture.supplyAsync(() -> ctx.select(PLAYER_DATA.LAST_RAID)
                .from(PLAYER_DATA)
                .where(PLAYER_DATA.UUID.eq(player.getUniqueId().toString()))
                .fetchOneInto(Long.class));
    }

    public CompletableFuture<Void> updateLastRaid(Player player, long time) {
        return CompletableFuture.runAsync(() -> {
             ctx.update(PLAYER_DATA)
                     .set(PLAYER_DATA.LAST_RAID, time)
                     .where(PLAYER_DATA.UUID.eq(player.getUniqueId().toString()))
                     .execute();
        });
    }

    public CompletableFuture<String> getPlayerLocale(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> ctx.select(PLAYER_DATA.LOCALE)
                .from(PLAYER_DATA)
                .where(PLAYER_DATA.UUID.eq(uuid.toString()))
                .fetchOneInto(String.class));
    }

    public CompletableFuture<Void> setPlayerLocale(UUID uuid, String locale) {
        return CompletableFuture.runAsync(() -> {
            if (locale == null) return;
            ctx.update(PLAYER_DATA)
                    .set(PLAYER_DATA.LOCALE, locale)
                    .where(PLAYER_DATA.UUID.eq(uuid.toString()))
                    .execute();
        });
    }


}
