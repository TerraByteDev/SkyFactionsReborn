package net.skullian.skyfactions.common.database.flyway;

import net.skullian.skyfactions.api.SkyApi;
import org.flywaydb.core.api.migration.Context;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

public class FlywayUtils {

    public static DSLContext getContext(Context context) {
        Configuration configuration = new DefaultConfiguration()
                .set(context.getConnection())
                .set(System.getProperty("net.skullian.skyfactions.codegen").equals("true")
                        ? SQLDialect.SQLITE
                        //: SkyApi.getInstance().getDatabaseService().getDialect()
                        : SQLDialect.MYSQL
                );

        return DSL.using(configuration);
    }
}
