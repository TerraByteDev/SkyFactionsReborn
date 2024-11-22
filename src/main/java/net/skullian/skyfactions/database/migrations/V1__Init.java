package net.skullian.skyfactions.database.migrations;

import net.skullian.skyfactions.util.MigrationUtil;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;

import static org.jooq.impl.SQLDataType.*;

public class V1__Init extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        DSLContext ctx = MigrationUtil.getCtx(context);

        ctx.createTableIfNotExists("islands")
                .column("id", INTEGER)
                .column("uuid", VARCHAR)
                .column("level", INTEGER)
                .column("gems", INTEGER)
                .column("runes", INTEGER)
                .column("defenceCount", INTEGER)
                .column("last_raided", BIGINT)
                .column("last_raider", VARCHAR)
                .primaryKey("id")
                .execute();

        ctx.createTableIfNotExists("player_data")
                .column("uuid", VARCHAR)
                .column("faction", VARCHAR)
                .column("discord_id", VARCHAR)
                .column("last_raid", BIGINT)
                .column("locale", VARCHAR)
                .primaryKey("uuid")
                .execute();

        ctx.createTableIfNotExists("faction_islands")
                .column("id", INTEGER)
                .column("factionName", VARCHAR)
                .column("runes", INTEGER)
                .column("defenceCount", INTEGER)
                .column("gems", INTEGER)
                .column("last_raided", BIGINT)
                .column("last_raider", VARCHAR)
                .primaryKey("id")
                .execute();

        ctx.createTableIfNotExists("factions")
                .column("name", VARCHAR)
                .column("motd", VARCHAR)
                .column("level", INTEGER)
                .column("last_raid", BIGINT)
                .column("locale", VARCHAR)
                .primaryKey("name")
                .execute();

        ctx.createTableIfNotExists("faction_members")
                .column("factionName", VARCHAR)
                .column("uuid", VARCHAR)
                .column("rank", VARCHAR)
                .primaryKey("uuid")
                .execute();

        ctx.createTableIfNotExists("trusted_players")
                .column("island_id", INTEGER)
                .column("uuid", VARCHAR)
                .execute();

        ctx.createTableIfNotExists("defence_locations")
                .column("uuid", VARCHAR)
                .column("type", VARCHAR)
                .column("factionName", VARCHAR)
                .column("x", INTEGER)
                .column("y", INTEGER)
                .column("z", INTEGER)
                .execute();

        ctx.createTableIfNotExists("audit_logs")
                .column("factionName", VARCHAR)
                .column("type", VARCHAR)
                .column("uuid", VARCHAR)
                .column("replacements", VARCHAR)
                .column("timestamp", BIGINT)
                .execute();

        ctx.createTableIfNotExists("faction_bans")
                .column("factionName", VARCHAR)
                .column("uuid", VARCHAR)
                .execute();

        ctx.createTableIfNotExists("faction_invites")
                .column("factionName", VARCHAR)
                .column("uuid", VARCHAR)
                .column("inviter", VARCHAR)
                .column("type", VARCHAR)
                .column("accepted", INTEGER)
                .column("timestamp", BIGINT)
                .execute();

        ctx.createTableIfNotExists("notifications")
                .column("uuid", VARCHAR)
                .column("type", VARCHAR)
                .column("replacements", VARCHAR)
                .column("timestamp", BIGINT)
                .execute();
    }
}
