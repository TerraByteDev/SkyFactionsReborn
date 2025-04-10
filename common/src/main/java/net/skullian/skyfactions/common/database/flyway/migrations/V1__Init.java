package net.skullian.skyfactions.common.database.flyway.migrations;

import net.skullian.skyfactions.common.database.flyway.FlywayUtils;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;

public class V1__Init extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        DSLContext ctx = FlywayUtils.getContext(context);
    }
}
