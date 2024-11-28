package net.skullian.skyfactions.common.gui.screens.obelisk.member;

import lombok.Builder;
import net.skullian.skyfactions.core.api.SpigotGUIAPI;
import net.skullian.skyfactions.core.config.types.GUIEnums;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.EmptyItem;
import net.skullian.skyfactions.core.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.core.gui.items.obelisk.member_manage.MemberBanItem;
import net.skullian.skyfactions.core.gui.items.obelisk.member_manage.MemberKickItem;
import net.skullian.skyfactions.core.gui.items.obelisk.member_manage.MemberRankItem;
import net.skullian.skyfactions.common.gui.screens.Screen;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;

public class ManageMemberUI extends Screen {
    private final OfflinePlayer subject;
    private final Faction faction;

    @Builder
    public ManageMemberUI(Player player, OfflinePlayer subject, Faction faction) {
        super(player, GUIEnums.OBELISK_MANAGE_MEMBER_GUI.getPath());
        this.subject = subject;
        this.faction = faction;

        initWindow();
    }

    public static void promptPlayer(Player player, OfflinePlayer subject, Faction faction) {
        try {
            ManageMemberUI.builder().player(player).subject(subject).faction(faction).build().show();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "manage a member", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    @Nullable
    @Override
    protected Item handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID()) {
            case "BORDER", "PLAYER_HEAD" ->
                    new EmptyItem(itemData, SpigotGUIAPI.createItem(itemData, subject.getUniqueId()), player);
            case "KICK" ->
                    new MemberKickItem(itemData, SpigotGUIAPI.createItem(itemData, subject.getUniqueId()), subject, player, faction);
            case "BAN" ->
                    new MemberBanItem(itemData, SpigotGUIAPI.createItem(itemData, subject.getUniqueId()), subject, player, faction);
            case "RANK" ->
                    new MemberRankItem(itemData, SpigotGUIAPI.createItem(itemData, subject.getUniqueId()), subject, player, faction);
            case "BACK" ->
                    new ObeliskBackItem(itemData, SpigotGUIAPI.createItem(itemData, subject.getUniqueId()), "faction", player);
            default -> null;
        };
    }
}
