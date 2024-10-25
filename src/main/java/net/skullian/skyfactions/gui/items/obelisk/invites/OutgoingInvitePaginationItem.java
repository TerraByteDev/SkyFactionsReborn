package net.skullian.skyfactions.gui.items.obelisk.invites;

import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.db.InviteData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.util.ErrorHandler;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.List;

public class OutgoingInvitePaginationItem extends AbstractItem {

    private String NAME;
    private String SOUND;
    private int PITCH;
    private List<String> LORE;
    private ItemStack STACK;
    private InviteData DATA;

    public OutgoingInvitePaginationItem(ItemData data, ItemStack STACK, InviteData inviteData) {
        this.NAME = data.getNAME();
        this.SOUND = data.getSOUND();
        this.PITCH = data.getPITCH();
        this.LORE = data.getLORE();
        this.STACK = STACK;
        this.DATA = inviteData;
    }

    @Override
    public ItemProvider getItemProvider() {
        ItemBuilder builder = new ItemBuilder(STACK)
                .setDisplayName(TextUtility.color(NAME));

        for (String loreLine : LORE) {
            builder.addLoreLines(TextUtility.color(loreLine
                    .replace("%inviter%", DATA.getInviter().getName())
                    .replace("%player_name%", DATA.getPlayer().getName())
                    .replace("%timestamp%", TextUtility.formatExtendedElapsedTime(DATA.getTimestamp()))
            ));
        }

        return builder;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.setCancelled(true);

        if (!SOUND.equalsIgnoreCase("none")) {
            SoundUtil.playSound(player, SOUND, PITCH, 1);
        }

        if (clickType.isRightClick()) {
            event.getInventory().close();

            FactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
                if (faction == null) {
                    Messages.ERROR.send(player, "%operation%", "get your Faction", "FACTION_NOT_FOUND");
                    return;
                } else if (ex != null) {
                    ErrorHandler.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                    return;
                }

                if (faction == null) {
                    Messages.ERROR.send(player, "%operation%", "revoke a Faction invite", "%debug%", "FACTION_NOT_EXIST");
                } else {
                    faction.revokeInvite(DATA, player);
                    Messages.FACTION_INVITE_REVOKE_SUCCESS.send(player, "%player_name%", DATA.getPlayer().getName());
                }
            });
        }
    }

}
