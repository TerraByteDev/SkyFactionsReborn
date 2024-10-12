package net.skullian.skyfactions.gui.items.obelisk;

import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.obelisk.invites.FactionInviteTypeSelectionUI;
import net.skullian.skyfactions.gui.obelisk.invites.PlayerInviteTypeSelectionUI;
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

public class ObeliskInvitesItem extends AbstractItem {

    private String NAME;
    private String SOUND;
    private int PITCH;
    private List<String> LORE;
    private ItemStack STACK;
    private String TYPE;
    private Faction FACTION;

    public ObeliskInvitesItem(ItemData data, ItemStack stack, String type, Faction faction) {
        this.NAME = data.getNAME();
        this.SOUND = data.getSOUND();
        this.PITCH = data.getPITCH();
        this.LORE = data.getLORE();
        this.STACK = stack;
        this.TYPE = type;
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
