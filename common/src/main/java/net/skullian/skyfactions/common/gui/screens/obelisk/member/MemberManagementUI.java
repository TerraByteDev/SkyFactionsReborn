package net.skullian.skyfactions.common.gui.screens.obelisk.member;

import lombok.Builder;
import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.GUIEnums;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.EmptyItem;
import net.skullian.skyfactions.common.gui.items.impl.BaseSkyItem;
import net.skullian.skyfactions.common.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.common.gui.items.obelisk.member_manage.MemberPaginationItem;
import net.skullian.skyfactions.common.gui.screens.PaginatedScreen;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MemberManagementUI extends PaginatedScreen {
    private final Faction faction;

    @Builder
    public MemberManagementUI(SkyUser player, Faction faction) {
        super(GUIEnums.OBELISK_MEMBER_MANAGEMENT_GUI.getPath(), player);
        this.faction = faction;
    }

    public static void promptPlayer(SkyUser player, Faction faction) {
        try {
            MemberManagementUI.builder().player(player).faction(faction).build().show();
        } catch (IllegalArgumentException error) {
            SLogger.fatal("Failed to create Member Management GUI for player {} - {}", player.getUniqueId(), error);
            Messages.ERROR.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "operation", "open the member management GUI", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    public BaseSkyItem handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID().toLowerCase(Locale.ROOT)) {
            case "prompt", "border" ->
                    new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "back" ->
                    new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "faction", player);
            default -> null;
        };
    }

    @NotNull
    @Override
    public List<BaseSkyItem> getModels(SkyUser player, ItemData data) {
        List<BaseSkyItem> items = new ArrayList<>();

        String oldTitle = data.getNAME();

        for (SkyUser member : faction.getAllMembers()) {
            String title = data.getNAME();

            // Skullians: please god, let this be the last ever fucking hack in this plugin
            data.setNAME(title.replace(player.getName(), member.getName())); // guiapi will auto place the gui viewer's name, so we just replace it here hehe
            items.add(new MemberPaginationItem(data, GUIAPI.createItem(data, member.getUniqueId()), faction.getRank(member.getUniqueId()), member, player, faction));

            data.setNAME(oldTitle);
        }

        return items;
    }
}
