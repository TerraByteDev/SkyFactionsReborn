package net.skullian.skyfactions.util;

import net.skullian.skyfactions.database.DatabaseManager;
import org.flywaydb.core.api.migration.Context;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

public class MigrationUtility {

    public static DSLContext getCtx(Context context) {
        boolean isCodegen = (System.getProperty("net.skullian.codegen").equals("true"));
        if (!isCodegen) return DatabaseManager.getCtx();

        Configuration configuration = new DefaultConfiguration()
                .set(context.getConnection())
                .set(SQLDialect.SQLITE);

        return DSL.using(configuration);
    }

}
