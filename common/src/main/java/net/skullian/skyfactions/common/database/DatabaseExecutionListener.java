package net.skullian.skyfactions.common.database;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.util.ErrorUtil;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;

public class DatabaseExecutionListener implements ExecuteListener {
    @Override
    public void exception(ExecuteContext ctx) {
        ErrorUtil.handleDatabaseError(ctx.exception());
        SkyApi.getInstance().getDatabaseManager().closed = SkyApi.getInstance().getDatabaseManager().getDataSource().isClosed();

        new RuntimeException("Database is closed! Cannot allow player to join without risking dupes and unexpected functionalities. Kicking all online players.").printStackTrace();
        if (SkyApi.getInstance().getDatabaseManager().closed) {
            SkyApi.getInstance().getPlayerAPI().getOnlinePlayers().forEach((player) -> player.kick(MiniMessage.miniMessage().deserialize(Messages.SERVER_NAME.getString(SkyApi.getInstance().getPlayerAPI().getLocale(player.getUuid())) + " <red>A fatal error has occurred. Please contact your server administrators.")));
        }
    }
}
