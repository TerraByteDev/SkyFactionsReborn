package net.skullian.skyfactions.paper.event.defence;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.DefencesConfig;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.defence.Defence;
import net.skullian.skyfactions.common.gui.screens.defence.DefenceManageUI;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.common.util.SkyLocation;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import net.skullian.skyfactions.paper.event.armor.ArmorEquipEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Optional;
import java.util.UUID;

public class DefenceInteractionHandler implements Listener {

    @EventHandler
    public void onDefenceExplode(BlockExplodeEvent event) {
        for (Block block : event.blockList()) {
            SkyLocation skyLocation = SpigotAdapter.adapt(block.getLocation());
            if (SkyApi.getInstance().getDefenceAPI().isDefenceMaterial(skyLocation) && SkyApi.getInstance().getDefenceAPI().isDefence(skyLocation)) {

                Defence defence = SkyApi.getInstance().getDefenceAPI().getLoadedDefence(skyLocation);
                if (defence != null) defence.damage(Optional.empty());

                event.blockList().remove(block);
            }
        }
    }

    @EventHandler
    public void onDefenceBreak(BlockBreakEvent event) {
        if (SkyApi.getInstance().getDefenceAPI().isDefence(SpigotAdapter.adapt(event.getBlock().getLocation()))) {
            event.setCancelled(true);
            Messages.DEFENCE_DESTROY_DENY.send(event.getPlayer(), SkyApi.getInstance().getPlayerAPI().getLocale(event.getPlayer().getUniqueId()));
        }
    }

    @EventHandler
    public void onArmorEquip(ArmorEquipEvent event) {
        if (SkyApi.getInstance().getDefenceAPI().isDefence(SpigotAdapter.adapt(event.getItem()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    @SuppressWarnings("ConstantConditions")
    public void onDefenceInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || !event.hasBlock()) return;

        SkyLocation skyLocation = SpigotAdapter.adapt(event.getClickedBlock().getLocation());
        if (!SkyApi.getInstance().getDefenceAPI().isDefenceMaterial(skyLocation) || !SkyApi.getInstance().getDefenceAPI().isDefence(skyLocation)) return;

        Player player = event.getPlayer();
        SkyUser user = SkyApi.getInstance().getUserManager().getUser(player.getUniqueId());
        Defence defence = SkyApi.getInstance().getDefenceAPI().getLoadedDefence(skyLocation);

        if (defence != null) {
            if (defence.getData().isIS_FACTION()) {
                SkyApi.getInstance().getFactionAPI().getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
                    if (ex != null) {
                        SLogger.fatal("Failed to fetch {}'s faction!", player.getUniqueId(), ex);
                        return;
                    } else if (faction == null) return;
                        else if (!faction.getName().equals(defence.getData().getUUIDFactionName())) return;
                        else if (SkyApi.getInstance().getDefenceAPI().hasPermissions(DefencesConfig.PERMISSION_ACCESS_DEFENCE.getList(), user, faction))
                            Messages.DEFENCE_INSUFFICIENT_PERMISSIONS.send(user, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()));

                    DefenceManageUI.promptPlayer(user, defence.getData(), defence.getStruct(), faction);
                });
            } else {
                if (UUID.fromString(defence.getData().getUUIDFactionName()).equals(player.getUniqueId())) {
                    DefenceManageUI.promptPlayer(user, defence.getData(), defence.getStruct(), null);
                }
            }
        }
    }


}
