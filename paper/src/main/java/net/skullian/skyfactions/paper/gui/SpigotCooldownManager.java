package net.skullian.skyfactions.paper.gui;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.gui.CooldownManager;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.execution.postprocessor.CommandPostprocessingContext;
import org.incendo.cloud.execution.postprocessor.CommandPostprocessor;
import org.incendo.cloud.services.type.ConsumerService;

@SuppressWarnings("UnstableApiUsage")
public class SpigotCooldownManager {
    /*
        Cooldown Post processor for using in commands
     */
    public static final class CooldownPostprocessor<C> implements CommandPostprocessor<C> {
        @Override
        public void accept(final @NonNull CommandPostprocessingContext<C> context) {
            if (context.commandContext().sender() instanceof CommandSourceStack css) {
                if (css.getSender() instanceof Player player) {
                    if (CooldownManager.COMMANDS.manage(SkyApi.getInstance().getUserManager().getUser(player.getUniqueId()))) {
                        ConsumerService.interrupt();
                    }
                }
            }
        }
    }
}
