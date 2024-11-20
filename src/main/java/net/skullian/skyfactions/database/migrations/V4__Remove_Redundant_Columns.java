package net.skullian.skyfactions.database.migrations;

import net.skullian.skyfactions.util.MigrationUtility;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;

public class V4__Remove_Redundant_Columns extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        DSLContext ctx = MigrationUtility.getCtx(context);

        ctx.alterTable("faction_invites")
                .dropColumn("accepted")
                .execute();

        ctx.alterTable("player_data")
                .dropColumn("faction")
                .execute();

    }
}
