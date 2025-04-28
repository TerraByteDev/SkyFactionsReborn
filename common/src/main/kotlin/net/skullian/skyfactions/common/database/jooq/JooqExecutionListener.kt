package net.skullian.skyfactions.common.database.jooq

import net.skullian.skyfactions.common.util.exception.ExceptionHandler
import org.jooq.ExecuteContext
import org.jooq.ExecuteListener

class JooqExecutionListener : ExecuteListener {

    override fun exception(ctx: ExecuteContext?) {
        if (ctx?.exception() != null) {
            ExceptionHandler.handleError(ctx.exception()!!, "handling database operations", "TODO")
        }

        //if ()
    }
}