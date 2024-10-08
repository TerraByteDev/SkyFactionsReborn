package net.skullian.torrent.skyfactions.gui.items.obelisk;

import net.skullian.torrent.skyfactions.api.FactionAPI;
import net.skullian.torrent.skyfactions.config.types.Messages;
import net.skullian.torrent.skyfactions.faction.Faction;
import net.skullian.torrent.skyfactions.gui.data.ItemData;
import net.skullian.torrent.skyfactions.gui.obelisk.member.MemberManagementUI;
import net.skullian.torrent.skyfactions.util.ErrorHandler;
import net.skullian.torrent.skyfactions.util.SoundUtil;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.List;

public class ObeliskMemberManagementItem extends AbstractItem {

    private String NAME;
    private String SOUND;
    private int PITCH;
    private List<String> LORE;
    private ItemStack STACK;
    private Faction FACTION;

    public ObeliskMemberManagementItem(ItemData data, ItemStack stack, Faction faction) {
        this.NAME = data.getNAME();
        this.SOUND = data.getSOUND();
        this.PITCH = data.getPITCH();
        this.LORE = data.getLORE();
        this.STACK = stack;
        this.FACTION = faction;
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
        if (FACTION.isOwner(player) || FACTION.isAdmin(player) || FACTION.isModerator(player)) {

            FactionAPI.getFaction(player).whenComplete((faction, ex) -> {
                if (faction == null) {
                    Messages.ERROR.send(player, "%operation%", "get your Faction", "FACTION_NOT_FOUND");
                    return;
                } else if (ex != null) {
                    ErrorHandler.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                    return;
                }

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
                if (faction.getOwner().equals(offlinePlayer) || faction.getAdmins().contains(offlinePlayer)) {
                    MemberManagementUI.promptPlayer(player);
                } else {
                    Messages.OBELISK_GUI_DENY.send(player, "%rank%", Messages.FACTION_ADMIN_TITLE.get());
                }
            });
        } else {
            Messages.OBELISK_GUI_DENY.send(player, "%rank%", Messages.FACTION_MODERATOR_TITLE.get());
        }
    }

}
