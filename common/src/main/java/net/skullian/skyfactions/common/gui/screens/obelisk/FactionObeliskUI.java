package net.skullian.skyfactions.common.gui.screens.obelisk;

import lombok.Builder;
import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.GUIEnums;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.AirItem;
import net.skullian.skyfactions.common.gui.items.EmptyItem;
import net.skullian.skyfactions.common.gui.items.impl.BaseSkyItem;
import net.skullian.skyfactions.common.gui.items.obelisk.*;
import net.skullian.skyfactions.common.gui.items.obelisk.defence.ObeliskDefencePurchaseItem;
import net.skullian.skyfactions.common.gui.items.obelisk.election.ObeliskFactionElectionMenuItem;
import net.skullian.skyfactions.common.gui.screens.Screen;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import org.jetbrains.annotations.NotNull;

public class FactionObeliskUI extends Screen {
    private final Faction faction;

    @Builder
    public FactionObeliskUI(SkyUser player, Faction faction) {
        super(GUIEnums.OBELISK_FACTION_GUI.getPath(), player);
        this.faction = faction;

        ;
    }

    public static void promptPlayer(SkyUser player) {
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());
        SkyApi.getInstance().getFactionAPI().getFaction(player.getUniqueId()).whenComplete((faction, exc) -> {
            if (exc != null) {
                ErrorUtil.handleError(player, "open faction obelisk", "GUI_LOAD_EXCEPTION", exc);
                return;
            } else if (faction == null) {
                Messages.ERROR.send(player, locale, "operation", "open faction obelisk", "debug", "FACTION_NOT_FOUND");
                return;
            }

            try {
                player.addMetadata("inFactionRelatedUI");
                FactionObeliskUI.builder().player(player).faction(faction).build().show();
            } catch (Exception e) {
                e.printStackTrace();
                Messages.ERROR.send(player, locale, "operation", "open faction obelisk", "debug", "GUI_LOAD_EXCEPTION");
            }
        });
    }

    @Override
    public BaseSkyItem handleItem(@NotNull ItemData itemData) {
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
