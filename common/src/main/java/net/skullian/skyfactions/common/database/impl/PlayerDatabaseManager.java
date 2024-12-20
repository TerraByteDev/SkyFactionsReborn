package net.skullian.skyfactions.common.database.impl;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.database.struct.PlayerData;
import net.skullian.skyfactions.common.database.tables.records.PlayerDataRecord;
import org.jooq.DSLContext;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.skullian.skyfactions.common.database.tables.PlayerData.PLAYER_DATA;

public class PlayerDatabaseManager {

    private final DSLContext ctx;

    public PlayerDatabaseManager(DSLContext ctx) {
        this.ctx = ctx;
    }

    public CompletableFuture<Void> registerPlayer(UUID uuid, boolean shouldRegister) {
        return CompletableFuture.runAsync(() -> {
            if (!shouldRegister) return;
            ctx.insertInto(PLAYER_DATA)
                    .columns(PLAYER_DATA.UUID, PLAYER_DATA.DISCORD_ID, PLAYER_DATA.LAST_RAID, PLAYER_DATA.LOCALE)
                    .values(uuid.toString(), "none", (long) 0, SkyApi.getInstance().getPlayerAPI().getLocale(uuid))
                    .execute();
        });
    }

    public CompletableFuture<PlayerData> getPlayerData(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            PlayerDataRecord result = ctx.selectFrom(PLAYER_DATA)
                    .where(PLAYER_DATA.UUID.eq(uuid.toString()))
                    .fetchOne();

            return result != null ?
                    new PlayerData(
                            result.getDiscordId(),
                            result.getLastRaid(),
                            result.getLocale()
                    ) : null;
        });
    }

    public CompletableFuture<Void> registerDiscordLink(UUID playerUUID, String discordID) {
        return CompletableFuture.runAsync(() -> {
            if (discordID == null) return;

            ctx.update(PLAYER_DATA)
                    .set(PLAYER_DATA.DISCORD_ID, discordID)
                    .where(PLAYER_DATA.UUID.eq(playerUUID.toString()))
                    .execute();
        });
    }

    public CompletableFuture<Void> updateLastRaid(UUID playerUUID, long time) {
        return CompletableFuture.runAsync(() -> {
            if (time == -1) return;
            ctx.update(PLAYER_DATA)
                    .set(PLAYER_DATA.LAST_RAID, time)
                    .where(PLAYER_DATA.UUID.eq(playerUUID.toString()))
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
