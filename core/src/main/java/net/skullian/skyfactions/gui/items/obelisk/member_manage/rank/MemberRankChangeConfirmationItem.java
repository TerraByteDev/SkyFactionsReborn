package net.skullian.skyfactions.gui.items.obelisk.member_manage.rank;

import lombok.Setter;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.api.PlayerAPI;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.faction.RankType;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MemberRankChangeConfirmationItem extends SkyItem {

    @Setter
    private RankType SELECTED;

    public MemberRankChangeConfirmationItem(ItemData data, ItemStack stack, Player player, Faction faction, OfflinePlayer subject, RankType currentType) {
        super(data, stack, player, List.of(faction, subject).toArray());

        this.SELECTED = currentType;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (SELECTED == null) return;
        player.closeInventory();

        Faction faction = (Faction) getOptionals()[0];
        OfflinePlayer subject = (OfflinePlayer) getOptionals()[1];

        faction.modifyPlayerRank(subject, SELECTED, player);
        Messages.RANK_CHANGE_SUCCESS.send(player, PlayerAPI.getLocale(player.getUniqueId()), "player_name", subject.getName(), "new_rank", faction.getRank(subject.getUniqueId()));
    }


}
