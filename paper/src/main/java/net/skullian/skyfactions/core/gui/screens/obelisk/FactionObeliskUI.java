package net.skullian.skyfactions.core.gui.screens.obelisk;

import lombok.Builder;
import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.core.SkyFactionsReborn;
import net.skullian.skyfactions.core.api.SpigotFactionAPI;
import net.skullian.skyfactions.core.api.SpigotGUIAPI;
import net.skullian.skyfactions.core.config.types.GUIEnums;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.AirItem;
import net.skullian.skyfactions.core.gui.items.EmptyItem;
import net.skullian.skyfactions.core.gui.items.obelisk.*;
import net.skullian.skyfactions.core.gui.items.obelisk.defence.ObeliskDefencePurchaseItem;
import net.skullian.skyfactions.core.gui.items.obelisk.election.ObeliskFactionElectionMenuItem;
import net.skullian.skyfactions.core.gui.screens.Screen;
import net.skullian.skyfactions.core.util.ErrorUtil;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.Item;

public class FactionObeliskUI extends Screen {
    private final Faction faction;

    @Builder
    public FactionObeliskUI(Player player, Faction faction) {
        super(player, GUIEnums.OBELISK_FACTION_GUI.getPath());
        this.faction = faction;

        initWindow();
    }

    public static void promptPlayer(Player player) {
        SpigotFactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, exc) -> {
            if (exc != null) {
                ErrorUtil.handleError(player, "open faction obelisk", "GUI_LOAD_EXCEPTION", exc);
                return;
            }

            if (faction == null) {
                Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "open faction obelisk", "debug", "FACTION_NOT_FOUND");
                return;
            }

            try {
                player.setMetadata("inFactionRelatedUI", new FixedMetadataValue(SkyFactionsReborn.getInstance(), true));
                FactionObeliskUI.builder().player(player).faction(faction).build().show();
            } catch (Exception e) {
                e.printStackTrace();
                Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "open faction obelisk", "debug", "GUI_LOAD_EXCEPTION");
            }
        });
    }

    @Override
    protected Item handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID()) {
            case "FACTION" ->
                    new ObeliskFactionOverviewItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "DEFENCES" ->
                    new ObeliskDefencePurchaseItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), "faction", faction, player);

            case "RUNES_CONVERSION" ->
                    new ObeliskRuneItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), "faction", player);

            case "MEMBER_MANAGEMENT" ->
                    new ObeliskMemberManagementItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), faction, player);

            case "AUDIT_LOGS" ->
                    new ObeliskAuditLogItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), player);

            case "INVITES" ->
                    new ObeliskInvitesItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), "faction", faction, player);

            case "ELECTION" -> faction.isElectionRunning() ?
                    new ObeliskFactionElectionMenuItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), faction, player) :
                    new AirItem(player);

            case "BORDER" -> new EmptyItem(itemData, SpigotGUIAPI.createItem(itemData, player.getUniqueId()), player);
            default -> null;
        };
    }
}
