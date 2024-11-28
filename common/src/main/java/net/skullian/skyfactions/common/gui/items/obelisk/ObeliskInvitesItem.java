package net.skullian.skyfactions.common.gui.items.obelisk;

import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.impl.old.SkyItem;
import net.skullian.skyfactions.core.gui.screens.obelisk.invites.FactionInviteTypeSelectionUI;
import net.skullian.skyfactions.core.gui.screens.obelisk.invites.PlayerInviteTypeSelectionUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ObeliskInvitesItem extends SkyItem {

    private String TYPE;
    private Faction FACTION;

    public ObeliskInvitesItem(ItemData data, ItemStack stack, String type, Faction faction, Player player) {
        super(data, stack, player, null);
        
        this.TYPE = type;
        this.FACTION = faction;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (TYPE.equals("faction")) {
            if (FACTION.isOwner(player) || FACTION.isAdmin(player) || FACTION.isModerator(player)) {
                FactionInviteTypeSelectionUI.promptPlayer(player);
            } else {
                Messages.OBELISK_GUI_DENY.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "rank", Messages.FACTION_MODERATOR_TITLE.get(SpigotPlayerAPI.getLocale(player.getUniqueId())));
            }
        } else if (TYPE.equals("player")) {
            PlayerInviteTypeSelectionUI.promptPlayer(player);
        }
    }


}
