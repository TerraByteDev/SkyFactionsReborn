package net.skullian.skyfactions.common.database.migrations;

import net.skullian.skyfactions.common.util.MigrationUtil;
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
                .column("uuid", VARCHAR(36))
                .column("level", INTEGER)
                .column("gems", INTEGER)
                .column("runes", INTEGER)
                .column("defenceCount", INTEGER)
                .column("last_raided", BIGINT)
                .column("last_raider", VARCHAR(36))
                .primaryKey("id")
                .execute();

        ctx.createTableIfNotExists("player_data")
                .column("uuid", VARCHAR(36))
                .column("faction", VARCHAR(65535))
                .column("discord_id", VARCHAR(18))
                .column("last_raid", BIGINT)
                .column("locale", VARCHAR(4))
                .primaryKey("uuid")
                .execute();

        ctx.createTableIfNotExists("faction_islands")
                .column("id", INTEGER)
                .column("factionName", VARCHAR(65535))
                .column("runes", INTEGER)
                .column("defenceCount", INTEGER)
                .column("gems", INTEGER)
                .column("last_raided", BIGINT)
                .column("last_raider", VARCHAR(36))
                .primaryKey("id")
                .execute();

        ctx.createTableIfNotExists("factions")
                .column("name", VARCHAR(65535))
                .column("motd", VARCHAR(65535))
                .column("level", INTEGER)
                .column("last_raid", BIGINT)
                .column("locale", VARCHAR(4))
                .primaryKey("name")
                .execute();

        ctx.createTableIfNotExists("faction_members")
                .column("factionName", VARCHAR(4))
                .column("uuid", VARCHAR(36))
                .column("rank", VARCHAR(128))
                .primaryKey("uuid")
                .execute();

        ctx.createTableIfNotExists("trusted_players")
                .column("island_id", INTEGER)
                .column("uuid", VARCHAR(36))
                .execute();

        ctx.createTableIfNotExists("defence_locations")
                .column("uuid", VARCHAR(36))
                .column("type", VARCHAR(128))
                .column("factionName", VARCHAR(65535))
                .column("x", INTEGER)
                .column("y", INTEGER)
                .column("z", INTEGER)
                .execute();

        ctx.createTableIfNotExists("audit_logs")
                .column("factionName", VARCHAR(65535))
                .column("type", VARCHAR(128))
                .column("uuid", VARCHAR(36))
                .column("replacements", VARCHAR(5120))
                .column("timestamp", BIGINT)
                .execute();

        ctx.createTableIfNotExists("faction_bans")
                .column("factionName", VARCHAR(65535))
                .column("uuid", VARCHAR(36))
                .execute();

        ctx.createTableIfNotExists("faction_invites")
                .column("factionName", VARCHAR(65535))
                .column("uuid", VARCHAR(36))
                .column("inviter", VARCHAR(36))
                .column("type", VARCHAR(128))
                .column("accepted", BOOLEAN)
                .column("timestamp", BIGINT)
                .execute();

        ctx.createTableIfNotExists("notifications")
                .column("uuid", VARCHAR(36))
                .column("type", VARCHAR(128))
                .column("replacements", VARCHAR(5120))
                .column("timestamp", BIGINT)
                .execute();
    }
}
