package net.skullian.skyfactions.database.migrations;

import net.skullian.skyfactions.util.MigrationUtil;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;

public class V5__Remove_Trust_Primary_Keys extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        DSLContext ctx = MigrationUtil.getCtx(context);

        ctx.alterTable("trusted_players")
                .dropPrimaryKey()
                .execute();
    }
}
