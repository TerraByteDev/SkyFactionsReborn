package net.skullian.skyfactions.database.migrations;

import net.skullian.skyfactions.util.MigrationUtility;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;

import static org.jooq.impl.SQLDataType.*;

public class V3__Cooldowns extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        DSLContext ctx = MigrationUtility.getCtx(context);

        ctx.alterTable("factions")
                .addColumn("last_renamed", BIGINT)
                .execute();

        ctx.alterTable("islands")
                .addColumn("created", BIGINT)
                .execute();
    }
}
