package net.skullian.skyfactions.common.database.impl;

import net.skullian.skyfactions.core.config.types.Settings;
import net.skullian.skyfactions.common.database.tables.records.DefenceLocationsRecord;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.skullian.skyfactions.common.database.tables.DefenceLocations.DEFENCE_LOCATIONS;

public class DefencesDatabaseManager {

    private final DSLContext ctx;

    public DefencesDatabaseManager(DSLContext ctx) {
        this.ctx = ctx;
    }

    public CompletableFuture<List<Location>> getDefenceLocations(Condition condition, String type) {
        return CompletableFuture.supplyAsync(() -> {
            Result<DefenceLocationsRecord> results = ctx.selectFrom(DEFENCE_LOCATIONS)
                    .where(DEFENCE_LOCATIONS.TYPE.eq(type), condition)
                    .fetch();

            World world = type.equals("faction") ? Bukkit.getWorld(Settings.ISLAND_FACTION_WORLD.getString()) : Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
            if (world == null) throw new NullPointerException("Could not find configured island world for type: " + type);

            List<Location> locations = new ArrayList<>();
            for (DefenceLocationsRecord location : results) {
                locations.add(new Location(
                        world,
                        location.getX(),
                        location.getY(),
                        location.getZ()
                ));
            }

            return locations;
        });
    }

    public CompletableFuture<Void> registerDefenceLocations(List<Location> locations, String owner, boolean faction) {
        return CompletableFuture.runAsync(() -> {
            ctx.transaction((Configuration trx) -> {
                for (Location location : locations) {
                    trx.dsl().insertInto(DEFENCE_LOCATIONS)
                            .columns(DEFENCE_LOCATIONS.UUID, DEFENCE_LOCATIONS.TYPE, DEFENCE_LOCATIONS.FACTIONNAME, DEFENCE_LOCATIONS.X, DEFENCE_LOCATIONS.Y, DEFENCE_LOCATIONS.Z)
                            .values((faction ? "N/A" : owner), (faction ? "faction" : "player"), (faction ? owner : "N/A"), location.getBlockX(), location.getBlockY(), location.getBlockZ())
                            .execute();
                }
            });
        });
    }

    public CompletableFuture<Void> removeDefenceLocations(List<Location> locations, String owner, boolean isFaction) {
        return CompletableFuture.runAsync(() -> {
            ctx.transaction((Configuration trx) -> {
                for (Location location : locations) {
                    trx.dsl().deleteFrom(DEFENCE_LOCATIONS)
                            .where((isFaction ? DEFENCE_LOCATIONS.FACTIONNAME.eq(owner) : DEFENCE_LOCATIONS.UUID.eq(owner)), (isFaction ? DEFENCE_LOCATIONS.TYPE.eq("faction") : DEFENCE_LOCATIONS.TYPE.eq("player")),
                                    DEFENCE_LOCATIONS.X.eq(location.getBlockX()), DEFENCE_LOCATIONS.Y.eq(location.getBlockY()), DEFENCE_LOCATIONS.Z.eq(location.getBlockZ()))
                            .execute();
                }
            });
        });
    }

    public CompletableFuture<Void> removeAllDefences(String owner, boolean isFaction) {
        return CompletableFuture.runAsync(() -> {
            ctx.deleteFrom(DEFENCE_LOCATIONS)
                    .where((isFaction ? DEFENCE_LOCATIONS.FACTIONNAME.eq(owner) : DEFENCE_LOCATIONS.UUID.eq(owner)), (isFaction ? DEFENCE_LOCATIONS.TYPE.eq("faction") : DEFENCE_LOCATIONS.TYPE.eq("player")))
                    .execute();
        });
    }


}
