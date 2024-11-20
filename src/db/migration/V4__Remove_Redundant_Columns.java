package db.migration;

import net.skullian.skyfactions.database.DatabaseManager;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;

public class V4__Remove_Redundant_Columns extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        DSLContext ctx = DatabaseManager.getCtx(context);

        ctx.alterTableIfExists("factionInvites")
                .dropColumn("accepted")
                .execute();

        ctx.alterTableIfExists("playerData")
                .dropColumn("faction")
                .execute();

    }
}
