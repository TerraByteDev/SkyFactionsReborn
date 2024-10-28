package net.skullian.skyfactions.gui.items.obelisk;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.gui.obelisk.invites.FactionInviteTypeSelectionUI;
import net.skullian.skyfactions.gui.obelisk.invites.PlayerInviteTypeSelectionUI;

public class ObeliskInvitesItem extends SkyItem {

    private String TYPE;
    private Faction FACTION;

    public ObeliskInvitesItem(ItemData data, ItemStack stack, String type, Faction faction) {
        super(data, stack, null, null);
        
        this.TYPE = type;
        this.FACTION = faction;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (TYPE.equals("faction")) {
            if (FACTION.isOwner(player) || FACTION.isAdmin(player) || FACTION.isModerator(player)) {
                FactionInviteTypeSelectionUI.promptPlayer(player);
            } else {
                Messages.OBELISK_GUI_DENY.send(player, "%rank%", Messages.FACTION_MODERATOR_TITLE.get());
            }
        } else if (TYPE.equals("player")) {
            PlayerInviteTypeSelectionUI.promptPlayer(player);
        }
    }


}
