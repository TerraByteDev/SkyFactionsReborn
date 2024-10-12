package net.skullian.skyfactions.gui.items.obelisk.invites;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.NotificationAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.faction.JoinRequestData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.util.ErrorHandler;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FactionPlayerJoinRequestConfirmItem extends AbstractItem {

    private String NAME;
    private String SOUND;
    private int PITCH;
    private List<String> LORE;
    private ItemStack STACK;
    private JoinRequestData DATA;

    public FactionPlayerJoinRequestConfirmItem(ItemData data, ItemStack stack, JoinRequestData joinRequestData) {
        this.NAME = data.getNAME();
        this.SOUND = data.getSOUND();
        this.PITCH = data.getPITCH();
        this.LORE = data.getLORE();
        this.STACK = stack;
        this.DATA = joinRequestData;
    }

    @Override
    public ItemProvider getItemProvider() {
        ItemBuilder builder = new ItemBuilder(STACK)
                .setDisplayName(TextUtility.color(NAME));

        for (String loreLine : LORE) {
            builder.addLoreLines(TextUtility.color(loreLine));
        }

        return builder;
    }


    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.setCancelled(true);

        if (!SOUND.equalsIgnoreCase("none")) {
            SoundUtil.playSound(player, SOUND, PITCH, 1);
        }
        event.getInventory().close();

        FactionAPI.getFaction(DATA.getFactionName()).whenComplete((faction, ex) -> {
            if (faction == null) {
                Messages.ERROR.send(player, "%operation%", "get your Faction", "FACTION_NOT_FOUND");
                return;
            } else if (ex != null) {
                ErrorHandler.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            }

            CompletableFuture.allOf(
                    faction.addFactionMember(player.getUniqueId()),
                    SkyFactionsReborn.databaseHandler.revokeInvite(DATA.getFactionName(), player.getUniqueId(), "incoming")
            ).whenComplete((ignored, exc) -> {
                if (exc != null) {
                    ErrorHandler.handleError(player, "accept a join request", "SQL_FACTION_GET", exc);
                    return;
                }

                Messages.PLAYER_FACTION_JOIN_SUCCESS.send(player, "%faction_name%", DATA.getFactionName());
                NotificationAPI.factionInviteStore.replace(faction.getName(), (NotificationAPI.factionInviteStore.get(faction.getName()) - 1));
            });
        });
    }

}
