package net.skullian.skyfactions.common.database;

import net.kyori.adventure.text.Component;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.util.ErrorUtil;
import org.bukkit.Bukkit;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;

public class DatabaseExecutionListener implements ExecuteListener {
    @Override
    public void exception(ExecuteContext ctx) {
        ErrorUtil.handleDatabaseError(ctx.exception());
        SkyApi.getInstance().getDatabaseManager().closed = SkyApi.getInstance().getDatabaseManager().getDataSource().isClosed();

        new RuntimeException("Database is closed! Cannot allow player to join without risking dupes and unexpected functionalities. Kicking all online players.").printStackTrace();
        if (SkyApi.getInstance().getDatabaseManager().closed) {
            Bukkit.getOnlinePlayers().forEach((player) -> player.kick(Component.text("<red>A fatal error occurred. Please contact your server owners to check logs.")));
        }
    }
}
