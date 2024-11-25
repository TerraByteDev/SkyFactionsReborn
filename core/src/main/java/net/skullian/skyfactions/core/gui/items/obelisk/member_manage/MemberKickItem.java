package net.skullian.skyfactions.core.gui.items.obelisk.member_manage;

import net.skullian.skyfactions.core.config.types.Settings;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.common.faction.Faction;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.core.api.FactionAPI;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.common.faction.AuditLogType;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.impl.SkyItem;
import net.skullian.skyfactions.core.util.ErrorUtil;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import java.util.List;

public class MemberKickItem extends SkyItem {

    private OfflinePlayer SUBJECT;

    public MemberKickItem(ItemData data, ItemStack stack, OfflinePlayer player, Player viewer, Faction faction) {
        super(data, stack, viewer, List.of(faction).toArray());
        
        this.SUBJECT = player;
    }

    @Override
    public ItemBuilder process(ItemBuilder builder) {
        Faction faction = (Faction) getOptionals()[0];

        if (!Settings.FACTION_KICK_PERMISSIONS.getList().contains(faction.getRankType(getPLAYER().getUniqueId()).getRankValue())) {
            builder.addLoreLines(toList(Messages.FACTION_MANAGE_NO_PERMISSIONS_LORE.getStringList(SpigotPlayerAPI.getLocale(getPLAYER().getUniqueId()))));
        }

        return builder;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        FactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, exc) -> {
            event.getInventory().close();
            if (exc != null) {
                ErrorUtil.handleError(player, "kick a player", "SQL_FACTION_GET", exc);
                return;
            }

            if (faction != null) {
                if (faction.getAllMembers().contains(SUBJECT)) {
                    // todo discord support & or announce in chat?
                    faction.createAuditLog(SUBJECT.getUniqueId(), AuditLogType.PLAYER_KICK, "kicked", SUBJECT.getName(), "player", player.getName());
                    faction.kickPlayer(SUBJECT, player);

                    Messages.FACTION_MANAGE_KICK_SUCCESS.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "player", SUBJECT.getName());
                } else {
                    Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "kick a player", "debug", "FACTION_MEMBER_UNKNOWN");
                    event.getInventory().close();
                }
            } else {
                Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "kick a player", "debug", "FACTION_NOT_EXIST");
                event.getInventory().close();
            }
        });


    }
}
