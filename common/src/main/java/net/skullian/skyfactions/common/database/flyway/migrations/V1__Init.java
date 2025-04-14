package net.skullian.skyfactions.common.database.flyway.migrations;

import net.skullian.skyfactions.common.database.flyway.FlywayUtils;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;

import static org.jooq.impl.SQLDataType.*;

public class V1__Init extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        DSLContext ctx = FlywayUtils.getContext(context);

        ctx.createTableIfNotExists("islands")
            .column("id", INTEGER) // Integer ID of all islands. Increments per island creation. Used to determine grid position of island area.
            .column("uuid", BLOB(16)) // byte[] (BLOB) UUID of the island owner.
            .column("last_raided", BIGINT) // Unix timestamp (long) of the last time the island was raided. Used to determine whether the island can be selected for a raid.
            .column("trusted_players", VARCHAR) // A JSON array string of trusted player UUIDs.
            .primaryKey("id")
            .execute();

        ctx.createTableIfNotExists("players")
            .column("uuid", BLOB(16)) // UUID (BLOB / byte[]) of this playerdata profile.
            .column("faction", BLOB(16)) // UUID (BLOB / byte[]) of the Faction this player belongs to.
            .column("last_raid", BIGINT) // Unix timestamp (long) of the last time this player has started a raid.
            .primaryKey("uuid")
            .execute();
    }
}
