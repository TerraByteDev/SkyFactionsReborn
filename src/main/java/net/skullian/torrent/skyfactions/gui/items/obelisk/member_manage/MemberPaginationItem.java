package net.skullian.torrent.skyfactions.gui.items.obelisk.member_manage;

import net.skullian.torrent.skyfactions.config.types.Messages;
import net.skullian.torrent.skyfactions.gui.data.ItemData;
import net.skullian.torrent.skyfactions.gui.obelisk.member.ManageMemberUI;
import net.skullian.torrent.skyfactions.util.SoundUtil;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
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

public class MemberPaginationItem extends AbstractItem {

    private String NAME;
    private String SOUND;
    private int PITCH;
    private List<String> LORE;
    private String RANK_TITLE;
    private ItemStack STACK;
    private OfflinePlayer SUBJECT;
    private Player ACTOR;

    public MemberPaginationItem(ItemData data, ItemStack stack, String rankTitle, OfflinePlayer player, Player actor) {
        this.NAME = data.getNAME();
        this.SOUND = data.getSOUND();
        this.PITCH = data.getPITCH();
        this.LORE = data.getLORE();
        this.RANK_TITLE = rankTitle;
        this.STACK = stack;
        this.SUBJECT = player;
        this.ACTOR = actor;
    }

    @Override
    public ItemProvider getItemProvider() {
        ItemBuilder builder = new ItemBuilder(STACK)
                .setDisplayName(TextUtility.color(NAME));

        for (String loreLine : LORE) {
            builder.addLoreLines(TextUtility.color(loreLine.replace("%player_rank%", TextUtility.color(RANK_TITLE))));
        }
        if (SUBJECT.getUniqueId().equals(ACTOR.getUniqueId())) {
            builder.addLoreLines(TextUtility.color(Messages.FACTION_MANAGE_SELF_DENY_LORE.get()));
        }

        return builder;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.setCancelled(true);

        if (!SOUND.equalsIgnoreCase("none")) {
            SoundUtil.playSound(player, SOUND, PITCH, 1);
        }

        if (SUBJECT.getUniqueId().equals(player.getUniqueId())) {
            Messages.FACTION_MANAGE_SELF_DENY.send(player);
        } else {
            ManageMemberUI.promptPlayer(player, SUBJECT);
        }
    }
}
