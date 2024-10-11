package net.skullian.skyfactions.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeff_media.customblockdata.CustomBlockData;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.IslandAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.defence.Defence;
import net.skullian.skyfactions.defence.DefencesFactory;
import net.skullian.skyfactions.defence.struct.DefenceData;
import net.skullian.skyfactions.defence.struct.DefenceStruct;
import net.skullian.skyfactions.util.ErrorHandler;
import net.skullian.skyfactions.util.SLogger;
import net.skullian.skyfactions.util.hologram.TextHologram;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class DefenceHandler implements Listener {
    public static final Map<String, List<Defence>> loadedFactionDefences = new HashMap<>();
    public static final Map<UUID, List<Defence>> loadedPlayerDefences = new HashMap<>();

    public static final Map<String, TextHologram> hologramsMap = new ConcurrentHashMap<>();

    @EventHandler
    public void onDefencePlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        ItemStack stack = event.getItemInHand();
        NamespacedKey defenceKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-identifier");

        PersistentDataContainer container = stack.getItemMeta().getPersistentDataContainer();
        if (container.has(defenceKey, PersistentDataType.STRING)) {
            String defenceIdentifier = container.get(defenceKey, PersistentDataType.STRING);

            DefenceStruct defence = DefencesFactory.defences.get(defenceIdentifier);
            if (defence != null) {
                Block placed = event.getBlockPlaced();
                Location belowLoc = placed.getLocation().clone();
                belowLoc.setY(belowLoc.getY() - 1);

                Block belowBlock = placed.getWorld().getBlockAt(belowLoc);
                if (!isAllowedBlock(belowBlock, defence)) {
                    event.setCancelled(true);
                    player.sendMessage(TextUtility.color(defence.getPLACEMENT_BLOCKED_MESSAGE().replace("%server_name%", Messages.SERVER_NAME.getString())));
                    return;
                }

                returnOwnerDependingOnLocation(placed.getLocation(), player).whenComplete((owner, ex) -> {
                    try {
                        if (ex != null) {
                            ErrorHandler.handleError(player, "place your defence", "SQL_FACTION_GET", ex);
                            return;
                        } else if (owner == null) {
                            event.setCancelled(true);
                            return;
                        }


                        boolean isFaction = isFaction(owner);
                        DefenceData data = new DefenceData(1, defenceIdentifier, 0, placed.getLocation(), owner, isFaction);
                        ObjectMapper mapper = new ObjectMapper();

                        PersistentDataContainer blockContainer = new CustomBlockData(placed, SkyFactionsReborn.getInstance());
                        NamespacedKey dataKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-data");

                        blockContainer.set(defenceKey, PersistentDataType.STRING, defenceIdentifier);
                        blockContainer.set(dataKey, PersistentDataType.STRING, mapper.writeValueAsString(data));

                        Class<? extends Defence> defenceClass = DefencesFactory.defenceTypes.get(defenceIdentifier);
                        Defence instance = defenceClass.getDeclaredConstructor().newInstance(data);

                        instance.enable();
                        addIntoMap(owner, isFaction, instance);
                    } catch (Exception error) {
                        event.setCancelled(true);
                        ErrorHandler.handleError(event.getPlayer(), "place your defence", "DEFENCE_PROCESSING_EXCEPTION", error);
                    }
                });
            } else {
                event.setCancelled(true);
                ErrorHandler.handleError(player, "place your defence", "DEFENCE_PROCESSING_EXCEPTION", new IllegalArgumentException("Failed to find defence with the name of " + defenceIdentifier));
            }
        }
    }

    private void addIntoMap(String owner, boolean isFaction, Defence defence) {
        if (isFaction) {
            if (!loadedFactionDefences.containsKey(owner)) {
                loadedFactionDefences.put(owner, List.of(defence));
            } else {
                List<Defence> defences = loadedFactionDefences.get(owner);
                defences.add(defence);
                loadedFactionDefences.replace(owner, defences);
            }
        } else {
            UUID playeruuid = UUID.fromString(owner);
            if (!loadedPlayerDefences.containsKey(playeruuid)) {
                loadedPlayerDefences.put(playeruuid, List.of(defence));
            } else {
                List<Defence> defences = loadedPlayerDefences.get(owner);
                defences.add(defence);
                loadedPlayerDefences.replace(playeruuid, defences);
            }
        }
    }

    private boolean isFaction(String uuid) {
        try {
            UUID.fromString(uuid);
            return false; // is player uuid
        } catch (Exception ignored) {
            return true; // is false
        }
    }

    private CompletableFuture<String> returnOwnerDependingOnLocation(Location location, Player player) {
        if (location.getWorld().getName().equals(Settings.ISLAND_PLAYER_WORLD.getString()) && FactionAPI.isLocationInRegion(location, player.getUniqueId().toString())) {
            return CompletableFuture.completedFuture(player.getUniqueId().toString());
        } else if (location.getWorld().getName().equals(Settings.ISLAND_FACTION_WORLD.getString())) {
            return FactionAPI.getFaction(player).thenApply((faction) -> {
                if (faction != null && FactionAPI.isLocationInRegion(location, faction.getName())) {
                    return faction.getName();
                } else return null;
            });
        }

        return null;
    }

    @EventHandler
    public void onDefenceBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        NamespacedKey defenceKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-identifier");

        PersistentDataContainer container = new CustomBlockData(block, SkyFactionsReborn.getInstance());
        if (container.has(defenceKey, PersistentDataType.STRING)) {
            event.setCancelled(true);
            Messages.DEFENCE_DESTROY_DENY.send(event.getPlayer());
        }
    }

    private boolean isAllowedBlock(Block block, DefenceStruct defenceStruct) {
        boolean isWhitelist = defenceStruct.isIS_WHITELIST();
        if (isWhitelist) return defenceStruct.getPLACEMENT_LIST().contains(block.getType());
        else return !defenceStruct.getPLACEMENT_LIST().contains(block.getType());
    }

    public static void addPlacedDefences(String factionName) {
        if (loadedFactionDefences.get(factionName) != null) return;
        SkyFactionsReborn.db.getDefenceLocations(factionName).whenComplete((locations, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }

            List<Defence> defences = new ArrayList<>();
            for (Location location : locations) {
                Block block = location.getBlock();
                if (block == null) continue;

                NamespacedKey defenceKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-identifier");
                NamespacedKey dataKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-data");
                PersistentDataContainer container = new CustomBlockData(block, SkyFactionsReborn.getInstance());
                if (container.has(defenceKey, PersistentDataType.STRING)) {
                    String name = container.get(defenceKey, PersistentDataType.STRING);
                    String data = container.get(dataKey, PersistentDataType.STRING);
                    DefenceStruct defence = DefencesFactory.defences.get(name);

                    try {
                        if (defence != null) {
                            ObjectMapper mapper = new ObjectMapper();
                            DefenceData defenceData = mapper.readValue(data, DefenceData.class);

                            Class<? extends Defence> defenceClass = DefencesFactory.defenceTypes.get(name);
                            Defence instance = defenceClass.getDeclaredConstructor().newInstance(defenceData);

                            instance.enable();
                            defences.add(instance);
                        } else SLogger.fatal("Failed to find defence with the name of " + name);
                    } catch (Exception error) {
                        error.printStackTrace();
                    }
                }
            }

            loadedFactionDefences.put(factionName, defences);
        });
    }

    public static void addPlacedDefences(Player player) {
        if (loadedPlayerDefences.get(player.getUniqueId()) != null) return;
        SkyFactionsReborn.db.getDefenceLocations(player.getUniqueId()).whenComplete((locations, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }

            List<Defence> defences = new ArrayList<>();
            for (Location location : locations) {
                Block block = location.getBlock();
                if (block == null) continue;

                NamespacedKey defenceKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-identifier");
                NamespacedKey dataKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-data");
                PersistentDataContainer container = new CustomBlockData(block, SkyFactionsReborn.getInstance());
                if (container.has(defenceKey, PersistentDataType.STRING)) {
                    String name = container.get(defenceKey, PersistentDataType.STRING);
                    String data = container.get(dataKey, PersistentDataType.STRING);
                    DefenceStruct defence = DefencesFactory.defences.get(name);

                    try {
                        if (defence != null) {
                            ObjectMapper mapper = new ObjectMapper();
                            DefenceData defenceData = mapper.readValue(data, DefenceData.class);

                            Class<? extends Defence> defenceClass = DefencesFactory.defenceTypes.get(name);
                            Defence instance = defenceClass.getDeclaredConstructor().newInstance(defenceData);

                            instance.enable();
                            defences.add(instance);
                        } else SLogger.fatal("Failed to find defence with the name of " + name);
                    } catch (Exception error) {
                        error.printStackTrace();
                    }
                }
            }

            loadedPlayerDefences.put(player.getUniqueId(), defences);
        });
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        String fromWorldName = event.getFrom().getWorld().getName();
        String toWorldName = event.getTo().getWorld().getName();
        boolean fromWasFaction = fromWorldName.equals(Settings.ISLAND_FACTION_WORLD.getString());
        boolean fromWasPlayer = fromWorldName.equals(Settings.ISLAND_PLAYER_WORLD.getString());
        boolean toWasFaction = toWorldName.equals(Settings.ISLAND_FACTION_WORLD.getString());
        boolean toWasPlayer = toWorldName.equals(Settings.ISLAND_PLAYER_WORLD.getString());

        if (fromWasFaction) {
            FactionAPI.getFaction(player).whenComplete((faction, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    return;
                } else if (faction == null) return;
                else if (!FactionAPI.isLocationInRegion(event.getFrom(), faction.getName())) return;

                FactionAPI.disableDefencesOnExit(faction.getName());
            });
        } else if (toWasFaction) {
            FactionAPI.getFaction(player).whenComplete((faction, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    return;
                } else if (faction == null) return;
                else if (!FactionAPI.isLocationInRegion(event.getTo(), faction.getName())) return;

                FactionAPI.enableDefencesOnEntry(faction.getName());
            });
        } else if (fromWasPlayer) {
            IslandAPI.getPlayerIsland(player.getUniqueId()).whenComplete((island, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    return;
                } else if (island == null) return;
                else if (!FactionAPI.isLocationInRegion(event.getFrom(), player.getUniqueId().toString())) return;

                IslandAPI.disableDefencesOnExit(player);
            });
        } else if (toWasFaction) {
            IslandAPI.getPlayerIsland(player.getUniqueId()).whenComplete((island, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    return;
                } else if (island == null) return;
                else if (!FactionAPI.isLocationInRegion(event.getTo(), player.getUniqueId().toString())) return;

                IslandAPI.enableDefencesOnEntry(player);
            });
        }
    }
}
