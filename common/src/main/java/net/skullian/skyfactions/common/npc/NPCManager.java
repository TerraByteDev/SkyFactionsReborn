package net.skullian.skyfactions.common.npc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.island.impl.FactionIsland;
import net.skullian.skyfactions.common.island.impl.PlayerIsland;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.common.util.SkyLocation;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;

import net.skullian.skyfactions.common.npc.factory.SkyNPCFactory;
import net.skullian.skyfactions.common.util.text.TextUtility;

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

    public void spawnNPC(UUID playerUUID, PlayerIsland island) {
        if (!Settings.NPC_INTEGRATION_ENABLED.getBoolean()) return;
        if (playerNPCs.containsValue(playerUUID) || factory.getNPC("player-" + island.getId(), false) != null) return;
        
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID);
        SkyNPC npc = factory.create(
            "player-" + island.getId(),
            TextUtility.legacyColor(Settings.NPC_PLAYER_ISLANDS_NAME.getString().replace("player_name", player.getName()), SkyApi.getInstance().getPlayerAPI().getLocale(playerUUID), null),
            getOffsetLocation(island.getCenter(Settings.ISLAND_PLAYER_WORLD.getString()), Settings.NPC_PLAYER_ISLANDS_OFFSET.getIntegerList()),
            Settings.NPC_PLAYER_ISLANDS_SKIN.getString().replace("player_name", player.getName()),
            EntityType.valueOf(Settings.NPC_PLAYER_ISLANDS_ENTITY.getString()),
            false
        );

        playerNPCs.put(npc, playerUUID);
    }

    public void spawnNPC(Faction faction, FactionIsland island) {
        if (!Settings.NPC_INTEGRATION_ENABLED.getBoolean()) return;
        if (factionNPCs.containsValue(faction) || factory.getNPC("faction-" + island.getId(), true) != null) return;

        SkyNPC npc = factory.create(
            "faction-" + island.getId(),
            TextUtility.legacyColor(Settings.NPC_FACTION_ISLANDS_NAME.getString().replace("faction_name", faction.getName()), faction.getLocale(), null),
            getOffsetLocation(island.getCenter(Settings.ISLAND_FACTION_WORLD.getString()), Settings.NPC_FACTION_ISLANDS_OFFSET.getIntegerList()),
            Settings.NPC_FACTION_ISLANDS_SKIN.getString().replace("faction_owner", faction.getOwner().getName()),
            EntityType.valueOf(Settings.NPC_FACTION_ISLANDS_ENTITY.getString()),
            true
        );

        factionNPCs.put(npc, faction.getName());
    }

    private SkyLocation getOffsetLocation(SkyLocation center, List<Integer> offset) {
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
        new Exception(String.format("Attempted to use the %s NPC factory when %s was not present on the server!", plugin, plugin)).printStackTrace();
        SkyApi.disablePlugin();
    }
}