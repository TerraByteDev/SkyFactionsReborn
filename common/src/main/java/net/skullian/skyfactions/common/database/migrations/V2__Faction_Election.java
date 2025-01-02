package net.skullian.skyfactions.common.database.migrations;

import net.skullian.skyfactions.common.config.types.Settings;
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
                .column("factionName", VARCHAR(Settings.FACTION_CREATION_MAX_LENGTH.getInt()))
                .column("endDate", DATE)
                .primaryKey("id")
                .execute();

        //tx.createIndexIfNotExists("factionElectionsFactionNameIndex")
        //        .on("faction_elections", "factionName")
        //        .execute();

        ctx.execute("CREATE INDEX factionElectionsFactionNameIndex ON faction_elections (factionName);"); // workaround for now

        ctx.createTableIfNotExists("election_votes")
                .column("election", INTEGER)
                .column("uuid", CLOB(36))
                .column("votedFor", CLOB(36))
                .primaryKey("election")
                .constraint(DSL.foreignKey("election").references("faction_elections", "id").onDeleteCascade())
                .execute();
    }
}
