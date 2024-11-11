package net.skullian.skyfactions.gui.items.obelisk.member_manage;

import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MemberRankItem extends SkyItem {
    public MemberRankItem(ItemData data, ItemStack stack, Player player, Object[] optionals, String type, Faction faction) {
        super(data, stack, player, List.of(type, faction).toArray());
    }

    @Override
    public Object[] replacements() {
        String type = (String) getOptionals()[0];
        Faction faction = (Faction) getOptionals()[1];
        return List.of(
                "is_selected", faction.getRankType(getPLAYER().getUniqueId()).getRankValue().equalsIgnoreCase(type) ? Messages.FACTION_MANAGE_RANK_SELECTED.getString(PlayerHandler.getLocale(getPLAYER().getUniqueId()))
                        : ""
        ).toArray();
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        player.getInventory().close();
    }
