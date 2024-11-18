package net.skullian.skyfactions.npc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import net.skullian.skyfactions.hooks.VaultAPIHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.island.impl.FactionIsland;
import net.skullian.skyfactions.island.impl.PlayerIsland;
import net.skullian.skyfactions.npc.factory.FancyNPCsFactory;
import net.skullian.skyfactions.npc.factory.SkyNPCFactory;
import net.skullian.skyfactions.npc.factory.ZNPCsPlusFactory;
import net.skullian.skyfactions.util.DependencyHandler;
import net.skullian.skyfactions.util.SLogger;
import net.skullian.skyfactions.util.text.TextUtility;

public class NPCManager {

    private final Map<SkyNPC, UUID> playerNPCs = new HashMap<>();
    private final Map<SkyNPC, Faction> factionNPCs = new HashMap<>();

    public final SkyNPCFactory factory;

    public NPCManager() {
        this.factory = getFactory();
    }
    
    public void onClick(SkyNPC npc, Player player) {
        boolean isFaction = playerNPCs.containsKey(npc); // sketchy, but it works (i hope 😭)

        if (isFaction) {
            Faction faction = factionNPCs.get(npc);
            if (faction == null) return; // probably only when a non-per-island npc is clicked.

            if (!faction.isInFaction(player.getUniqueId())) {
                Messages.NPC_ACCESS_DENY.send(player, PlayerHandler.getLocale(player.getUniqueId()));
                return;
            }

            process(Messages.NPC_FACTION_ISLANDS_ACTIONS.getStringList(PlayerHandler.getLocale(player.getUniqueId())), player);
        } else {
            UUID owner = playerNPCs.get(npc);
            if (owner == null) return;

            if (!owner.equals(player.getUniqueId())) {
                Messages.NPC_ACCESS_DENY.send(player, PlayerHandler.getLocale(player.getUniqueId()));
                return;
            }

            process(Messages.NPC_PLAYER_ISLANDS_ACTIONS.getStringList(PlayerHandler.getLocale(player.getUniqueId())), player);
        }
    }

