package net.skullian.skyfactions.common.util;

import net.skullian.skyfactions.common.api.SkyApi;
import org.flywaydb.core.api.migration.Context;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

public class MigrationUtil {

    public static DSLContext getCtx(Context context) {
        Configuration configuration = new DefaultConfiguration()
                .set(context.getConnection())
                .set(SkyApi.getInstance().getDatabaseManager().getDialect());

        return DSL.using(configuration);
    }

}
