package net.skullian.skyfactions.paper.npc;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.island.impl.FactionIsland;
import net.skullian.skyfactions.common.island.impl.PlayerIsland;
import net.skullian.skyfactions.common.npc.NPCManager;
import net.skullian.skyfactions.common.npc.SkyNPC;
import net.skullian.skyfactions.common.npc.factory.SkyNPCFactory;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.paper.util.DependencyHandler;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.common.util.text.TextUtility;
import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.paper.hooks.VaultAPIHook;
import net.skullian.skyfactions.paper.npc.factory.FancyNPCsFactory;
import net.skullian.skyfactions.paper.npc.factory.ZNPCsPlusFactory;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class SpigotNPCManager extends NPCManager {
    @Override
    public void spawnNPC(UUID playerUUID, PlayerIsland island) {
        if (!Settings.NPC_INTEGRATION_ENABLED.getBoolean()) return;
        if (getPlayerNPCs().containsValue(playerUUID) || factory.getNPC("player-" + island.getId(), false) != null) return;

        OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID);
        SkyNPC npc = factory.create(
                "player-" + island.getId(),
                TextUtility.legacyColor(Settings.NPC_PLAYER_ISLANDS_NAME.getString().replace("player_name", player.getName()), SkyApi.getInstance().getPlayerAPI().getLocale(playerUUID), null),
                getOffsetLocation(island.getCenter(Settings.ISLAND_PLAYER_WORLD.getString()), Settings.NPC_PLAYER_ISLANDS_OFFSET.getIntegerList()),
                Settings.NPC_PLAYER_ISLANDS_SKIN.getString().replace("player_name", player.getName()),
                EntityType.valueOf(Settings.NPC_PLAYER_ISLANDS_ENTITY.getString()),
                false
        );

        getPlayerNPCs().put(npc, playerUUID);
    }

    @Override
    public void spawnNPC(Faction faction, FactionIsland island) {
        if (!Settings.NPC_INTEGRATION_ENABLED.getBoolean()) return;
        if (getFactionNPCs().containsValue(faction) || factory.getNPC("faction-" + island.getId(), true) != null) return;

        SkyNPC npc = factory.create(
                "faction-" + island.getId(),
                TextUtility.legacyColor(Settings.NPC_FACTION_ISLANDS_NAME.getString().replace("faction_name", faction.getName()), faction.getLocale(), null),
                getOffsetLocation(island.getCenter(Settings.ISLAND_FACTION_WORLD.getString()), Settings.NPC_FACTION_ISLANDS_OFFSET.getIntegerList()),
                Settings.NPC_FACTION_ISLANDS_SKIN.getString().replace("faction_owner", faction.getOwner().getName()),
                EntityType.valueOf(Settings.NPC_FACTION_ISLANDS_ENTITY.getString()),
                true
        );

        getFactionNPCs().put(npc, faction.getName());
    }

    @Override
    public void process(List<String> actions, SkyUser player) {
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());
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
                    player.sendMessage(TextUtility.color(cmd, locale, player));
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

    @Override
    public SkyNPCFactory getFactory() {
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
                SkyFactionsReborn.getInstance().disable();
        }

        return null;
    }

    @Override
    public CompletableFuture<Integer> updateNPCs(boolean remove) {
        return CompletableFuture.supplyAsync(() -> {
            AtomicInteger affected = new AtomicInteger(0);
            if (remove && Settings.NPC_INTEGRATION_ENABLED.getBoolean()) {
                throw new RuntimeException("Attempted to disable NPCs when the integration is still enabled in the config!");
            } else if (!remove && !Settings.NPC_INTEGRATION_ENABLED.getBoolean()) {
                throw new RuntimeException("Attempted to reload NPCs when the integration is not enabled in the config!");
            }

            IntStream.range(0, SkyApi.getInstance().getDatabaseManager().getPlayerIslandManager().cachedPlayerIslandID)
                    .forEach(i -> {
                        SkyNPC npc = factory.getNPC("player-" + i, false);
                        if (npc == null) return;
                        if (remove) {
                            npc.remove();
                            affected.incrementAndGet();
                            return;
                        }
                        SkyApi.getInstance().getDatabaseManager().getPlayerIslandManager().getOwnerOfIsland(new PlayerIsland(i))
                                .whenComplete((uuid, ex) -> {
                                    if (ex != null) {
                                        SLogger.fatal("Failed to get owner of island [{}] in order to refresh their NPC.", i);
                                        ex.printStackTrace();
                                        return;
                                    }
                                    OfflinePlayer owner = Bukkit.getOfflinePlayer(uuid);
                                    npc.updateDisplayName(TextUtility.legacyColor(Settings.NPC_PLAYER_ISLANDS_NAME.getString().replace("player_name", owner.getName()), SkyApi.getInstance().getPlayerAPI().getLocale(uuid), SkyApi.getInstance().getUserManager().getUser(owner.getUniqueId())));
                                    npc.updateEntityType(Settings.NPC_PLAYER_ISLANDS_ENTITY.getString());
                                    npc.updateSkin(Settings.NPC_PLAYER_ISLANDS_SKIN.toString().replace("player_name", owner.getName()));
                                    affected.incrementAndGet();
                                });
                    });

            IntStream.range(0, SkyApi.getInstance().getDatabaseManager().getFactionIslandManager().cachedFactionIslandID)
                    .forEach(i -> {
                        SkyNPC npc = factory.getNPC("faction-" + i, true);
                        if (npc == null) return;
                        if (remove) {
                            npc.remove();
                            affected.incrementAndGet();
                            return;
                        }
                        SkyApi.getInstance().getDatabaseManager().getFactionsManager().getFactionByIslandID(i)
                                .whenComplete((faction, ex) -> {
                                    if (ex != null) {
                                        SLogger.fatal("Failed to get faction owner of island [{}] in order to refresh their NPCs.", i);
                                        ex.printStackTrace();
                                        return;
                                    }
                                    npc.updateDisplayName(Settings.NPC_FACTION_ISLANDS_NAME.getString().replace("faction_name", faction.getName()));
                                    npc.updateEntityType(Settings.NPC_FACTION_ISLANDS_ENTITY.getString());
                                    npc.updateSkin(Settings.NPC_FACTION_ISLANDS_SKIN.getString().replace("faction_owner", faction.getOwner().getName()));
                                    affected.incrementAndGet();
                                });
                    });

            return affected.get();
        });
    }
}
