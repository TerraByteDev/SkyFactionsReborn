package net.skullian.skyfactions.database;

import net.skullian.skyfactions.util.ErrorUtil;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;

public class DatabaseExecutionListener implements ExecuteListener {
    @Override
    public void exception(ExecuteContext ctx) {
        ErrorUtil.handleError(ctx.exception());
    }
}
