package net.skullian.skyfactions.common.database;

import org.jooq.DSLContext;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.Executor;

public abstract class AbstractTableManager {

    protected final DSLContext ctx;
    protected final Executor executor;

    public AbstractTableManager(DSLContext ctx, Executor executor) {
        this.ctx = ctx;
        this.executor = executor;
    }

    public static UUID fromBytes(byte[] uuid) {
        return UUID.nameUUIDFromBytes(uuid);
    }

    public static byte[] fromUUID(UUID uuid) {
        ByteBuffer bb = ByteBuffer.allocate(16);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

        return bb.array();
    }

}
