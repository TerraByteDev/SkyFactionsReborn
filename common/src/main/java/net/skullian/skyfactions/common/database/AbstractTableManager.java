package net.skullian.skyfactions.common.database;

import org.jooq.DSLContext;

import java.util.concurrent.Executor;

public abstract class AbstractTableManager {

    protected final DSLContext ctx;
    protected final Executor executor;

    public AbstractTableManager(DSLContext ctx, Executor executor) {
        this.ctx = ctx;
        this.executor = executor;
    }

}
