package net.skullian.skyfactions.common.gui.screens.obelisk.member;

import lombok.Builder;
import net.skullian.skyfactions.core.api.SpigotGUIAPI;
import net.skullian.skyfactions.core.config.types.GUIEnums;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.data.PaginationItemData;
import net.skullian.skyfactions.core.gui.items.EmptyItem;
import net.skullian.skyfactions.core.gui.items.PaginationBackItem;
import net.skullian.skyfactions.core.gui.items.PaginationForwardItem;
import net.skullian.skyfactions.core.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.core.gui.items.obelisk.member_manage.MemberPaginationItem;
import net.skullian.skyfactions.core.gui.screens.confirmation.PaginatedScreen;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;

import java.util.ArrayList;
import java.util.List;

public class MemberManagementUI extends PaginatedScreen {
    private final Faction faction;

    @Builder
    public MemberManagementUI(Player player, Faction faction) {
        super(player, GUIEnums.OBELISK_MEMBER_MANAGEMENT_GUI.getPath());
        this.faction = faction;

        initWindow();
    }

    public static void promptPlayer(Player player, Faction faction) {
        try {
            MemberManagementUI.builder().player(player).faction(faction).build().show();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "open the member management GUI", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    protected Item handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID()) {
            case "PROMPT", "BORDER" ->
                    new EmptyItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "BACK" ->
                    new ObeliskBackItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), "faction", player);
            default -> null;
        };
    }

    @Nullable
    @Override
    protected Item handlePaginationItem(@NotNull PaginationItemData paginationItem) {
        return switch (paginationItem.getITEM_ID()) {
            case "FORWARD_BUTTON" ->
                    new PaginationForwardItem(paginationItem, SpigotGUIAPI.createItem(paginationItem, player.getUniqueId()));
            case "BACK_BUTTON" ->
                    new PaginationBackItem(paginationItem, SpigotGUIAPI.createItem(paginationItem, player.getUniqueId()));
            default -> null;
        };
    }

    @NotNull
    @Override
    protected List<Item> getModels(Player player, ItemData data) {
        List<Item> items = new ArrayList<>();

        String oldTitle = data.getNAME();

        for (OfflinePlayer member : faction.getAllMembers()) {
            String title = data.getNAME();

            // Skullians: please god, let this be the last ever fucking hack in this plugin
            data.setNAME(title.replace(player.getName(), member.getName())); // guiapi will auto place the gui viewer's name, so we just replace it here hehe
            items.add(new MemberPaginationItem(data, SpigotGUIAPI.createItem(data, member.getUniqueId()), faction.getRank(member.getUniqueId()), member, player, faction));

            data.setNAME(oldTitle);
        }

        return items;
    }
}
