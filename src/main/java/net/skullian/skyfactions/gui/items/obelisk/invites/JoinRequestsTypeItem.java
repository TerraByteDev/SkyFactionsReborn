package net.skullian.skyfactions.gui.items.obelisk.invites;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.obelisk.invites.JoinRequestsUI;
import net.skullian.skyfactions.gui.obelisk.invites.PlayerOutgoingRequestManageUI;
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

public class JoinRequestsTypeItem extends AbstractItem {

    private String NAME;
    private String SOUND;
    private int PITCH;
    private List<String> LORE;
    private ItemStack STACK;
    private String TYPE;

    public JoinRequestsTypeItem(ItemData data, ItemStack stack, String type) {
        this.NAME = data.getNAME();
        this.SOUND = data.getSOUND();
        this.PITCH = data.getPITCH();
        this.LORE = data.getLORE();
        this.STACK = stack;
        this.TYPE = type;
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

        if (TYPE.equals("faction")) {
            JoinRequestsUI.promptPlayer(player);
        } else if (TYPE.equals("player")) {
            SkyFactionsReborn.databaseHandler.getPlayerOutgoingJoinRequest(player).whenComplete((joinRequest, ex) -> {
                if (ex != null) {
                    ErrorHandler.handleError(player, "get your outgoing join request", "SQL_JOIN_REQUEST_GET", ex);
                    return;
                }

                if (joinRequest == null) {
                    Messages.FACTION_JOIN_REQUEST_NOT_EXIST.send(player);
                } else {
                    PlayerOutgoingRequestManageUI.promptPlayer(player, joinRequest);
                }
            });
        }
    }

}