    public void spawnNPC(UUID playerUUID, PlayerIsland island) {
        if (!Settings.NPC_INTEGRATION_ENABLED.getBoolean()) return;
        if (playerNPCs.containsValue(playerUUID) || factory.getNPC("player-" + island.getId(), false) != null) return;
        
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID);
        SkyNPC npc = factory.create(
            "player-" + island.getId(),
            TextUtility.legacyColor(Settings.NPC_PLAYER_ISLANDS_NAME.getString().replace("player_name", player.getName()), PlayerHandler.getLocale(playerUUID), player),
            getOffsetLocation(island.getCenter(Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString())), Settings.NPC_PLAYER_ISLANDS_OFFSET.getIntegerList()),
            Settings.NPC_PLAYER_ISLANDS_SKIN.getString().replace("player_name", Bukkit.getOfflinePlayer(playerUUID).getName()),
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
            getOffsetLocation(island.getCenter(Bukkit.getWorld(Settings.ISLAND_FACTION_WORLD.getString())), Settings.NPC_FACTION_ISLANDS_OFFSET.getIntegerList()),
            Settings.NPC_FACTION_ISLANDS_SKIN.getString().replace("faction_owner", faction.getOwner().getName()),
            EntityType.valueOf(Settings.NPC_FACTION_ISLANDS_ENTITY.getString()),
            true
        );

        factionNPCs.put(npc, faction);
    }

    private Location getOffsetLocation(Location center, List<Integer> offset) {
        center.add(
            offset.get(0),
            offset.get(1),
            offset.get(2)
        );

        return center;
    }

    private void process(List<String> actions, Player player) {
        String locale = PlayerHandler.getLocale(player.getUniqueId());
        for (String request : actions) {
            String[] parts = request.split("\\[([^]]+)\\]: (.+)");
            String action = parts[0].trim().toLowerCase();
            String cmd = parts[1].trim();

            switch(action) {

                case "[console]":
                    Bukkit.dispatchCommand(
                        Bukkit.getServer().getConsoleSender(),
                        TextUtility.legacyColor(cmd, locale, player)
                    );
                    break;
                
                case "[player]":
                    player.performCommand(TextUtility.legacyColor(cmd, locale, player));
                    break;
                
                case "[message]":
                    player.sendMessage(TextUtility.legacyColor(cmd, locale, player));
                    break;

                case "[givepermission]":
                    if (DependencyHandler.isEnabled("Vault")) {
                        VaultAPIHook.addPermission(player, cmd);
                    } else SLogger.warn("Attempted to give player a permission node on NPC interact when Vault is not present!");
                    break;

                case "[removepermission]":
                    if (DependencyHandler.isEnabled("Vault")) {
                        VaultAPIHook.removePermission(player, cmd);
                    } else SLogger.warn("Attempted to remove a permission node from a player on NPC interact when Vault is not present!");
                    break;


            }

        }
    }

    private SkyNPCFactory getFactory() {
        if (!Settings.NPC_INTEGRATION_ENABLED.getBoolean()) return null;

        switch (Settings.NPC_FACTORY.getString().toLowerCase()) {

            case "znpcsplus":
                if (DependencyHandler.isEnabled("ZNPCsPlus")) {
                    ZNPCsPlusFactory npcFactory = new ZNPCsPlusFactory();
                    Bukkit.getServer().getPluginManager().registerEvents(npcFactory, SkyFactionsReborn.getInstance());

                    return new ZNPCsPlusFactory();
                } else alert("ZNPCsPlus");

                break;

            case "fancynpcs":
                if (DependencyHandler.isEnabled("FancyNPCs")) {
                    FancyNPCsFactory npcFactory = new FancyNPCsFactory();
                    Bukkit.getServer().getPluginManager().registerEvents(npcFactory, SkyFactionsReborn.getInstance());

                    return new FancyNPCsFactory();
                } else alert("FancyNPCs");

                break;
            
            default:
                new Exception("Unknown NPC Factory: " + Settings.NPC_FACTORY.getString()).printStackTrace();
                Bukkit.getPluginManager().disablePlugin(SkyFactionsReborn.getInstance());
        }

        return null;
    }

    public CompletableFuture<Integer> updateNPCs(boolean remove) {
        return CompletableFuture.supplyAsync(() -> {
            AtomicInteger affected = new AtomicInteger(0);
            if (remove && Settings.NPC_INTEGRATION_ENABLED.getBoolean()) {
                throw new RuntimeException("Attempted to disable NPCs when the integration is still enabled in the config!");
            } else if (!remove && !Settings.NPC_INTEGRATION_ENABLED.getBoolean()) {
                throw new RuntimeException("Attempted to reload NPCs when the integration is not enabled in the config!");
            }

            IntStream.range(0, SkyFactionsReborn.getDatabaseManager().getPlayerIslandManager().cachedPlayerIslandID)
                    .forEach(i -> {
                        SkyNPC npc = factory.getNPC("player-" + i, false);
                        if (npc == null) return;
                        if (remove) {
                            npc.remove();
                            affected.incrementAndGet();
                            return;
                        }
                        SkyFactionsReborn.getDatabaseManager().getPlayerIslandManager().getOwnerOfIsland(new PlayerIsland(i))
                                .whenComplete((uuid, ex) -> {
                                    if (ex != null) {
                                        SLogger.fatal("Failed to get owner of island [{}] in order to refresh their NPC.", i);
                                        ex.printStackTrace();
                                        return;
                                    }
                                    OfflinePlayer owner = Bukkit.getOfflinePlayer(uuid);
                                    npc.updateDisplayName(TextUtility.legacyColor(Settings.NPC_PLAYER_ISLANDS_NAME.getString().replace("player_name", owner.getName()), PlayerHandler.getLocale(uuid), owner));
                                    npc.updateEntityType(EntityType.valueOf(Settings.NPC_PLAYER_ISLANDS_ENTITY.getString()));
                                    npc.updateSkin(Settings.NPC_PLAYER_ISLANDS_SKIN.toString().replace("player_name", owner.getName()));
                                    affected.incrementAndGet();
                                });
                    });

            IntStream.range(0, SkyFactionsReborn.getDatabaseManager().getFactionIslandManager().cachedFactionIslandID)
                    .forEach(i -> {
                        SkyNPC npc = factory.getNPC("faction-" + i, true);
                        if (npc == null) return;
                        if (remove) {
                            npc.remove();
                            affected.incrementAndGet();
                            return;
                        }
                        SkyFactionsReborn.getDatabaseManager().getFactionsManager().getFactionByIslandID(i)
                                .whenComplete((faction, ex) -> {
                                    if (ex != null) {
                                        SLogger.fatal("Failed to get faction owner of island [{}] in order to refresh their NPCs.", i);
                                        ex.printStackTrace();
                                        return;
                                    }
                                    npc.updateDisplayName(Settings.NPC_FACTION_ISLANDS_NAME.getString().replace("faction_name", faction.getName()));
                                    npc.updateEntityType(EntityType.valueOf(Settings.NPC_FACTION_ISLANDS_ENTITY.getString()));
                                    npc.updateSkin(Settings.NPC_FACTION_ISLANDS_SKIN.getString().replace("faction_owner", faction.getOwner().getName()));
                                    affected.incrementAndGet();
                                });
                    });

            return affected.get();
        });
    }

    private void alert(String plugin) {
        SLogger.fatal("----------------------- NPC EXCEPTION -----------------------");
        SLogger.fatal("There was an error initialising the NPC integration.");
        SLogger.fatal("Plugin will now disable.");
        SLogger.fatal("----------------------- NPC EXCEPTION -----------------------");
        new Exception(String.format("Attempted to use the %s NPC factory when %s was not present on the server!", plugin, plugin)).printStackTrace();
        Bukkit.getPluginManager().disablePlugin(SkyFactionsReborn.getInstance());
    }
}