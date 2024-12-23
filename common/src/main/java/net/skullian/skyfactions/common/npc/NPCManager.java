package net.skullian.skyfactions.common.npc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import lombok.Getter;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.island.impl.FactionIsland;
import net.skullian.skyfactions.common.island.impl.PlayerIsland;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.common.util.SkyLocation;

import net.skullian.skyfactions.common.npc.factory.SkyNPCFactory;

@Getter
public abstract class NPCManager {

    private final Map<SkyNPC, UUID> playerNPCs = new HashMap<>();
    private final Map<SkyNPC, String> factionNPCs = new HashMap<>();

    public final SkyNPCFactory factory;

    public NPCManager() {
        this.factory = getFactory();
    }
    
    public void onClick(SkyNPC npc, SkyUser player) {
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());
        boolean isFaction = playerNPCs.containsKey(npc); // sketchy, but it works (i hope 😭)

        if (isFaction) {
            Faction faction = SkyApi.getInstance().getFactionAPI().getCachedFaction(factionNPCs.get(npc));
            if (faction == null) return; // probably only when a non-per-island npc is clicked.

            if (!faction.isInFaction(player.getUniqueId())) {
                Messages.NPC_ACCESS_DENY.send(player, locale);
                return;
            }

            process(Messages.NPC_FACTION_ISLANDS_ACTIONS.getStringList(locale), player);
        } else {
            UUID owner = playerNPCs.get(npc);
            if (owner == null) return;

            if (!owner.equals(player.getUniqueId())) {
                Messages.NPC_ACCESS_DENY.send(player, locale);
                return;
            }

            process(Messages.NPC_PLAYER_ISLANDS_ACTIONS.getStringList(locale), player);
        }
    }

    public abstract void spawnNPC(UUID playerUUID, PlayerIsland island);

    public abstract void spawnNPC(Faction faction, FactionIsland island);

    public SkyLocation getOffsetLocation(SkyLocation center, List<Integer> offset) {
        center.add(new SkyLocation(null, offset.get(0), offset.get(1), offset.get(2)));

        return center;
    }

    public abstract void process(List<String> actions, SkyUser player);

    public abstract SkyNPCFactory getFactory();

    public abstract CompletableFuture<Integer> updateNPCs(boolean remove);

    public void alert(String plugin) {
        SLogger.fatal("----------------------- NPC EXCEPTION -----------------------");
        SLogger.fatal("There was an error initialising the NPC integration.");
        SLogger.fatal("Plugin will now disable.");
        SLogger.fatal("----------------------- NPC EXCEPTION -----------------------");
        SLogger.fatal(new IllegalStateException(String.format("Attempted to use the %s NPC factory when %s was not present on the server!", plugin, plugin)));
        SkyApi.disablePlugin();
    }
}