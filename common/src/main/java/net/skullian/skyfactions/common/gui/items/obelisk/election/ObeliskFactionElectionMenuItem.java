package net.skullian.skyfactions.common.gui.items.obelisk.election;

import net.kyori.adventure.text.Component;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.AsyncSkyItem;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

public class ObeliskFactionElectionMenuItem extends AsyncSkyItem {
    private final Faction FACTION;

    public ObeliskFactionElectionMenuItem(ItemData data, SkyItemStack stack, Faction faction, SkyUser player) {
        super(data, stack, player, null);
        this.FACTION = faction;
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        if (!FACTION.isElectionRunning()) {
            player.sendMessage(Component.text("There's no running elections right now"));
            return;
        }
    }
}
