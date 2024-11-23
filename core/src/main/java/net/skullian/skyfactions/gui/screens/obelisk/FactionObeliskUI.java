package net.skullian.skyfactions.gui.screens.obelisk;

import lombok.Builder;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.GUIAPI;
import net.skullian.skyfactions.config.types.GUIEnums;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.api.PlayerAPI;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.AirItem;
import net.skullian.skyfactions.gui.items.EmptyItem;
import net.skullian.skyfactions.gui.items.obelisk.*;
import net.skullian.skyfactions.gui.items.obelisk.defence.ObeliskDefencePurchaseItem;
import net.skullian.skyfactions.gui.items.obelisk.election.ObeliskFactionElectionMenuItem;
import net.skullian.skyfactions.gui.screens.Screen;
import net.skullian.skyfactions.util.ErrorUtil;
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
        FactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, exc) -> {
            if (exc != null) {
                ErrorUtil.handleError(player, "open faction obelisk", "GUI_LOAD_EXCEPTION", exc);
                return;
            }

            if (faction == null) {
                Messages.ERROR.send(player, PlayerAPI.getLocale(player.getUniqueId()), "operation", "open faction obelisk", "debug", "FACTION_NOT_FOUND");
                return;
            }

            try {
                player.setMetadata("inFactionRelatedUI", new FixedMetadataValue(SkyFactionsReborn.getInstance(), true));
                FactionObeliskUI.builder().player(player).faction(faction).build().show();
            } catch (Exception e) {
                e.printStackTrace();
                Messages.ERROR.send(player, PlayerAPI.getLocale(player.getUniqueId()), "operation", "open faction obelisk", "debug", "GUI_LOAD_EXCEPTION");
            }
        });
    }

    @Override
    protected Item handleItem(@NotNull ItemData itemData) {
        return switch (itemData.getITEM_ID()) {
            case "FACTION" ->
                    new ObeliskFactionOverviewItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            case "DEFENCES" ->
                    new ObeliskDefencePurchaseItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "faction", faction, player);

            case "RUNES_CONVERSION" ->
                    new ObeliskRuneItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "faction", player);

            case "MEMBER_MANAGEMENT" ->
                    new ObeliskMemberManagementItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), faction, player);

            case "AUDIT_LOGS" ->
                    new ObeliskAuditLogItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);

            case "INVITES" ->
                    new ObeliskInvitesItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "faction", faction, player);

            case "ELECTION" -> faction.isElectionRunning() ?
                    new ObeliskFactionElectionMenuItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), faction, player) :
                    new AirItem(player);

            case "BORDER" -> new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player);
            default -> null;
        };
    }
}
