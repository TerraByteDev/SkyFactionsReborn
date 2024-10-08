package net.skullian.torrent.skyfactions.gui.items.obelisk.invites;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.api.FactionAPI;
import net.skullian.torrent.skyfactions.config.types.Messages;
import net.skullian.torrent.skyfactions.db.InviteData;
import net.skullian.torrent.skyfactions.faction.AuditLogType;
import net.skullian.torrent.skyfactions.gui.data.ItemData;
import net.skullian.torrent.skyfactions.util.ErrorHandler;
import net.skullian.torrent.skyfactions.util.SoundUtil;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlayerIncomingInviteAccept extends AbstractItem {

    private String NAME;
    private String SOUND;
    private int PITCH;
    private List<String> LORE;
    private ItemStack STACK;
    private InviteData DATA;

    public PlayerIncomingInviteAccept(ItemData data, ItemStack stack, InviteData inviteData) {
        this.NAME = data.getNAME();
        this.SOUND = data.getSOUND();
        this.PITCH = data.getPITCH();
        this.LORE = data.getLORE();
        this.STACK = stack;
        this.DATA = inviteData;
    }

    @Override
    public ItemProvider getItemProvider() {
        ItemBuilder builder = new ItemBuilder(STACK)
                .setDisplayName(TextUtility.color(NAME));

        for (String loreLine : LORE) {
            builder.addLoreLines(TextUtility.color(loreLine));
        }

        return builder;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.setCancelled(true);

        if (!SOUND.equalsIgnoreCase("none")) {
            SoundUtil.playSound(player, SOUND, PITCH, 1);
        }
        event.getInventory().close();

        FactionAPI.isInFaction(player).whenComplete((isInFaction, ex) -> {
            if (ex != null) {
                ErrorHandler.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            } else if (isInFaction) {
                Messages.ALREADY_IN_FACTION.send(player);
                return;
            }

            FactionAPI.getFaction(DATA.getFactionName()).whenComplete((faction, throwable) -> {
                if (throwable != null) {
                    ErrorHandler.handleError(player, "get the Faction", "SQL_FACTION_GET", throwable);
                    return;
                }

                CompletableFuture.allOf(
                        SkyFactionsReborn.db.revokeInvite(DATA.getFactionName(), player.getUniqueId(), "outgoing"),
                        faction.addFactionMember(player.getUniqueId()),
                        faction.createAuditLog(player.getUniqueId(), AuditLogType.INVITE_ACCEPT, "%player_name%", player.getName())
                ).whenComplete((ignored, exce) -> {
                    if (exce != null) {
                        ErrorHandler.handleError(player, "accept an invite", "SQL_INVITE_ACEPT", exce);
                        return;
                    }

                    Messages.PLAYER_FACTION_JOIN_SUCCESS.send(player, "%faction_name%", player.getName());
                });
            });
        });
    }
}
