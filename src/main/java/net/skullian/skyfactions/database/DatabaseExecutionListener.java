package net.skullian.skyfactions.database;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.util.ErrorUtil;
import net.skullian.skyfactions.util.SLogger;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;

public class DatabaseExecutionListener implements ExecuteListener {
    @Override
    public void start(ExecuteContext ctx) {
        SLogger.info("Successfully connected to Database.");
        SkyFactionsReborn.databaseManager.closed = false;
    }

    @Override
    public void end(ExecuteContext ctx) {
        SLogger.fatal("Database connection closed!");
        SkyFactionsReborn.databaseManager.closed = true;
    }

    @Override
    public void exception(ExecuteContext ctx) {
        ErrorUtil.handleError(ctx.exception());
    }
}
