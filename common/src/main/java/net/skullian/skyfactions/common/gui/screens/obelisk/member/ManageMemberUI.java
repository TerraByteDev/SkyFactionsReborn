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
import net.skullian.skyfactions.common.gui.items.obelisk.member_manage.MemberBanItem;
import net.skullian.skyfactions.common.gui.items.obelisk.member_manage.MemberKickItem;
import net.skullian.skyfactions.common.gui.items.obelisk.member_manage.MemberRankItem;
import net.skullian.skyfactions.common.gui.screens.Screen;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class ManageMemberUI extends Screen {
    private final SkyUser subject;
    private final Faction faction;

    @Builder
    public ManageMemberUI(SkyUser player, SkyUser subject, Faction faction) {
        super(GUIEnums.OBELISK_MANAGE_MEMBER_GUI.getPath(), player);
        this.subject = subject;
        this.faction = faction;
    }

    public static void promptPlayer(SkyUser player, SkyUser subject, Faction faction) {
        try {
            ManageMemberUI.builder().player(player).subject(subject).faction(faction).build().show();
        } catch (IllegalArgumentException error) {
            SLogger.fatal("Failed to create Manage Member GUI for player {} - {}", player.getUniqueId(), error);
            Messages.ERROR.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "operation", "manage a member", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    public BaseSkyItem handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID().toLowerCase(Locale.ROOT)) {
            case "border", "player-head" ->
                    new EmptyItem(itemData, GUIAPI.createItem(itemData, subject.getUniqueId()), player);
            case "kick" ->
                    new MemberKickItem(itemData, GUIAPI.createItem(itemData, subject.getUniqueId()), subject, player, faction);
            case "ban" ->
                    new MemberBanItem(itemData, GUIAPI.createItem(itemData, subject.getUniqueId()), subject, player, faction);
            case "rank" ->
                    new MemberRankItem(itemData, GUIAPI.createItem(itemData, subject.getUniqueId()), subject, player, faction);
            case "back" ->
                    new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, subject.getUniqueId()), "faction", player);
            default -> null;
        };
    }
}
