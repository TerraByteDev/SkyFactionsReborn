package net.skullian.skyfactions.common.database;

import net.kyori.adventure.text.Component;
import net.skullian.skyfactions.core.SkyFactionsReborn;
import net.skullian.skyfactions.core.util.ErrorUtil;
import org.bukkit.Bukkit;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;

public class DatabaseExecutionListener implements ExecuteListener {
    @Override
    public void exception(ExecuteContext ctx) {
        ErrorUtil.handleError(ctx.exception());
        SkyFactionsReborn.getDatabaseManager().closed = SkyFactionsReborn.getDatabaseManager().getDataSource().isClosed();

        new RuntimeException("Database is closed! Cannot allow player to join without risking dupes and unexpected functionalities. Kicking all online players.").printStackTrace();
        if (SkyFactionsReborn.getDatabaseManager().closed) {
            Bukkit.getOnlinePlayers().forEach((player) -> player.kick(Component.text("<red>A fatal error occurred. Please contact your server owners to check logs.")));
        }
    }
}
