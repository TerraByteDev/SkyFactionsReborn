package net.skullian.skyfactions.common.database.migrations;

import net.skullian.skyfactions.common.util.MigrationUtil;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import static org.jooq.impl.SQLDataType.*;

public class V2__Faction_Election extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        DSLContext ctx = MigrationUtil.getCtx(context);

        ctx.createTableIfNotExists("faction_elections")
                .column("id", INTEGER.identity(true))
                .column("factionName", VARCHAR(65535))
                .column("endDate", DATE)
                .primaryKey("id")
                .execute();

        ctx.createIndex("factionElectionsFactionNameIndex")
                .on("faction_elections", "factionName")
                .execute();

        ctx.createTableIfNotExists("election_votes")
                .column("election", INTEGER)
                .column("uuid", BLOB(16))
                .column("votedFor", CLOB(36))
                .primaryKey("election")
                .constraint(DSL.foreignKey("election").references("faction_elections", "id").onDeleteCascade())
                .execute();
    }
}
