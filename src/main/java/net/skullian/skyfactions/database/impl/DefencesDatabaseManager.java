package net.skullian.skyfactions.database.impl;

import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.database.tables.records.DefencelocationsRecord;
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

import static net.skullian.skyfactions.database.tables.Defencelocations.DEFENCELOCATIONS;

public class DefencesDatabaseManager {

    private final DSLContext ctx;

    public DefencesDatabaseManager(DSLContext ctx) {
        this.ctx = ctx;
    }

    public CompletableFuture<List<Location>> getDefenceLocations(Condition condition, String type) {
        return CompletableFuture.supplyAsync(() -> {
            Result<DefencelocationsRecord> results = ctx.selectFrom(DEFENCELOCATIONS)
                    .where(DEFENCELOCATIONS.TYPE.eq(type), condition)
                    .fetch();

            World world = type.equals("faction") ? Bukkit.getWorld(Settings.ISLAND_FACTION_WORLD.getString()) : Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
            if (world == null) throw new NullPointerException("Could not find configured island world for type: " + type);

            List<Location> locations = new ArrayList<>();
            for (DefencelocationsRecord location : results) {
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
                    trx.dsl().insertInto(DEFENCELOCATIONS)
                            .columns(DEFENCELOCATIONS.UUID, DEFENCELOCATIONS.TYPE, DEFENCELOCATIONS.FACTIONNAME, DEFENCELOCATIONS.X, DEFENCELOCATIONS.Y, DEFENCELOCATIONS.Z)
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
                    trx.dsl().deleteFrom(DEFENCELOCATIONS)
                            .where((isFaction ? DEFENCELOCATIONS.FACTIONNAME.eq(owner) : DEFENCELOCATIONS.UUID.eq(owner)), (isFaction ? DEFENCELOCATIONS.TYPE.eq("faction") : DEFENCELOCATIONS.TYPE.eq("player")),
                                    DEFENCELOCATIONS.X.eq(location.getBlockX()), DEFENCELOCATIONS.Y.eq(location.getBlockY()), DEFENCELOCATIONS.Z.eq(location.getBlockZ()))
                            .execute();
                }
            });
        });
    }

    public CompletableFuture<Void> removeAllDefences(String owner, boolean isFaction) {
        return CompletableFuture.runAsync(() -> {
            ctx.deleteFrom(DEFENCELOCATIONS)
                    .where((isFaction ? DEFENCELOCATIONS.FACTIONNAME.eq(owner) : DEFENCELOCATIONS.UUID.eq(owner)), (isFaction ? DEFENCELOCATIONS.TYPE.eq("faction") : DEFENCELOCATIONS.TYPE.eq("player")))
                    .execute();
        });
    }


}
